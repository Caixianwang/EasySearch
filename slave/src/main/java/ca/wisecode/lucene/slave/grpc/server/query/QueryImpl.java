package ca.wisecode.lucene.slave.grpc.server.query;


import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.Pager;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/5/2024 10:15 AM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class QueryImpl {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private SearchManager searchManager;
    private NodeChannel nodeChannel;

    public JsonOut query(final Pager pager) {
        try {
            JsonNode json = objectMapper.readTree("");
            return JsonOut.newBuilder()
                    .setMsg(String.format("{\"code\":200,\"total\":\"%d\"}", searchManager.getReader().numDocs()))
                    .build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return JsonOut.newBuilder()
                    .setMsg(String.format("{\"code\":400,\"tip\":\"%s\"}", Constants.FAILURE))
                    .build();
        }

    }


}
