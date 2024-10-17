package ca.wisecode.lucene.master.grpc.client.distribute.balance;

import ca.wisecode.lucene.master.grpc.client.distribute.vo.BalanceNode;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/7/2024 11:43 PM
 * @Version: 1.0
 * @description:
 */

public class Balancer {
    private final BalancerStrategy balancerStrategy;

    public Balancer(BalancerStrategy balancerStrategy) {
        this.balancerStrategy = balancerStrategy;
    }

    public void calculatePercentDistribution(List<BalanceNode> belowNodes) {
        balancerStrategy.calculatePercentDistribution(belowNodes);
    }
}
