package ca.wisecode.lucene.master.grpc.client.index.balance.dist;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.grpc.models.BalanceRequest;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.ManageServiceGrpc;
import ca.wisecode.lucene.grpc.models.TargetNode;
import ca.wisecode.lucene.master.grpc.client.index.balance.vo.BalanceNode;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/7/2024 11:19 PM
 * @Version: 1.0
 * @description: 负责数据分发
 */
@Slf4j
public class NodeDistribution {

    public void distributeData(MasterNode masterNode, List<BalanceNode> aboveNodes, List<BalanceNode> belowNodes) {
        for (BalanceNode aboveNode : aboveNodes) {
            int balance = aboveNode.getTotal() - aboveNode.getAvg();
            BalanceRequest.Builder builder = BalanceRequest.newBuilder();

            for (BalanceNode belowNode : belowNodes) {
                TargetNode targetNode = TargetNode.newBuilder()
                        .setHost(belowNode.getHost())
                        .setPort(belowNode.getPort())
                        .setCnt((int) (balance * belowNode.getPercent()))
                        .build();
                builder.addTargetNodes(targetNode);
            }

            BalanceRequest balanceRequest = builder.build();
            ManagedChannel managedChannel = getChannel(masterNode, aboveNode.getHost(), aboveNode.getPort());

            if (managedChannel != null) {
                ManageServiceGrpc.ManageServiceStub stub = ManageServiceGrpc.newStub(managedChannel);
                stub.balance(balanceRequest, new StreamObserver<JsonOut>() {
                    @Override
                    public void onNext(JsonOut jsonOut) {
                        log.debug("balance onNext");
                    }

                    @Override
                    public void onError(Throwable t) {
                        log.error("balance onError", t);
                    }

                    @Override
                    public void onCompleted() {
                        log.info("balance onCompleted");
                    }
                });
            }
        }
    }


    private ManagedChannel getChannel(MasterNode masterNode, String host, int port) {
        for (NodeChannel nodeChannel : masterNode.availableChannels()) {
            if (nodeChannel.getTargetPort() == port && nodeChannel.getTargetHost().equals(host)) {
                return nodeChannel.getChannel();
            }
        }
        return null;
    }
}
