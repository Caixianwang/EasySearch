package ca.wisecode.lucene.master.cfg;

import ca.wisecode.lucene.common.grpc.GrpcUtils;
import ca.wisecode.lucene.common.sqlite.SQLiteTemplate;
import ca.wisecode.lucene.master.env.EnvInfo;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:26 PM
 * @Version: 1.0
 * @description:
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class BeanCfg {

    @Value("${spring.sqlite.url}")
    private String sqliteUrl;
    @Value("${spring.application.name}")
    private String applicationName;

    private String masterHost;
    @Value("${grpc.port:50050}")
    private int masterPort;

    @Bean
    public ReentrantLock getReentrantLock() {
        return new ReentrantLock(); // 共享的锁
    }

    @Bean
    public EnvInfo getEnvInfo() throws UnknownHostException {
        if (sqliteUrl != null) {
            SQLiteTemplate.initDatabaseUrl(sqliteUrl);
        }
        EnvInfo envInfo = new EnvInfo();
        envInfo.setMasterHost(GrpcUtils.getLocalHost());
        envInfo.setMasterPort(masterPort);
        envInfo.setSqliteUrl(sqliteUrl);
        envInfo.setApplicationName(applicationName);
        return envInfo;
    }

    @Bean
    public MasterNode initMasterNode() throws UnknownHostException {
        MasterNode masterNode = new MasterNode();
        masterNode.setMasterHost(GrpcUtils.getLocalHost());
        masterNode.setMasterPort(masterPort);
        return masterNode;
    }

}
