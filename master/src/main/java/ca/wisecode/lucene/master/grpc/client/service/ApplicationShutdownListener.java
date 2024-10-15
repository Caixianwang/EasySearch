package ca.wisecode.lucene.master.grpc.client.service;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.sqlite.SQLiteTemplate;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import io.grpc.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 9:06 AM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ApplicationShutdownListener implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private Server server;
    @Autowired
    private MasterNode masterNode;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            SQLiteTemplate.shutdownPool();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (server != null) {
            try {
                server.shutdown().awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        for (NodeChannel nodeChannel : masterNode.getChannels()) {
            if (nodeChannel.getChannel() != null) {
                nodeChannel.getChannel().shutdown();
            }
        }

        log.info("Master close success");
    }

}

