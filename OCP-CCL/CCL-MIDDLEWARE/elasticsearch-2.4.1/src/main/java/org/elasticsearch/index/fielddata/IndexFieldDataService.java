/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.fielddata;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.apache.lucene.util.Accountable;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.Version;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.AbstractIndexComponent;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.fielddata.plain.AbstractGeoPointDVIndexFieldData;
import org.elasticsearch.index.fielddata.plain.BytesBinaryDVIndexFieldData;
import org.elasticsearch.index.fielddata.plain.DocValuesIndexFieldData;
import org.elasticsearch.index.fielddata.plain.DoubleArrayIndexFieldData;
import org.elasticsearch.index.fielddata.plain.FloatArrayIndexFieldData;
import org.elasticsearch.index.fielddata.plain.GeoPointArrayIndexFieldData;
import org.elasticsearch.index.fielddata.plain.IndexIndexFieldData;
import org.elasticsearch.index.fielddata.plain.PackedArrayIndexFieldData;
import org.elasticsearch.index.fielddata.plain.PagedBytesIndexFieldData;
import org.elasticsearch.index.fielddata.plain.ParentChildIndexFieldData;
import org.elasticsearch.index.mapper.MappedFieldType;
import org.elasticsearch.index.mapper.MappedFieldType.Names;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.mapper.core.BooleanFieldMapper;
import org.elasticsearch.index.mapper.internal.IndexFieldMapper;
import org.elasticsearch.index.mapper.internal.ParentFieldMapper;
import org.elasticsearch.index.settings.IndexSettingsService;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.indices.breaker.CircuitBreakerService;
import org.elasticsearch.indices.fielddata.cache.IndicesFieldDataCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IndexFieldDataService extends AbstractIndexComponent {

    public static final String FIELDDATA_CACHE_KEY = "index.fielddata.cache";
    public static final String FIELDDATA_CACHE_VALUE_NODE = "node";

    private static final String DISABLED_FORMAT = "disabled";
    private static final String DOC_VALUES_FORMAT = "doc_values";
    private static final String ARRAY_FORMAT = "array";
    private static final String PAGED_BYTES_FORMAT = "paged_bytes";

    private static final IndexFieldData.Builder DISABLED_BUILDER = new IndexFieldData.Builder() {
        @Override
        public IndexFieldData<?> build(Index index, Settings indexSettings, MappedFieldType fieldType, IndexFieldDataCache cache,
                CircuitBreakerService breakerService, MapperService mapperService) {
            throw new IllegalStateException("Field data loading is forbidden on [" + fieldType.names().fullName() + "]");
        }
    };

    private final static ImmutableMap<String, IndexFieldData.Builder> buildersByType;
    private final static ImmutableMap<String, IndexFieldData.Builder> docValuesBuildersByType;
    private final static ImmutableMap<Tuple<String, String>, IndexFieldData.Builder> buildersByTypeAndFormat;
    private final CircuitBreakerService circuitBreakerService;

    static {
        buildersByType = MapBuilder.<String, IndexFieldData.Builder>newMapBuilder()
                .put("string", new PagedBytesIndexFieldData.Builder())
                .put("float", new FloatArrayIndexFieldData.Builder())
                .put("double", new DoubleArrayIndexFieldData.Builder())
                .put("byte", new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.BYTE))
                .put("short", new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.SHORT))
                .put("int", new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.INT))
                .put("long", new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.LONG))
                .put("geo_point", new GeoPointArrayIndexFieldData.Builder())
                .put(ParentFieldMapper.NAME, new ParentChildIndexFieldData.Builder())
                .put(IndexFieldMapper.NAME, new IndexIndexFieldData.Builder())
                .put("binary", DISABLED_BUILDER)
                .put(BooleanFieldMapper.CONTENT_TYPE, new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.BOOLEAN))
                .immutableMap();

        docValuesBuildersByType = MapBuilder.<String, IndexFieldData.Builder>newMapBuilder()
                .put("string", new DocValuesIndexFieldData.Builder())
                .put("float", new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.FLOAT))
                .put("double", new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.DOUBLE))
                .put("byte", new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.BYTE))
                .put("short", new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.SHORT))
                .put("int", new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.INT))
                .put("long", new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.LONG))
                .put("geo_point", new AbstractGeoPointDVIndexFieldData.Builder())
                .put("binary", new BytesBinaryDVIndexFieldData.Builder())
                .put(BooleanFieldMapper.CONTENT_TYPE, new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.BOOLEAN))
                .immutableMap();

        buildersByTypeAndFormat = MapBuilder.<Tuple<String, String>, IndexFieldData.Builder>newMapBuilder()
                .put(Tuple.tuple("string", PAGED_BYTES_FORMAT), new PagedBytesIndexFieldData.Builder())
                .put(Tuple.tuple("string", DOC_VALUES_FORMAT), new DocValuesIndexFieldData.Builder())
                .put(Tuple.tuple("string", DISABLED_FORMAT), DISABLED_BUILDER)

                .put(Tuple.tuple("float", ARRAY_FORMAT), new FloatArrayIndexFieldData.Builder())
                .put(Tuple.tuple("float", DOC_VALUES_FORMAT), new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.FLOAT))
                .put(Tuple.tuple("float", DISABLED_FORMAT), DISABLED_BUILDER)

                .put(Tuple.tuple("double", ARRAY_FORMAT), new DoubleArrayIndexFieldData.Builder())
                .put(Tuple.tuple("double", DOC_VALUES_FORMAT), new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.DOUBLE))
                .put(Tuple.tuple("double", DISABLED_FORMAT), DISABLED_BUILDER)

                .put(Tuple.tuple("byte", ARRAY_FORMAT), new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.BYTE))
                .put(Tuple.tuple("byte", DOC_VALUES_FORMAT), new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.BYTE))
                .put(Tuple.tuple("byte", DISABLED_FORMAT), DISABLED_BUILDER)

                .put(Tuple.tuple("short", ARRAY_FORMAT), new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.SHORT))
                .put(Tuple.tuple("short", DOC_VALUES_FORMAT), new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.SHORT))
                .put(Tuple.tuple("short", DISABLED_FORMAT), DISABLED_BUILDER)

                .put(Tuple.tuple("int", ARRAY_FORMAT), new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.INT))
                .put(Tuple.tuple("int", DOC_VALUES_FORMAT), new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.INT))
                .put(Tuple.tuple("int", DISABLED_FORMAT), DISABLED_BUILDER)

                .put(Tuple.tuple("long", ARRAY_FORMAT), new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.LONG))
                .put(Tuple.tuple("long", DOC_VALUES_FORMAT), new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.LONG))
                .put(Tuple.tuple("long", DISABLED_FORMAT), DISABLED_BUILDER)

                .put(Tuple.tuple("geo_point", ARRAY_FORMAT), new GeoPointArrayIndexFieldData.Builder())
                .put(Tuple.tuple("geo_point", DOC_VALUES_FORMAT), new AbstractGeoPointDVIndexFieldData.Builder())
                .put(Tuple.tuple("geo_point", DISABLED_FORMAT), DISABLED_BUILDER)

                .put(Tuple.tuple("binary", DOC_VALUES_FORMAT), new BytesBinaryDVIndexFieldData.Builder())
                .put(Tuple.tuple("binary", DISABLED_FORMAT), DISABLED_BUILDER)

                .put(Tuple.tuple(BooleanFieldMapper.CONTENT_TYPE, ARRAY_FORMAT), new PackedArrayIndexFieldData.Builder().setNumericType(IndexNumericFieldData.NumericType.BOOLEAN))
                .put(Tuple.tuple(BooleanFieldMapper.CONTENT_TYPE, DOC_VALUES_FORMAT), new DocValuesIndexFieldData.Builder().numericType(IndexNumericFieldData.NumericType.BOOLEAN))
                .put(Tuple.tuple(BooleanFieldMapper.CONTENT_TYPE, DISABLED_FORMAT), DISABLED_BUILDER)

                .immutableMap();
    }

    private final IndicesFieldDataCache indicesFieldDataCache;
    // the below map needs to be modified under a lock
    private final Map<String, IndexFieldDataCache> fieldDataCaches = Maps.newHashMap();
    private final MapperService mapperService;
    private static final IndexFieldDataCache.Listener DEFAULT_NOOP_LISTENER = new IndexFieldDataCache.Listener() {
        @Override
        public void onCache(ShardId shardId, Names fieldNames, FieldDataType fieldDataType, Accountable ramUsage) {
        }

        @Override
        public void onRemoval(ShardId shardId, Names fieldNames, FieldDataType fieldDataType, boolean wasEvicted, long sizeInBytes) {
        }
    };
    private volatile IndexFieldDataCache.Listener listener = DEFAULT_NOOP_LISTENER;


    // We need to cache fielddata on the _parent field because of 1.x indices.
    // When we don't support 1.x anymore (3.0) then remove this caching
    // This variable needs to be read/written under lock
    private IndexFieldData<?> parentIndexFieldData;

    @Inject
    public IndexFieldDataService(Index index, IndexSettingsService indexSettingsService, IndicesFieldDataCache indicesFieldDataCache,
                                 CircuitBreakerService circuitBreakerService, MapperService mapperService) {
        super(index, indexSettingsService.getSettings());
        this.indicesFieldDataCache = indicesFieldDataCache;
        this.circuitBreakerService = circuitBreakerService;
        this.mapperService = mapperService;
    }

    public synchronized void clear() {
        parentIndexFieldData = null;
        List<Throwable> exceptions = new ArrayList<>(0);
        final Collection<IndexFieldDataCache> fieldDataCacheValues = fieldDataCaches.values();
        for (IndexFieldDataCache cache : fieldDataCacheValues) {
            try {
                cache.clear();
            } catch (Throwable t) {
                exceptions.add(t);
            }
        }
        fieldDataCacheValues.clear();
        ExceptionsHelper.maybeThrowRuntimeAndSuppress(exceptions);
    }

    public synchronized void clearField(final String fieldName) {
        if (ParentFieldMapper.NAME.equals(fieldName)) {
            parentIndexFieldData = null;
        }
        List<Throwable> exceptions = new ArrayList<>(0);
        final IndexFieldDataCache cache = fieldDataCaches.remove(fieldName);
        if (cache != null) {
            try {
                cache.clear();
            } catch (Throwable t) {
                exceptions.add(t);
            }
        }
        ExceptionsHelper.maybeThrowRuntimeAndSuppress(exceptions);
    }

    @SuppressWarnings("unchecked")
    public <IFD extends IndexFieldData<?>> IFD getForField(MappedFieldType fieldType) {
        final Names fieldNames = fieldType.names();
        final FieldDataType type = fieldType.fieldDataType();
        if (type == null) {
            throw new IllegalArgumentException("found no fielddata type for field [" + fieldNames.fullName() + "]");
        }
        final boolean docValues = fieldType.hasDocValues();
        IndexFieldData.Builder builder = null;
        Settings indexSettings = indexSettings();
        String format = type.getFormat(indexSettings);
        if (format != null && FieldDataType.DOC_VALUES_FORMAT_VALUE.equals(format) && !docValues) {
            logger.warn("field [" + fieldNames.fullName() + "] has no doc values, will use default field data format");
            format = null;
        }
        if (format != null) {
            builder = buildersByTypeAndFormat.get(Tuple.tuple(type.getType(), format));
            if (builder == null) {
                logger.warn("failed to find format [" + format + "] for field [" + fieldNames.fullName() + "], will use default");
            }
        }
        if (builder == null && docValues) {
            builder = docValuesBuildersByType.get(type.getType());
        }
        if (builder == null) {
            builder = buildersByType.get(type.getType());
        }
        if (builder == null) {
            throw new IllegalArgumentException("failed to find field data builder for field " + fieldNames.fullName() + ", and type " + type.getType());
        }

        IndexFieldDataCache cache;
        synchronized (this) {
            cache = fieldDataCaches.get(fieldNames.indexName());
            if (cache == null) {
                //  we default to node level cache, which in turn defaults to be unbounded
                // this means changing the node level settings is simple, just set the bounds there
                String cacheType = type.getSettings().get("cache", indexSettings.get(FIELDDATA_CACHE_KEY, FIELDDATA_CACHE_VALUE_NODE));
                if (FIELDDATA_CACHE_VALUE_NODE.equals(cacheType)) {
                    cache = indicesFieldDataCache.buildIndexFieldDataCache(listener, index, fieldNames, type);
                } else if ("none".equals(cacheType)){
                    cache = new IndexFieldDataCache.None();
                } else {
                    throw new IllegalArgumentException("cache type not supported [" + cacheType + "] for field [" + fieldNames.fullName() + "]");
                }
                fieldDataCaches.put(fieldNames.indexName(), cache);
            }

            // Remove this in 3.0
            final boolean isOldParentField = ParentFieldMapper.NAME.equals(fieldNames.indexName())
                    && Version.indexCreated(indexSettings).before(Version.V_2_0_0_beta1);
            if (isOldParentField) {
                if (parentIndexFieldData == null) {
                    parentIndexFieldData = builder.build(index, indexSettings, fieldType, cache, circuitBreakerService, mapperService);
                }
                return (IFD) parentIndexFieldData;
            }
        }

        return (IFD) builder.build(index, indexSettings, fieldType, cache, circuitBreakerService, mapperService);
    }

    /**
     * Sets a {@link org.elasticsearch.index.fielddata.IndexFieldDataCache.Listener} passed to each {@link IndexFieldData}
     * creation to capture onCache and onRemoval events. Setting a listener on this method will override any previously
     * set listeners.
     * @throws IllegalStateException if the listener is set more than once
     */
    public void setListener(IndexFieldDataCache.Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        if (this.listener != DEFAULT_NOOP_LISTENER) {
            throw new IllegalStateException("can't set listener more than once");
        }
        this.listener = listener;
    }

}
