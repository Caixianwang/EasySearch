package ca.wisecode.lucene.slave.grpc.server.query.mode;

import ca.wisecode.lucene.common.model.QueryMode;
import ca.wisecode.lucene.grpc.models.FilterRule;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/20/2024 2:00 PM
 * @Version: 1.0
 * @description:
 */

public class TermWrapQuery extends AbstractQuery {

    protected TermWrapQuery() {
        super(QueryMode.Term);
    }

    @Override
    public Query buildQuery(FilterRule filterRule) {
        return new TermQuery(new Term(filterRule.getName(), filterRule.getValue()));
    }

}
