package ca.wisecode.lucene.slave.grpc.server.manage.balance.dist;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.grpc.models.BalanceRequest;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 1:46 AM
 * @Version: 1.0
 * @description:
 */

public abstract class AbstractDistribution {

    protected SearchManager searchManager;

    public AbstractDistribution(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public void distribute(BalanceRequest balanceRequest, DistributionStrategy strategy, IndexCommand command) {
        int distNums = strategy.distributeNums(balanceRequest);
        float percent = strategy.fetchPercent(distNums, searchManager.getReader().numDocs());
        if (percent > 0) {
            FetchSample fetchSample = new FetchSample();
            try {
                IndexSearcher searcher = searchManager.getSearcher();
                List<ScoreDoc> scoreDocs = fetchSample.pagedSearch(searcher, 5000, percent);
                while (!scoreDocs.isEmpty()) {
                    List<DestNode> destNodes = buildDestination(distNums, balanceRequest, scoreDocs);
                    command.execute(searcher, destNodes);
                    scoreDocs = fetchSample.pagedSearch(searcher, 5000, percent);
                }
            } catch (IOException e) {
                throw new BusinessException(e);
            }
        }
    }

    protected abstract List<DestNode> buildDestination(int distNums, BalanceRequest balanceRequest, List<ScoreDoc> scoreDocs);
}
