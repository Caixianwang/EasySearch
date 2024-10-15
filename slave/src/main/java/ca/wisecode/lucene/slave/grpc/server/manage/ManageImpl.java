package ca.wisecode.lucene.slave.grpc.server.manage;


import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.grpc.node.NodeState;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.BalanceRequest;
import ca.wisecode.lucene.grpc.models.JsonIn;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import ca.wisecode.lucene.slave.grpc.server.manage.balance.dist.DistBalance;
import ca.wisecode.lucene.slave.grpc.server.manage.balance.dist.DistBalanceService;
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
public class ManageImpl {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private SearchManager searchManager;
    @Autowired
    private DistBalanceService distBalanceService;
    @Autowired
    private NodeChannel nodeChannel;

    public JsonOut docsTotal(final JsonIn req) {
        try {
            JsonNode json = objectMapper.readTree(req.getMsg());
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

    public JsonOut balance(final BalanceRequest balanceRequest) {
        try {
            nodeChannel.setState(NodeState.ONE_BALANCE);
            distBalanceService.executeDistribute(balanceRequest);
            return JsonOut.newBuilder()
                    .setMsg(String.format("{\"code\":200,\"total\":\"%d\"}", searchManager.getReader().numDocs()))
                    .build();
        }finally {
            nodeChannel.setState(NodeState.ZERO_RUN);
        }

    }
}
