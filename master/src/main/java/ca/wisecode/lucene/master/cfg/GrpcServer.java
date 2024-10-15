package ca.wisecode.lucene.master.cfg;

import ca.wisecode.lucene.common.grpc.GrpcUtils;
import ca.wisecode.lucene.common.sqlite.SQLiteTemplate;
import ca.wisecode.lucene.master.env.EnvInfo;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import ca.wisecode.lucene.master.grpc.server.ActuatorGrpc;
import ca.wisecode.lucene.master.grpc.server.IndexGrpc;
import ca.wisecode.lucene.master.grpc.server.ProjectGrpc;
import io.grpc.Grpc;
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
    private final EnvInfo envInfo;
    private final ActuatorGrpc actuatorGrpc;
    private final IndexGrpc indexGrpc;
    private final ProjectGrpc projectGrpc;

    @Bean
    public Server startServer() throws IOException {
        if (envInfo.getSqliteUrl() != null) {
            SQLiteTemplate.initDatabaseUrl(envInfo.getSqliteUrl());
        }
        envInfo.setMasterHost(GrpcUtils.getLocalHost());
        return ServerBuilder.forPort(envInfo.getMasterPort())
                .addService(actuatorGrpc).addService(indexGrpc).addService(projectGrpc)
                .build()
                .start();
    }


}
