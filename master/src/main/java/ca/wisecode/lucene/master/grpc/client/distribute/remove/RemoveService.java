package ca.wisecode.lucene.master.grpc.client.distribute.remove;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.grpc.models.TargetNode;
import ca.wisecode.lucene.master.grpc.client.distribute.Intercept;
import ca.wisecode.lucene.master.grpc.client.distribute.RemoveDistribute;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/2/2024 12:20 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class RemoveService {
    @Autowired
    private MasterNode masterNode;
    @Autowired
    private ReentrantLock lock;

    public void remove(String host, int port) {
        try {
            lock.lock();
            boolean valid = Intercept.isValid(masterNode);
            if (valid) {
                log.info("remove start...");
                DistributeRequest distributeRequest = this.buildDistributeRequest(host, port);
                this.distribute(distributeRequest, host, port);
                log.info("remove end...");
            }
        } finally {
            lock.unlock();

        }

    }

    private void distribute(DistributeRequest distributeRequest, String host, int port) {
        if (distributeRequest != null) {
            RemoveDistribute removeDistribute = new RemoveDistribute(masterNode);
            removeDistribute.distribute(distributeRequest, host, port);
        }
    }

    private DistributeRequest buildDistributeRequest(String host, int port) {
        List<NodeChannel> nodeChannels = masterNode.availableChannels();
        DistributeRequest.Builder builder = DistributeRequest.newBuilder();
        for (NodeChannel channel : nodeChannels) {
            if (!channel.getTargetHost().equals(host) || channel.getTargetPort() != port) {
                TargetNode targetNode = TargetNode.newBuilder()
                        .setHost(channel.getTargetHost())
                        .setPort(channel.getTargetPort())
                        .setCnt(1)
                        .build();
                builder.addTargetNodes(targetNode);
            }
        }
        if (!builder.getTargetNodesList().isEmpty()) {
            return builder.build();
        } else {
            return null;
        }
    }
}
