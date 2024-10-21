package ca.wisecode.lucene.slave.grpc.server.query.mode;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.grpc.models.FilterRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/20/2024 1:10 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public abstract class AbstractQuery implements IWrapQuery {
    protected BooleanQuery.Builder builder;
    protected String currMode;
    protected IWrapQuery nextQuery;

    public static class Mode {
        public static final String Parser = "Parser";
        public static final String Term = "Term";
    }
    public static class LOGIC {
        public static final String OR = "OR";
        public static final String NOT = "NOT";
        public static final String AND = "AND";
    }

    protected AbstractQuery(BooleanQuery.Builder builder, String currMode) {
        this.builder = builder;
        this.currMode = currMode;

    }
    @Override
    public void setNextQuery(IWrapQuery nextQuery) {
        this.nextQuery = nextQuery;
    }

    @Override
    public Query build(FilterRule filterRule) {
        if (filterRule.getQueryMode().equals(currMode)) {
            log.info(this.getClass().getSimpleName());
            Query query = this.buildQuery(filterRule);
            this.logic(query, filterRule.getLogic());
            return query;
        }
        if (nextQuery != null) {
            return nextQuery.build(filterRule);
        }
        throw new BusinessException("没有处理的查询模式");
    }


    protected abstract Query buildQuery(FilterRule filterRule);

    private void logic(Query query, String logic) {
        switch (logic) {
            case LOGIC.OR:
                builder.add(query, BooleanClause.Occur.SHOULD);
                break;
            case LOGIC.NOT:
                builder.add(query, BooleanClause.Occur.MUST_NOT);
                break;
            default:
                builder.add(query, BooleanClause.Occur.MUST);
                break;
        }
    }
}
