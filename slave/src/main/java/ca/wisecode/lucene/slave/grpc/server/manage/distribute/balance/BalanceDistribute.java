package ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.QuantitySplitStrategy;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.remove.AbstractDistribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 11:24 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class BalanceDistribute extends AbstractDistribute {
    @Override
    protected float samplepPercent(DistributeRequest balanceRequest) {
        SampleRadio sampleRadio = new BalanceSampleRadio(searchManager);
        return sampleRadio.fetchPercent(balanceRequest);
    }

    @Override
    protected QuantitySplitStrategy quantityStrategy(DistributeRequest balanceRequest) {
        return new BalanceQuantitySplit();
    }


}
