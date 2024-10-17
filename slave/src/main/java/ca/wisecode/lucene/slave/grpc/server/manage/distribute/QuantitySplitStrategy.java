package ca.wisecode.lucene.slave.grpc.server.manage.distribute;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance.DestNode;
import org.apache.lucene.search.ScoreDoc;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 1:35 AM
 * @Version: 1.0
 * @description:
 */
public interface QuantitySplitStrategy {

    /**
     * 节点动态平衡和移除数据，向各节点分配数量计算
     * 1、动态平衡，需要按各节点分配的数量，进行比例计算（比例不一样）
     * 2、移除节点，各节点按照平均数进行计算（比例一样）
     *
     * @param distributeRequest
     * @param scoreDocs
     * @return
     */
    List<DestNode> buildDestination(DistributeRequest distributeRequest, List<ScoreDoc> scoreDocs);

}
