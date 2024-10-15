package ca.wisecode.lucene.slave.grpc.client;


import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.slave.grpc.client.service.HealthService;
import ca.wisecode.lucene.slave.service.StateBS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final NodeChannel nodeChannel;
    private final StateBS stateBS;
    private final IndexWriter indexWriter;

    @Scheduled(initialDelay = 30000, fixedRate = 15000)
    public void healthTask() {
        // 如果master检查过，就不用再次检查
        long interval = Duration.between(nodeChannel.getLastTime(), LocalDateTime.now()).getSeconds();
        if (interval > 10 || nodeChannel.getFailTimes() != 0) {
            healthService.healthCheck(nodeChannel);
        }
    }

    @Scheduled(initialDelay = 50000, fixedRate = 30000)
    public void refreshIndex() {
        if (stateBS.getNewAddIndexSize() > Constants.MEMORY_MAX_INDEX) {
            try {
                indexWriter.commit();
                stateBS.setNewAddIndexSize(0);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
