package ca.wisecode.lucene.slave.grpc.server;

import ca.wisecode.lucene.grpc.models.*;
import ca.wisecode.lucene.slave.grpc.server.index.IndexImpl;
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
public class IndexGrpc extends IndexServiceGrpc.IndexServiceImplBase {

    private final IndexImpl indexImpl;

    @Override
    public void insertTable(TableRequest table, StreamObserver<JsonOut> responseObserver) {
        JsonOut jsonOut = indexImpl.insertTable(table);
        responseObserver.onNext(jsonOut);
        responseObserver.onCompleted();
    }
    @Override
    public void insertRows(RowsRequest rowsRequest, StreamObserver<JsonOut> responseObserver) {
        JsonOut jsonOut = indexImpl.insertRows(rowsRequest);
        responseObserver.onNext(jsonOut);
        responseObserver.onCompleted();
    }

}
