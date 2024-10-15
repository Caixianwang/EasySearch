package ca.wisecode.lucene.master.grpc.node;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.grpc.node.NodeState;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
    @Getter
    @Setter
    private String masterHost;
    @Getter
    @Setter
    private int masterPort;
    @Getter
    private final List<NodeChannel> channels = new ArrayList<>();

    public void slaveRegister(String slaveHost, int slavePort, int state) {
        boolean exist = false;
        for (NodeChannel nodeChannel : channels) {
            if (slavePort == nodeChannel.getTargetPort() && slaveHost.equals(nodeChannel.getTargetHost())) {
                exist = true;
                nodeChannel.setLastTime(LocalDateTime.now());
                nodeChannel.setFailTimes(0);
                nodeChannel.setState(NodeState.fromValue(state));

            }
        }
        if (!exist) {
            NodeChannel nodeChannel = new NodeChannel(masterHost, masterPort, slaveHost, slavePort);
            nodeChannel.buildManagedChannel(slaveHost, slavePort);
            nodeChannel.setState(NodeState.fromValue(state));
            channels.add(nodeChannel);
            log.debug("Master slaveRegister success -> {}", nodeChannel.getTips());
        }
    }

    public List<NodeChannel> availableChannels() {
        List<NodeChannel> availableList = new ArrayList<>();
        for (NodeChannel channel : channels) {
            if (channel.getState().getValue() >= NodeState.ZERO_RUN.getValue()) {
                availableList.add(channel);
            }
        }
        return availableList;
    }
}
