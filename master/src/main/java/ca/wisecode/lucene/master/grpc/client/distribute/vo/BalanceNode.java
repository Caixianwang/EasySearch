package ca.wisecode.lucene.master.grpc.client.distribute.vo;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/2/2024 1:40 PM
 * @Version: 1.0
 * @description:
 */

public class BalanceNode {
    private final String host;
    private final int port;
    private int total;
    private int avg;
    private float percent = 0.0f;

    public BalanceNode(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPort() {
        return port;
    }


    public String getHost() {
        return host;
    }

    public int getAvg() {
        return avg;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}


