package ca.wisecode.lucene.common.sqlite;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/18/2024 1:38 PM
 * @Version: 1.0
 * @description:
 */

public class ConnectionPool {
    private static final int INITIAL_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 10;
    private static int currConnCount = 0;
    private static final long HEALTH_CHECK_INTERVAL_MS = 30000; // 每 30 秒健康检查一次
    private static ConnectionPool instance;
    private final LinkedList<PooledConnection> connPool = new LinkedList<>();
    private Timer healthCheckTimer;

    private ConnectionPool() throws SQLException {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            connPool.add(ConnectionProxyHandler.createProxy(createConnection(), this));
            currConnCount++;
        }
        startHealthCheckTask();
    }

    protected static synchronized ConnectionPool getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    private Connection createConnection() throws SQLException {
        return ConnectionFactory.createConnection();
    }

    protected synchronized PooledConnection getConnection() throws SQLException {
        if (connPool.isEmpty()) {
            if (currConnCount < MAX_POOL_SIZE) {
                connPool.add(ConnectionProxyHandler.createProxy(createConnection(), this));
                currConnCount++;
            } else {
                throw new SQLException("Maximum pool size reached, no available connections!");
            }
        }
        PooledConnection proxy = connPool.removeFirst();
        ((ConnectionProxyHandler) Proxy.getInvocationHandler(proxy)).updateLastUsedTime();
        return proxy;
    }

    protected synchronized void releaseConnection(PooledConnection connection) {
        if (connection != null) {
            connPool.addLast(connection);
        }
    }

    protected synchronized void removeTimedOutConnections() throws SQLException {
        Iterator<PooledConnection> iterator = connPool.iterator();
        while (iterator.hasNext()) {
            PooledConnection connection = iterator.next();
            if (Proxy.isProxyClass(connection.getClass())) {
                ConnectionProxyHandler handler = (ConnectionProxyHandler) Proxy.getInvocationHandler(connection);
                if (connPool.size() > INITIAL_POOL_SIZE) {
                    if (handler.isTimedOut()) {
                        connection.forceClose();
                        iterator.remove();
                        currConnCount--;
                    }
                }
            }
        }
    }

    private void startHealthCheckTask() {
        healthCheckTimer = new Timer(true);
        healthCheckTimer.scheduleAtFixedRate(new HealthCheckTask(this), 0, HEALTH_CHECK_INTERVAL_MS);
    }

    protected synchronized int getPoolSize() {
        return connPool.size();
    }

    // 关闭所有物理连接
    public synchronized void shutdownPool() throws SQLException {
        if (healthCheckTimer != null) {
            healthCheckTimer.cancel();
        }
        Iterator<PooledConnection> iterator = connPool.iterator();
        while (iterator.hasNext()) {
            PooledConnection connection = iterator.next();
            connection.forceClose();
            iterator.remove();
        }
    }


    protected synchronized void shutdownPool1() throws SQLException {
//        if (healthCheckTimer != null) {
//            healthCheckTimer.cancel();
//        }
        for (PooledConnection connection : connPool) {
            System.out.println(connPool.size() + "=====" + connection);
            if (connection != null && !connection.isClosed()) {
                connection.forceClose();
            }
        }
        connPool.clear();
    }
}
