package ca.wisecode.lucene.master.grpc.client.index.balance.dist;

import ca.wisecode.lucene.master.grpc.client.index.balance.vo.BalanceNode;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/2/2024 12:20 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class BalanceService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MasterNode masterNode;

    private final NodeDocument nodeDocument = new NodeDocument();
    private final AvgCalculator avgCalculator = new AvgCalculator();
    private final NodeSelector nodeSelector = new NodeSelector();
    private final Balancer balancer = new Balancer(new DefaultBalancer());
    private final NodeDistribution distribution = new NodeDistribution();

    public void balance() {
        List<BalanceNode> balanceNodeList = nodeDocument.calculateNodeDocs(masterNode);

        if (!balanceNodeList.isEmpty()) {
            int avg = avgCalculator.calculateAverage(balanceNodeList);
            List<BalanceNode> aboveNodes = nodeSelector.selectAboveAverageNodes(avg, balanceNodeList);

            if (!aboveNodes.isEmpty()) {
                List<BalanceNode> belowNodes = nodeSelector.selectBelowAverageNodes(avg, balanceNodeList);
                balancer.calculatePercentDistribution(belowNodes);
                distribution.distributeData(masterNode, aboveNodes, belowNodes);
            }
        }
    }


}
