package ca.wisecode.lucene.slave.grpc.server.actuator;


import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.HealthInOut;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/5/2024 10:15 AM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class HealthImpl {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private NodeChannel nodeChannel;
    @Autowired
    private SearchManager searchManager;

    public HealthInOut health(final HealthInOut healthIn) {
        return HealthInOut.newBuilder()
                .setPort(nodeChannel.getSourcePort())
                .setHost(nodeChannel.getSourceHost())
                .setState(nodeChannel.getState().getValue())
                .setDocsTotal(searchManager.getReader().numDocs())
                .setMsg(Constants.SUCCESS)
                .build();
    }
}
