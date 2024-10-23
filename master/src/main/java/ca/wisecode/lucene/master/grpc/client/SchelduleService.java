package ca.wisecode.lucene.master.grpc.client;


import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.grpc.node.NodeState;
import ca.wisecode.lucene.master.grpc.client.service.HealthService;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/10/2024 1:24 PM
 * @Version: 1.0
 * @description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchelduleService {
    private final HealthService healthService;
    private final MasterNode masterNode;

    @Scheduled(initialDelay = 30000, fixedRate = 2000)
    public void scanSlaveTask() {
        for (NodeChannel nodeChannel : masterNode.getChannels()) {
//            log.info("---" + nodeChannel.getTargetPort() + "---" + nodeChannel.getState());
            // 如果Slave检查过，就不用再次检查
            long interval = Duration.between(nodeChannel.getLastTime(), LocalDateTime.now()).getSeconds();
            if (interval > 1 && nodeChannel.getFailTimes() < 10 && nodeChannel.getState().getValue() >= NodeState.ONE_FAILURE.getValue()) {
                healthService.healthCheck(nodeChannel);
            }
        }

    }
}
