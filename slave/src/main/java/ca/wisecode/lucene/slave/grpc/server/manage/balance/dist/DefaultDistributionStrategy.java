package ca.wisecode.lucene.slave.grpc.server.manage.balance.dist;

import ca.wisecode.lucene.grpc.models.BalanceRequest;
import ca.wisecode.lucene.grpc.models.TargetNode;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 1:35 AM
 * @Version: 1.0
 * @description:
 */

public class DefaultDistributionStrategy implements DistributionStrategy {
    @Override
    public int distributeNums(BalanceRequest balanceRequest) {
        int distNums = 0;
        for (TargetNode targetNode : balanceRequest.getTargetNodesList()) {
            distNums += targetNode.getCnt();
        }
        return distNums;
    }

    @Override
    public float fetchPercent(int distNums, int numDocs) {
        if (numDocs != 0 && distNums != 0) {
            return (float) distNums / numDocs;
        }
        return 0;
    }
}
