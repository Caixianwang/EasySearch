package ca.wisecode.lucene.common.sqlite;

import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.TimerTask;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/18/2024 1:52 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class HealthCheckTask extends TimerTask {
    private final ConnectionPool connectionPool;

    protected HealthCheckTask(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void run() {
        try {
            connectionPool.removeTimedOutConnections();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }
}
