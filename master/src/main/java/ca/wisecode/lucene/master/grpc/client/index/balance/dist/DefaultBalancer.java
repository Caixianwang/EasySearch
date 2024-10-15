package ca.wisecode.lucene.master.grpc.client.index.balance.dist;

import ca.wisecode.lucene.master.grpc.client.index.balance.vo.BalanceNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/7/2024 11:18 PM
 * @Version: 1.0
 * @description:  负责计算低于平均数节点需要接收的比例
 */

public class DefaultBalancer implements BalancerStrategy {
    @Override
    public void calculatePercentDistribution(List<BalanceNode> belowNodes) {
        AtomicInteger totalDiff = new AtomicInteger();
        belowNodes.forEach(node -> totalDiff.addAndGet(Math.abs(node.getTotal() - node.getAvg())));

        belowNodes.forEach(node -> {
            node.setPercent((float) Math.abs(node.getTotal() - node.getAvg()) / totalDiff.get());
        });
    }
}
