package ca.wisecode.lucene.master.grpc.client.index.balance.dist;

import ca.wisecode.lucene.master.grpc.client.index.balance.vo.BalanceNode;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/7/2024 11:38 PM
 * @Version: 1.0
 * @description: 负责计算低于平均数节点需要接收的比例
 */
public interface BalancerStrategy {
    void calculatePercentDistribution(List<BalanceNode> belowNodes);
}
