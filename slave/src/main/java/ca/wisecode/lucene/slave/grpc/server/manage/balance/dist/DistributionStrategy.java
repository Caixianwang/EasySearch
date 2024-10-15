package ca.wisecode.lucene.slave.grpc.server.manage.balance.dist;

import ca.wisecode.lucene.grpc.models.BalanceRequest;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 1:35 AM
 * @Version: 1.0
 * @description:
 */
public interface DistributionStrategy {
    /**
     * 从当前节点需要分发出去的总数
     *
     * @param balanceRequest
     * @return
     */
    int distributeNums(BalanceRequest balanceRequest);

    /**
     * 从当前节点抽取的索引比例
     *
     * @param distNums
     * @return
     */
    float fetchPercent(int distNums, int numDocs);
}
