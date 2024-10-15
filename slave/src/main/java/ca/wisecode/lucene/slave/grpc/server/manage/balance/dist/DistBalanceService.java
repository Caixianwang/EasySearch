package ca.wisecode.lucene.slave.grpc.server.manage.balance.dist;

import ca.wisecode.lucene.grpc.models.BalanceRequest;
import ca.wisecode.lucene.grpc.models.TargetNode;
import ca.wisecode.lucene.slave.grpc.client.service.ProjectService;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ScoreDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 1:47 AM
 * @Version: 1.0
 * @description:
 */

@Service
@Slf4j
public class DistBalanceService extends AbstractDistribution {

    @Autowired
    private IndexWriter indexWriter;
    @Autowired
    private ProjectService projectService;

    @Autowired
    public DistBalanceService(SearchManager searchManager) {
        super(searchManager);
    }

    @Override
    protected List<DestNode> buildDestination(int distNums, BalanceRequest balanceRequest, List<ScoreDoc> scoreDocs) {
        int start = 0;
        List<DestNode> destNodes = new ArrayList<>();
        for (TargetNode targetNode : balanceRequest.getTargetNodesList()) {
            DestNode destNode = new DestNode(targetNode.getHost(), targetNode.getPort());
            int len = (int) (targetNode.getCnt() / distNums * scoreDocs.size());
            if ((len + start) <= scoreDocs.size()) {
                destNode.setList(scoreDocs.subList(start, len + start));
                start += len;
            }
            destNodes.add(destNode);
        }
        return destNodes;
    }

    public void executeDistribute(BalanceRequest balanceRequest)  {
        DistributionStrategy strategy = new DefaultDistributionStrategy();
        IndexCommand command = new DistributeCommand(projectService, indexWriter);
        distribute(balanceRequest, strategy, command);
    }
}
