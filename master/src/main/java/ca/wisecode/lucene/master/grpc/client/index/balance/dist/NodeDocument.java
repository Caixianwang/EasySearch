package ca.wisecode.lucene.master.grpc.client.index.balance.dist;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.grpc.models.JsonIn;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.ManageServiceGrpc;
import ca.wisecode.lucene.master.grpc.client.index.balance.vo.BalanceNode;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/7/2024 11:12 PM
 * @Version: 1.0
 * @description: 负责计算每个节点的文档数量
 */

public class NodeDocument {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<BalanceNode> calculateNodeDocs(MasterNode masterNode) {
        List<BalanceNode> balanceNodeList = new ArrayList<>();
        for (NodeChannel nodeChannel : masterNode.availableChannels()) {
            ManagedChannel managedChannel = nodeChannel.getChannel();
            ManageServiceGrpc.ManageServiceBlockingStub stub = ManageServiceGrpc.newBlockingStub(managedChannel);
            String msg = String.format("{\"total\":0}");
            JsonIn jsonIn = JsonIn.newBuilder().setMsg(msg).build();
            JsonOut jsonOut = stub.docsTotal(jsonIn);
            try {
                JsonNode rootNode = objectMapper.readTree(jsonOut.getMsg());
                BalanceNode balanceNode = new BalanceNode(nodeChannel.getTargetHost(), nodeChannel.getTargetPort());
                balanceNode.setTotal(rootNode.get("total").asInt());
                balanceNodeList.add(balanceNode);
            } catch (JsonProcessingException e) {
                throw new BusinessException(e);
            }
        }
        return balanceNodeList;
    }
}
