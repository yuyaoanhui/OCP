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

package org.elasticsearch.index.query;

import org.apache.lucene.search.Query;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.DeprecationLogger;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.lucene.search.Queries;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;

@Deprecated
public class LimitQueryParser implements QueryParser {

    public static final String NAME = "limit";
    private final DeprecationLogger deprecationLogger;

    @Inject
    public LimitQueryParser() {
        ESLogger logger = Loggers.getLogger(getClass());
        deprecationLogger = new DeprecationLogger(logger);
    }

    @Override
    public String[] names() {
        return new String[]{NAME};
    }

    @Override
    public Query parse(QueryParseContext parseContext) throws IOException, QueryParsingException {
        deprecationLogger.deprecated("The [limit] query is deprecated, please use the [terminate_after] parameter of the search API instead.");

        XContentParser parser = parseContext.parser();

        int limit = -1;
        String currentFieldName = null;
        XContentParser.Token token;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token.isValue()) {
                if ("value".equals(currentFieldName)) {
                    limit = parser.intValue();
                } else {
                    throw new QueryParsingException(parseContext, "[limit] query does not support [" + currentFieldName + "]");
                }
            }
        }

        if (limit == -1) {
            throw new QueryParsingException(parseContext, "No value specified for limit query");
        }

        // this filter is deprecated and parses to a filter that matches everything
        return Queries.newMatchAllQuery();
    }
}
