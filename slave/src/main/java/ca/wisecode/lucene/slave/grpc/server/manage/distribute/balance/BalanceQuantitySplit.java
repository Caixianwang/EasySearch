package ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.grpc.models.TargetNode;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.BaseQuantitySplit;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.QuantitySplitStrategy;
import org.apache.lucene.search.ScoreDoc;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 10:53 PM
 * @Version: 1.0
 * @description:
 */

public class BalanceQuantitySplit extends BaseQuantitySplit implements QuantitySplitStrategy {
    @Override
    public List<DestNode> buildDestination(DistributeRequest balanceRequest, List<ScoreDoc> scoreDocs) {
        int distributeTotal = balanceRequest.getTargetNodesList().stream().mapToInt(TargetNode::getCnt).sum();
        return this.createDestination(distributeTotal, balanceRequest, scoreDocs);

    }
}
