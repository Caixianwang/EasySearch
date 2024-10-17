package ca.wisecode.lucene.slave.grpc.server.manage.distribute.remove;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance.BalanceSampleRadio;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance.SampleRadio;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 11:37 PM
 * @Version: 1.0
 * @description:
 */

public class RemoveSampleRadio extends BalanceSampleRadio implements SampleRadio {

    public RemoveSampleRadio(SearchManager searchManager) {
        super(searchManager);
    }

    @Override
    public float fetchPercent(DistributeRequest removeRequest) {
        return 1.0f;
    }
}
