package ca.wisecode.lucene.master.grpc.server;

import ca.wisecode.lucene.grpc.models.JsonIn;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.ProjectServiceGrpc;
import ca.wisecode.lucene.master.grpc.server.index.ProjectImpl;
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
public class ProjectGrpc extends ProjectServiceGrpc.ProjectServiceImplBase {

    private final ProjectImpl projectImpl;

    @Override
    public void readProject(JsonIn req, StreamObserver<JsonOut> responseObserver) {
        JsonOut jsonOut = projectImpl.readProject(req);
        responseObserver.onNext(jsonOut);
        responseObserver.onCompleted();
    }

}
