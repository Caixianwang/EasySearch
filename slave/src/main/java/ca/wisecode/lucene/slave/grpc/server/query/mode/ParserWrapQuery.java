package ca.wisecode.lucene.slave.grpc.server.query.mode;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.grpc.models.FilterRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/20/2024 1:10 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class ParserWrapQuery extends AbstractQuery {
    private final Analyzer analyzer;

    protected ParserWrapQuery(Analyzer analyzer, BooleanQuery.Builder builder) {
        super(builder, Mode.Parser);
        this.analyzer = analyzer;
    }

    @Override
    protected Query buildQuery(FilterRule filterRule) {
        try {
            // 使用 QueryParser 进行分词查询
            QueryParser queryParser = new QueryParser(filterRule.getName(), analyzer);
            Query query = queryParser.parse(filterRule.getValue());
            return query;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }
}
