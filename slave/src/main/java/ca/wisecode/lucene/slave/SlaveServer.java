package ca.wisecode.lucene.slave;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.slave.grpc.client.service.HealthService;
import io.grpc.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 1:47 PM
 * @Version: 1.0
 * @description:
 */

@SpringBootApplication
@EnableScheduling
@Slf4j
public class SlaveServer {
    public static void main(final String[] args) {
        SpringApplication.run(SlaveServer.class, args);

    }

    @Bean
    public CommandLineRunner commandLineRunner(final Server server, final HealthService healthService, final NodeChannel nodeChannel) {
        log.info(" ****** GRPC slave started on host {} port {}", nodeChannel.getSourceHost(), nodeChannel.getSourcePort());
        return args -> {
            healthService.healthCheck(nodeChannel);
        };
    }
}
