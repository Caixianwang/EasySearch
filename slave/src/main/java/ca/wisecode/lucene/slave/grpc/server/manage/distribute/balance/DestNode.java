package ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance;

import org.apache.lucene.search.ScoreDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/6/2024 10:29 AM
 * @Version: 1.0
 * @description:
 */

public class DestNode {
    private String host;
    private int port;
    private List<ScoreDoc> list = new ArrayList<>();

    public DestNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<ScoreDoc> getList() {
        return list;
    }

    public void setList(List<ScoreDoc> list) {
        this.list = list;
    }
}
