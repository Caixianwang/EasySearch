package ca.wisecode.lucene.master.grpc.client.distribute;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.grpc.node.NodeState;
import ca.wisecode.lucene.master.grpc.node.MasterNode;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/16/2024 12:27 PM
 * @Version: 1.0
 * @description:
 */

public class Intercept {
    public static boolean isValid(MasterNode masterNode) {
        for (NodeChannel channel : masterNode.getChannels()) {
            if (channel.getState() == NodeState.ONE_BALANCING || channel.getState() == NodeState.TWO_REMOVING) {
                return false;
            }
        }
        return true;
    }
}
