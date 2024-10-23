package ca.wisecode.lucene.master.service.query;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.model.QueryMode;
import ca.wisecode.lucene.grpc.models.FilterRule;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.PagerRequest;
import ca.wisecode.lucene.grpc.models.QueryServiceGrpc;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import ca.wisecode.lucene.master.service.index.IndexBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 2:02 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class QueryBS extends IndexBase {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    ExecutorService executor = Executors.newFixedThreadPool(30);
    @Autowired
    private MasterNode masterNode;

    public List<Map<String, String>> queryList(String content) {
        List<NodeChannel> nodeChannels = masterNode.availableChannels();
        FilterRule filterRule = FilterRule.newBuilder().setName("content")
                .setValue(content)
                .setQueryMode(QueryMode.Parser).build();
        PagerRequest pagerRequest = PagerRequest.newBuilder().setPageSize(15)
                .addFilterRules(filterRule).build();
        List<Future<List<Map<String, String>>>> futures = new ArrayList<>();
        for (NodeChannel nodeChannel : nodeChannels) {
            futures.add(executor.submit(() -> queryNode(nodeChannel, pagerRequest)));
        }
        List<Map<String, String>> allList = new ArrayList<>();
        int targetSize = 15;
        int currentSize = 0;

        // Process the results from each node
        for (Future<List<Map<String, String>>> future : futures) {
            try {
                List<Map<String, String>> list = future.get();
                int len = Math.min(list.size(), Math.max(5, targetSize - currentSize)); // Ensure max 5 records from each node
                allList.addAll(list.subList(0, len));
                currentSize += len;

                if (currentSize >= targetSize) {
                    break; // Stop if we've gathered enough records
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new BusinessException(e);
            }
        }
        return allList;
    }

    private List<Map<String, String>> queryNode(NodeChannel nodeChannel, PagerRequest pagerRequest) throws JsonProcessingException {
        ManagedChannel channel = nodeChannel.getChannel();
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        JsonOut jsonOut = stub.query(pagerRequest);

        return objectMapper.readValue(jsonOut.getMsg(), new TypeReference<List<Map<String, String>>>() {
        });
    }
}
