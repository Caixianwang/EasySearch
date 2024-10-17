package ca.wisecode.lucene.master.grpc.client.distribute;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.grpc.models.DistributeRequest;
import ca.wisecode.lucene.grpc.models.ManageServiceGrpc;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/16/2024 12:38 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public abstract class AbstractDistribute {
    private MasterNode masterNode;

    public AbstractDistribute(MasterNode masterNode) {
        this.masterNode = masterNode;
    }


    public void distribute(DistributeRequest distributeRequest, String host, int port) {
        ManagedChannel managedChannel = getChannel(host, port);
        if (managedChannel != null) {
            ManageServiceGrpc.ManageServiceStub stub = ManageServiceGrpc.newStub(managedChannel);
            this.execute(stub,distributeRequest);
        }
    }

    protected abstract void execute(ManageServiceGrpc.ManageServiceStub stub,DistributeRequest distributeRequest);

    protected ManagedChannel getChannel(String host, int port) {
        for (NodeChannel nodeChannel : masterNode.availableChannels()) {
            if (nodeChannel.getTargetPort() == port && nodeChannel.getTargetHost().equals(host)) {
                return nodeChannel.getChannel();
            }
        }
        return null;
    }
}
