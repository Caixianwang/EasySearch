package ca.wisecode.lucene.slave.grpc.client.service;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.grpc.node.NodeState;
import io.grpc.Server;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 8:20 AM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ShutdownService {
    @Autowired
    private HealthService healthService;
    @Autowired
    private Server server;
    @Autowired
    private NodeChannel nodeChannel;
    @Autowired
    private IndexWriter indexWriter;

    @PreDestroy
    public void onShutdown() {
        nodeChannel.setState(NodeState.NINE_CLOSE_);
        healthService.healthCheck(nodeChannel);
        nodeChannel.getChannel().shutdown();
        try {
            server.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        if (nodeChannel.getChannel() != null) {
            nodeChannel.getChannel().shutdown();
        }
        try {
            indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Close the slave success");
    }
}
