package ca.wisecode.lucene.master.grpc.client.distribute.balance;

import ca.wisecode.lucene.master.grpc.client.distribute.vo.BalanceNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/7/2024 11:13 PM
 * @Version: 1.0
 * @description: 计算平均文档数量
 */

public class AvgCalculator {
    public int calculateAverage(List<BalanceNode> balanceNodeList) {
        AtomicLong totalDocs = new AtomicLong();
        balanceNodeList.forEach(node -> totalDocs.addAndGet(node.getTotal()));
        return (int) (totalDocs.get() / balanceNodeList.size());
    }
}
