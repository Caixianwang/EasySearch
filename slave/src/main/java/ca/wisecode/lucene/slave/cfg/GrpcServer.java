package ca.wisecode.lucene.slave.cfg;

import ca.wisecode.lucene.common.grpc.GrpcUtils;
import ca.wisecode.lucene.slave.env.EnvInfo;
import ca.wisecode.lucene.slave.grpc.server.ActuatorGrpc;
import ca.wisecode.lucene.slave.grpc.server.IndexGrpc;
import ca.wisecode.lucene.slave.grpc.server.ManageGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:26 PM
 * @Version: 1.0
 * @description:
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class GrpcServer {

    private final IndexGrpc indexGrpc;
    private final ActuatorGrpc actuatorGrpc;
    private final ManageGrpc manageGrpc;
    private final EnvInfo envInfo;

    @Bean
    public Server startServer() throws IOException {
        envInfo.setSlaveHost(GrpcUtils.getLocalHost());
        return ServerBuilder.forPort(envInfo.getSlavePort())
                .addService(indexGrpc).addService(actuatorGrpc)
                .addService(manageGrpc)
                .build()
                .start();
    }

}
