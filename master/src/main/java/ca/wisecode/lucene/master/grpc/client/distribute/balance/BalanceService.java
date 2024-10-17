package ca.wisecode.lucene.master.grpc.client.distribute.balance;

import ca.wisecode.lucene.master.grpc.client.distribute.Intercept;
import ca.wisecode.lucene.master.grpc.client.distribute.vo.BalanceNode;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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
    @Autowired
    private ReentrantLock lock;

    private final NodeDocument nodeDocument = new NodeDocument();
    private final AvgCalculator avgCalculator = new AvgCalculator();
    private final NodeSelector nodeSelector = new NodeSelector();
    private final Balancer balancer = new Balancer(new DefaultBalancer());


    public void balance() {

        try {
            lock.lock();
            boolean valid = Intercept.isValid(masterNode);
            if (valid) {
                log.info("balance start...");
                List<BalanceNode> balanceNodeList = nodeDocument.calculateNodeDocs(masterNode);
                if (!balanceNodeList.isEmpty()) {
                    int avg = avgCalculator.calculateAverage(balanceNodeList);
                    List<BalanceNode> aboveNodes = nodeSelector.selectAboveAverageNodes(avg, balanceNodeList);
                    if (!aboveNodes.isEmpty()) {
                        List<BalanceNode> belowNodes = nodeSelector.selectBelowAverageNodes(avg, balanceNodeList);
                        balancer.calculatePercentDistribution(belowNodes);
                        NodeBalance distribution = new NodeBalance(masterNode);
                        distribution.distributeData(aboveNodes, belowNodes);
                    }
                }
                log.info("balance end...");
            }
        } finally {
            lock.unlock();
        }
    }
}
