package ca.wisecode.lucene.master.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/10/2024 12:36 PM
 * @Version: 1.0
 * @description:
 */

public class EnvInfo {
    private String sqliteUrl;
    private String applicationName;

    private String masterHost;
    private int masterPort;

    public String getSqliteUrl() {
        return sqliteUrl;
    }

    public void setSqliteUrl(String sqliteUrl) {
        this.sqliteUrl = sqliteUrl;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getMasterHost() {
        return masterHost;
    }

    public void setMasterHost(String masterHost) {
        this.masterHost = masterHost;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(int masterPort) {
        this.masterPort = masterPort;
    }
}
