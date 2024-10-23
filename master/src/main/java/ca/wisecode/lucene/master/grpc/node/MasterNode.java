package ca.wisecode.lucene.master.grpc.node;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.grpc.node.NodeState;
import ca.wisecode.lucene.grpc.models.HealthInOut;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/4/2024 9:14 AM
 * @Version: 1.0
 * @description:
 */

@Slf4j
public class MasterNode {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Getter
    @Setter
    private String masterHost;
    @Getter
    @Setter
    private int masterPort;
    @Getter
    private final List<NodeChannel> channels = new ArrayList<>();

    public void slaveHealth(HealthInOut healthIn) {
        boolean exist = false;
        for (NodeChannel nodeChannel : channels) {
            if (healthIn.getPort() == nodeChannel.getTargetPort() && healthIn.getHost().equals(nodeChannel.getTargetHost())) {
                exist = true;
                nodeChannel.setLastTime(LocalDateTime.now());
                nodeChannel.setFailTimes(0);
//                boolean stateChanged = nodeChannel.getState() != NodeState.fromValue(healthIn.getState());
//                boolean docsTotalChanged = nodeChannel.getDocsTotal() != healthIn.getDocsTotal();
                nodeChannel.setState(NodeState.fromValue(healthIn.getState()));
                nodeChannel.setDocsTotal(healthIn.getDocsTotal());
//                if (stateChanged || docsTotalChanged) {
//                    eventPublisher.publishEvent(new SlaveStatusChangeEvent(this));
//                }
            }
        }
        if (!exist) {
            NodeChannel nodeChannel = new NodeChannel(masterHost, masterPort, healthIn.getHost(), healthIn.getPort());
            nodeChannel.buildManagedChannel(healthIn.getHost(), healthIn.getPort());
            nodeChannel.setState(NodeState.fromValue(healthIn.getState()));
            nodeChannel.setDocsTotal(healthIn.getDocsTotal());
            nodeChannel.setIndexPath(healthIn.getIndexPath());
            nodeChannel.setSlaveServerPort(healthIn.getServerPort());
            channels.add(nodeChannel);
//            eventPublisher.publishEvent(new SlaveStatusChangeEvent(this));
            log.debug("Master slaveRegister success -> {}", nodeChannel.getTips());
        }
    }


    public List<NodeChannel> availableChannels() {
        List<NodeChannel> availableList = new ArrayList<>();
        for (NodeChannel channel : channels) {
            if (channel.getState().getValue() >= NodeState.ZERO_RUNNING.getValue()) {
                availableList.add(channel);
            }
        }
        return availableList;
    }
}
