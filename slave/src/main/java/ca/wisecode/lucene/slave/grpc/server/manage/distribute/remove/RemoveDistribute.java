package ca.wisecode.lucene.slave.grpc.server.manage.distribute.remove;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.QuantitySplitStrategy;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance.SampleRadio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/5/2024 1:30 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class RemoveDistribute extends AbstractDistribute {


    @Override
    protected float samplepPercent(DistributeRequest distributeRequest) {
        SampleRadio sampleRadio = new RemoveSampleRadio(searchManager);
        return sampleRadio.fetchPercent(distributeRequest);
    }

    @Override
    protected QuantitySplitStrategy quantityStrategy(DistributeRequest distributeRequest) {
        return new RemoveQuantitySplit();
    }

}
