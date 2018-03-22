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

package org.elasticsearch.index.similarity;

import org.apache.lucene.search.similarities.PerFieldSimilarityWrapper;
import org.apache.lucene.search.similarities.Similarity;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.AbstractIndexComponent;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.mapper.MappedFieldType;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.settings.IndexSettingsService;

public class SimilarityService extends AbstractIndexComponent {
    private final SimilarityLookupService similarityLookupService;
    private final MapperService mapperService;

    private final Similarity perFieldSimilarity;

    @Inject
    public SimilarityService(Index index, IndexSettingsService indexSettingsService,
                             final SimilarityLookupService similarityLookupService, final MapperService mapperService) {
        super(index, indexSettingsService.getSettings());
        this.similarityLookupService = similarityLookupService;
        this.mapperService = mapperService;

        Similarity defaultSimilarity = similarityLookupService.similarity(SimilarityLookupService.DEFAULT_SIMILARITY).get();
        // Expert users can configure the base type as being different to default, but out-of-box we use default.
        Similarity baseSimilarity = (similarityLookupService.similarity("base") != null) ? similarityLookupService.similarity("base").get() :
                defaultSimilarity;

        this.perFieldSimilarity = (mapperService != null) ? new PerFieldSimilarity(defaultSimilarity, baseSimilarity, mapperService) :
                defaultSimilarity;
    }

    public Similarity similarity() {
        return perFieldSimilarity;
    }

    public SimilarityLookupService similarityLookupService() {
        return similarityLookupService;
    }

    public MapperService mapperService() {
        return mapperService;
    }

    static class PerFieldSimilarity extends PerFieldSimilarityWrapper {

        private final Similarity defaultSimilarity;
        private final Similarity baseSimilarity;
        private final MapperService mapperService;

        PerFieldSimilarity(Similarity defaultSimilarity, Similarity baseSimilarity, MapperService mapperService) {
            this.defaultSimilarity = defaultSimilarity;
            this.baseSimilarity = baseSimilarity;
            this.mapperService = mapperService;
        }

        @Override
        public float coord(int overlap, int maxOverlap) {
            return baseSimilarity.coord(overlap, maxOverlap);
        }

        @Override
        public float queryNorm(float valueForNormalization) {
            return baseSimilarity.queryNorm(valueForNormalization);
        }

        @Override
        public Similarity get(String name) {
            MappedFieldType fieldType = mapperService.smartNameFieldType(name);
            return (fieldType != null && fieldType.similarity() != null) ? fieldType.similarity().get() : defaultSimilarity;
        }
    }
}
