package ca.wisecode.lucene.slave.grpc.server.manage.distribute;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.grpc.models.TargetNode;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance.DestNode;
import org.apache.lucene.search.ScoreDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 10:53 PM
 * @Version: 1.0
 * @description:
 */

public class BaseQuantitySplit {

    /**
     * @param distributeTotal:可能是实际分发的数量，也可以是个分母 最终计算各比例：targetNode.getCnt() / distributeTotal
     * @param distributeRequest
     * @param scoreDocs
     * @return
     */
    protected List<DestNode> createDestination(int distributeTotal, DistributeRequest distributeRequest, List<ScoreDoc> scoreDocs) {
        int totalSize = scoreDocs.size();
        List<DestNode> destNodes = new ArrayList<>();
        int start = 0;
        for (TargetNode targetNode : distributeRequest.getTargetNodesList()) {
            DestNode destNode = new DestNode(targetNode.getHost(), targetNode.getPort());
            int partSize = (int) Math.round((float) targetNode.getCnt() / distributeTotal * totalSize);
            // Ensure we don't go out of bounds
            int end = Math.min(start + partSize, totalSize);
            destNode.setList(scoreDocs.subList(start, end));
            start = end;
            destNodes.add(destNode);
        }
        return destNodes;
    }
}
