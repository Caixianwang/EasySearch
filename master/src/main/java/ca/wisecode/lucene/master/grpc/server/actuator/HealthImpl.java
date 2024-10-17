package ca.wisecode.lucene.master.grpc.server.actuator;

import ca.wisecode.lucene.grpc.models.HealthInOut;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
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
    private MasterNode masterNode;

    public HealthInOut health(final HealthInOut healthIn) {
        masterNode.slaveHealth(healthIn.getHost(), healthIn.getPort(), healthIn.getState());
        return HealthInOut.newBuilder().build();
    }
}
