package ca.wisecode.lucene.slave.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/10/2024 12:36 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class EnvInfo {
    @Value("${spring.application.name}")
    private String applicationName;

    private String slaveHost;

    @Value("${server.port:8081}")
    private int slaveServerPort;
    @Value("${grpc.port:50051}")
    private int slavePort;
    @Value("${grpc.master-host:localhost}")
    private String masterHost;
    @Value("${grpc.master-port:50051}")
    private int masterPort;

    @Value("${lucene.path}")
    private String indexPath;

    public void setSlaveHost(String slaveHost) {
        this.slaveHost = slaveHost;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getSlaveHost() {
        return slaveHost;
    }

    public int getSlavePort() {
        return slavePort;
    }

    public String getMasterHost() {
        return masterHost;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public int getSlaveServerPort() {
        return slaveServerPort;
    }
}
