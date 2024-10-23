package ca.wisecode.lucene.common.grpc.node;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/8/2024 1:42 PM
 * @Version: 1.0
 * @description:
 */
public enum NodeState {
    ONE_BALANCING(1),
    ZERO_RUNNING(0),

    ONE_FAILURE(-1),
    TWO_REMOVING(-2),

    NINE_CLOSED(-9);

    private int value;

    private NodeState(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static NodeState fromValue(int value) {
        for (NodeState state : NodeState.values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid NodeState value: " + value);
    }
}
