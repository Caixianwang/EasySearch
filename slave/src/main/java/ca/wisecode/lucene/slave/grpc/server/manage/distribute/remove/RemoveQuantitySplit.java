package ca.wisecode.lucene.slave.grpc.server.manage.distribute.remove;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.BaseQuantitySplit;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.QuantitySplitStrategy;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance.DestNode;
import org.apache.lucene.search.ScoreDoc;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 10:53 PM
 * @Version: 1.0
 * @description:
 */

public class RemoveQuantitySplit extends BaseQuantitySplit implements QuantitySplitStrategy {
    @Override
    public List<DestNode> buildDestination(DistributeRequest distributeRequest, List<ScoreDoc> scoreDocs) {

        int distributeTotal = distributeRequest.getTargetNodesList().size();//分母
        distributeRequest.getTargetNodesList().forEach(node -> node.toBuilder().setCnt(1));
        // 各节点按照平均值分配
        return this.createDestination(distributeTotal, distributeRequest, scoreDocs);
    }
}
