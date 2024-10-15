package ca.wisecode.lucene.master.grpc.client.index.balance.dist;

import ca.wisecode.lucene.master.grpc.client.index.balance.vo.BalanceNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/7/2024 11:15 PM
 * @Version: 1.0
 * @description: 筛选出高于和低于平均数的节点
 */

public class NodeSelector {
    private static final double MIN_PERCENT = 0.1;  // 10%

    public List<BalanceNode> selectAboveAverageNodes(int avg, List<BalanceNode> balanceNodeList) {
        List<BalanceNode> aboveNodes = new ArrayList<>();
        balanceNodeList.forEach(node -> {
            if ((double) (node.getTotal() - avg) / avg > MIN_PERCENT) {
                node.setAvg(avg);
                aboveNodes.add(node);
            }
        });
        return aboveNodes;

    }

    public List<BalanceNode> selectBelowAverageNodes(int avg, List<BalanceNode> balanceNodeList) {
        List<BalanceNode> belowNodes = new ArrayList<>();
        balanceNodeList.forEach(node -> {
            if ((double) (avg - node.getTotal()) / avg > MIN_PERCENT) {
                node.setAvg(avg);
                belowNodes.add(node);
            }
        });
        return belowNodes;
    }
}
