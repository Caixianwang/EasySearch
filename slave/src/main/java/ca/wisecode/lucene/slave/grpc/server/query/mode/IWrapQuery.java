package ca.wisecode.lucene.slave.grpc.server.query.mode;

import ca.wisecode.lucene.grpc.models.FilterRule;
import org.apache.lucene.search.Query;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/20/2024 1:09 PM
 * @Version: 1.0
 * @description:
 */
public interface IWrapQuery {
    Query build(FilterRule filterRule);

    void setNextQuery(IWrapQuery nextQuery);
}
