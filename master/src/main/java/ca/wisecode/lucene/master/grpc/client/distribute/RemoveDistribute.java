package ca.wisecode.lucene.master.grpc.client.distribute;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.ManageServiceGrpc;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/16/2024 12:38 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class RemoveDistribute extends AbstractDistribute {


    public RemoveDistribute(MasterNode masterNode) {
        super(masterNode);
    }

    @Override
    protected void execute(ManageServiceGrpc.ManageServiceStub stub, DistributeRequest balanceRequest) {
        stub.remove(balanceRequest, new StreamObserver<JsonOut>() {
            @Override
            public void onNext(JsonOut jsonOut) {
                log.info("remove onNext");
            }

            @Override
            public void onError(Throwable t) {
                log.error("remove onError", t);
            }

            @Override
            public void onCompleted() {
                log.info("remove onCompleted");
            }
        });
    }
}
