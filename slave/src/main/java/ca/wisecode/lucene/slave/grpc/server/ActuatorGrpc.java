package ca.wisecode.lucene.slave.grpc.server;

import ca.wisecode.lucene.grpc.models.ActuatorServiceGrpc;
import ca.wisecode.lucene.grpc.models.HealthInOut;
import ca.wisecode.lucene.grpc.models.JsonIn;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.slave.grpc.server.actuator.HealthImpl;
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
public class ActuatorGrpc extends ActuatorServiceGrpc.ActuatorServiceImplBase {

    private final HealthImpl healthImpl;

    @Override
    public void health(HealthInOut healthIn, StreamObserver<HealthInOut> responseObserver) {
        HealthInOut healthOut = healthImpl.health(healthIn);
        responseObserver.onNext(healthOut);
        responseObserver.onCompleted();
    }

}
