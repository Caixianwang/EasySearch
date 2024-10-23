package ca.wisecode.lucene.slave.grpc.server.query.mode;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.model.QueryLogic;
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
    protected String currMode;
    protected IWrapQuery nextQuery;


    protected AbstractQuery(String currMode) {
        this.currMode = currMode;
    }

    @Override
    public void setNextQuery(IWrapQuery nextQuery) {
        this.nextQuery = nextQuery;
    }

    @Override
    public void build(BooleanQuery.Builder builder,FilterRule filterRule) {
        log.info(filterRule.getQueryMode());
        if (filterRule.getQueryMode().equals(currMode)) {
            log.info(this.getClass().getSimpleName());
            Query query = this.buildQuery(filterRule);
            this.logic(builder,query, filterRule.getLogic());
        } else if (nextQuery != null) {
            nextQuery.build(builder,filterRule);
        } else {
            throw new BusinessException("没有处理的查询模式");
        }
    }


    protected abstract Query buildQuery(FilterRule filterRule);

    private void logic(BooleanQuery.Builder builder,Query query, String logic) {
        switch (logic) {
            case QueryLogic.OR:
                builder.add(query, BooleanClause.Occur.SHOULD);
                break;
            case QueryLogic.NOT:
                builder.add(query, BooleanClause.Occur.MUST_NOT);
                break;
            default:
                builder.add(query, BooleanClause.Occur.MUST);
                break;
        }
    }
}
