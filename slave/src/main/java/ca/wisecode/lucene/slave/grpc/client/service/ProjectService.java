package ca.wisecode.lucene.slave.grpc.client.service;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.model.PrjMeta;
import ca.wisecode.lucene.grpc.models.JsonIn;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.ProjectServiceGrpc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/10/2024 11:57 AM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ProjectService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, PrjMeta> prjMap = new HashMap<>();
    @Autowired
    private NodeChannel nodeChannel;

    public PrjMeta readProject(String prjID) {
        if (prjMap.containsKey(prjID)) {
            return prjMap.get(prjID);
        } else {
            ManagedChannel managedChannel = nodeChannel.getChannel();
            ProjectServiceGrpc.ProjectServiceBlockingStub stub = ProjectServiceGrpc.newBlockingStub(managedChannel);
            String msg = String.format("{\"prjID\":\"%s\"}", prjID);
            JsonIn jsonIn = JsonIn.newBuilder().setMsg(msg).build();
            JsonOut jsonOut = stub.readProject(jsonIn);
            try {
                return objectMapper.readValue(jsonOut.getMsg(), PrjMeta.class);
            } catch (JsonProcessingException e) {
                throw new BusinessException(e);
            }
        }
    }
}
