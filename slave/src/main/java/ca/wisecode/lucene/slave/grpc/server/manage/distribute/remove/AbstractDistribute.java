package ca.wisecode.lucene.slave.grpc.server.manage.distribute.remove;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.slave.grpc.client.service.ProjectService;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.FetchSample;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.QuantitySplitStrategy;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.Transfer;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance.DestNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/5/2024 1:30 PM
 * @Version: 1.0
 * @description:
 */

@Slf4j
public abstract class AbstractDistribute {

    @Autowired
    protected SearchManager searchManager;
    @Autowired
    protected ProjectService projectService;
    @Autowired
    protected IndexWriter indexWriter;

    protected abstract float samplepPercent(DistributeRequest distributeRequest);

    protected abstract QuantitySplitStrategy quantityStrategy(DistributeRequest distributeRequest);

    public void distribute(DistributeRequest distributeRequest) {
        try {
            float percent = samplepPercent(distributeRequest);
            QuantitySplitStrategy quantityStrategy = quantityStrategy(distributeRequest);
            FetchSample fetchSample = new FetchSample();
            IndexSearcher searcher = searchManager.getSearcher();
            Transfer transfer = new Transfer(indexWriter, projectService);
            List<ScoreDoc> scoreDocs = fetchSample.pagedSearch(searcher, percent);
            while (!scoreDocs.isEmpty()) {
                log.info("scoreDocs.size=" + scoreDocs.size());
                List<DestNode> destNodes = quantityStrategy.buildDestination(distributeRequest, scoreDocs);
                transfer.sendDestination(searcher, destNodes);
                if (percent > 0.9999) {
                    searcher = searchManager.getSearcher();
                }
                scoreDocs = fetchSample.pagedSearch(searcher, percent);
            }
        } catch (IOException e) {
            throw new BusinessException(e);
        }

    }


}
