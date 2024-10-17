package ca.wisecode.lucene.master.grpc.client.distribute.balance;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.grpc.models.TargetNode;
import ca.wisecode.lucene.master.grpc.client.distribute.BalanceDistribute;
import ca.wisecode.lucene.master.grpc.client.distribute.vo.BalanceNode;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/7/2024 11:19 PM
 * @Version: 1.0
 * @description: 负责数据分发
 */
@Slf4j
public class NodeBalance {
    private MasterNode masterNode;

    public NodeBalance(MasterNode masterNode) {
        this.masterNode = masterNode;
    }

    public void distributeData(List<BalanceNode> aboveNodes, List<BalanceNode> belowNodes) {
        for (BalanceNode aboveNode : aboveNodes) {
            int balance = aboveNode.getTotal() - aboveNode.getAvg();
            DistributeRequest distributeRequest = this.buildDistributeRequest(belowNodes, balance);
            this.distribute(distributeRequest, aboveNode.getHost(), aboveNode.getPort());
        }
    }

    private void distribute(DistributeRequest distributeRequest, String host, int port) {
        if (distributeRequest != null) {
            BalanceDistribute balanceDistribute = new BalanceDistribute(masterNode);
            balanceDistribute.distribute(distributeRequest, host, port);
        }
    }

    private DistributeRequest buildDistributeRequest(List<BalanceNode> belowNodes, int balance) {
        DistributeRequest.Builder builder = DistributeRequest.newBuilder();
        for (BalanceNode belowNode : belowNodes) {
            TargetNode targetNode = TargetNode.newBuilder()
                    .setHost(belowNode.getHost())
                    .setPort(belowNode.getPort())
                    .setCnt((int) (balance * belowNode.getPercent()))
                    .build();
            builder.addTargetNodes(targetNode);
        }
        if (!builder.getTargetNodesList().isEmpty()) {
            return builder.build();
        } else {
            return null;
        }
    }

}
