/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.operation;

import org.mongodb.Document;
import org.mongodb.MongoFuture;
import org.mongodb.MongoNamespace;
import org.mongodb.codecs.DocumentCodec;
import org.mongodb.protocol.QueryProtocol;
import org.mongodb.protocol.QueryResult;
import org.mongodb.session.Session;

import java.util.EnumSet;
import java.util.List;

import static org.mongodb.assertions.Assertions.notNull;
import static org.mongodb.operation.OperationHelper.executeProtocol;
import static org.mongodb.operation.OperationHelper.executeProtocolAsync;
import static org.mongodb.operation.OperationHelper.queryResultToList;
import static org.mongodb.operation.OperationHelper.queryResultToListAsync;

public class GetIndexesOperation implements AsyncOperation<List<Document>>, Operation<List<Document>> {
    private final MongoNamespace collectionNamespace;

    public GetIndexesOperation(final MongoNamespace collectionNamespace) {
        this.collectionNamespace = notNull("collectionNamespace", collectionNamespace);
    }

    @Override
    public List<Document> execute(final Session session) {
        QueryResult<Document> queryResult = executeProtocol(getProtocol(), session);
        return queryResultToList(queryResult, session, getIndexNamespace(), new DocumentCodec());
    }

    @Override
    public MongoFuture<List<Document>> executeAsync(final Session session) {
        final MongoFuture<QueryResult<Document>> queryResult = executeProtocolAsync(getProtocol(), session);
        return queryResultToListAsync(queryResult, session, getIndexNamespace(), new DocumentCodec());
    }

    private Document asQueryDocument() {
        return new Document("ns", collectionNamespace.getFullName());
    }

    private MongoNamespace getIndexNamespace() {
        return new MongoNamespace(collectionNamespace.getDatabaseName(), "system.indexes");
    }

    private QueryProtocol<Document> getProtocol() {
        return new QueryProtocol<Document>(getIndexNamespace(), EnumSet.noneOf(QueryFlag.class), 0, 0, asQueryDocument(),
                null, new DocumentCodec(), new DocumentCodec());
    }

}
