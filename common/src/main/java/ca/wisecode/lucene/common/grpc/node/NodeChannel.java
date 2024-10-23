package ca.wisecode.lucene.common.grpc.node;

import ca.wisecode.lucene.common.grpc.GrpcUtils;
import io.grpc.ManagedChannel;

import java.time.LocalDateTime;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/10/2024 1:03 PM
 * @Version: 1.0
 * @description:
 */

public class NodeChannel {
    /**
     * source 指本地 target 指另一端
     */
    private final String sourceHost;
    private final int sourcePort;
    private int slaveServerPort;
    private final String targetHost;
    private final int targetPort;
    private String indexPath;
    private ManagedChannel channel;
    private NodeState state = NodeState.ZERO_RUNNING;
    private LocalDateTime lastTime = LocalDateTime.now();
    private int failTimes = 0;

    private int docsTotal = 0;

    public NodeChannel(String sourceHost, int sourcePort, String targetHost, int targetPort) {
        this.sourceHost = sourceHost;
        this.sourcePort = sourcePort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    public String getTips() {
        return String.format("{\"sourceHost\":\"%s\",\"sourcePort\":\"%d\",\"targetHost\":\"%s\",\"targetPort\":\"%d\",\"state\":\"%s\"}", sourceHost, sourcePort, targetHost, targetPort, state);
    }

    public void buildManagedChannel(String host, int port) {
        this.channel = GrpcUtils.getManagedChannel(host, port);
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public NodeState getState() {
        return state;
    }

    public void setState(NodeState state) {
        this.state = state;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public LocalDateTime getLastTime() {
        return lastTime;
    }

    public void setLastTime(LocalDateTime lastTime) {
        this.lastTime = lastTime;
    }

    public int getFailTimes() {
        return failTimes;
    }

    public void setFailTimes(int failTimes) {
        this.failTimes = failTimes;
    }

    public int getDocsTotal() {
        return docsTotal;
    }

    public void setDocsTotal(int docsTotal) {
        this.docsTotal = docsTotal;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    public int getSlaveServerPort() {
        return slaveServerPort;
    }

    public void setSlaveServerPort(int slaveServerPort) {
        this.slaveServerPort = slaveServerPort;
    }
}
