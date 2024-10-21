package ca.wisecode.lucene.slave.grpc.server;

import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.Pager;
import ca.wisecode.lucene.grpc.models.QueryServiceGrpc;
import ca.wisecode.lucene.slave.grpc.server.query.QueryImpl;
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
public class QueryGrpc extends QueryServiceGrpc.QueryServiceImplBase {

    private final QueryImpl queryImpl;

    @Override
    public void query(Pager pager, StreamObserver<JsonOut> responseObserver) {
        JsonOut jsonOut = queryImpl.query(pager);
        responseObserver.onNext(jsonOut);
        responseObserver.onCompleted();
    }

}
