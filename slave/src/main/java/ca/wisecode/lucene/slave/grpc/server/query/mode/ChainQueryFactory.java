package ca.wisecode.lucene.slave.grpc.server.query.mode;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanQuery;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/20/2024 3:03 PM
 * @Version: 1.0
 * @description:
 */

public class ChainQueryFactory {

    private static ChainQueryFactory instance;

    private IWrapQuery cacheQuery;

    private ChainQueryFactory() {
    }

    public static synchronized ChainQueryFactory getInstance() {
        if (instance == null) {
            instance = new ChainQueryFactory();
        }
        return instance;
    }

    public IWrapQuery buildQuery(Analyzer analyzer, BooleanQuery.Builder builder) {
        if (this.cacheQuery == null) {
            IWrapQuery parserQuery = new ParserWrapQuery(analyzer, builder);
            IWrapQuery termWrapQuery = new TermWrapQuery(builder);
            parserQuery.setNextQuery(termWrapQuery);
            this.cacheQuery = parserQuery;
            return parserQuery;
        }
        return this.cacheQuery;

    }
}
