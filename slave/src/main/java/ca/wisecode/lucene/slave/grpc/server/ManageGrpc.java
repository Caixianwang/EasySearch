package ca.wisecode.lucene.slave.grpc.server;

import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.grpc.models.JsonIn;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.ManageServiceGrpc;
import ca.wisecode.lucene.slave.cfg.ApplicationContextHolder;
import ca.wisecode.lucene.slave.grpc.server.manage.ManageImpl;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:39 PM
 * @Version: 1.0
 * @description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ManageGrpc extends ManageServiceGrpc.ManageServiceImplBase {

    private final ManageImpl manageImpl;

    @Override
    public void docsTotal(JsonIn req, StreamObserver<JsonOut> responseObserver) {
        JsonOut jsonOut = manageImpl.docsTotal(req);
        responseObserver.onNext(jsonOut);
        responseObserver.onCompleted();
    }

    @Override
    public void balance(DistributeRequest balanceRequest, StreamObserver<JsonOut> responseObserver) {
        JsonOut jsonOut = manageImpl.balance(balanceRequest);
        responseObserver.onNext(jsonOut);
        responseObserver.onCompleted();
    }

    @Override
    public void remove(DistributeRequest removeRequest, StreamObserver<JsonOut> responseObserver) {
        JsonOut jsonOut = manageImpl.remove(removeRequest);
        responseObserver.onNext(jsonOut);
        responseObserver.onCompleted();
        try {
            Thread.sleep(1000);
            ApplicationContextHolder.shutdown();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

    }

}
