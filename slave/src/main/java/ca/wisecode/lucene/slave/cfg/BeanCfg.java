package ca.wisecode.lucene.slave.cfg;

import ca.wisecode.lucene.common.grpc.GrpcUtils;
import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.slave.env.EnvInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:26 PM
 * @Version: 1.0
 * @description:
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class BeanCfg {

    private final EnvInfo envInfo;

    @Bean
    public NodeChannel initNodeChannel() throws IOException {
        NodeChannel nodeChannel = new NodeChannel(GrpcUtils.getLocalHost(), envInfo.getSlavePort(), envInfo.getMasterHost(), envInfo.getMasterPort());
        nodeChannel.buildManagedChannel(nodeChannel.getTargetHost(), nodeChannel.getTargetPort());
        return nodeChannel;
    }

}
