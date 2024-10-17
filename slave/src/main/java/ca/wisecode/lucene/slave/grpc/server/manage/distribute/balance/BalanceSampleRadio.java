package ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.grpc.models.TargetNode;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.BaseFetchRadio;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 11:37 PM
 * @Version: 1.0
 * @description:
 */

public class BalanceSampleRadio extends BaseFetchRadio implements SampleRadio {

    public BalanceSampleRadio(SearchManager searchManager) {
        super(searchManager);
    }

    @Override
    public float fetchPercent(DistributeRequest balanceRequest) {
        int distributeTotal = balanceRequest.getTargetNodesList().stream().mapToInt(TargetNode::getCnt).sum();
        int docTotal = searchManager.getReader().numDocs();
        if (docTotal != 0 && distributeTotal != 0) {
            return (float) distributeTotal / docTotal;
        }
        return 0;
    }
}
