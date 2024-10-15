package ca.wisecode.lucene.slave.grpc.client.service;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.grpc.models.ActuatorServiceGrpc;
import ca.wisecode.lucene.grpc.models.HealthInOut;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/10/2024 11:57 AM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class HealthService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void healthCheck(NodeChannel nodeChannel) {
        ManagedChannel managedChannel = nodeChannel.getChannel();
        ActuatorServiceGrpc.ActuatorServiceStub stub = ActuatorServiceGrpc.newStub(managedChannel);

        HealthInOut healthIn = HealthInOut.newBuilder()
                .setHost(nodeChannel.getSourceHost())
                .setPort(nodeChannel.getSourcePort())
                .setState(nodeChannel.getState().getValue())
                .build();

        stub.health(healthIn, new StreamObserver<HealthInOut>() {
            @Override
            public void onNext(HealthInOut healthOut) {
                log.debug("Slave client healthCheck success - > {}", nodeChannel.getTips());
            }

            @Override
            public void onError(Throwable t) {
                nodeChannel.setLastTime(LocalDateTime.now());
                nodeChannel.setFailTimes(nodeChannel.getFailTimes() + 1);
                if (t instanceof StatusRuntimeException) {
                    StatusRuntimeException e = (StatusRuntimeException) t;
                    log.error("Slave client healthCheck failed -> {} {}", nodeChannel.getTips(), e.getMessage());
                } else {
                    log.error(t.getMessage(), t);
                }
            }

            @Override
            public void onCompleted() {
                nodeChannel.setLastTime(LocalDateTime.now());
                nodeChannel.setFailTimes(0);
                log.info("Slave client healthCheck success -> {}", nodeChannel.getTips());
            }
        });
    }
}
