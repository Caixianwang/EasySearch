package ca.wisecode.lucene.common.sqlite;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/18/2024 1:54 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class ConnectionProxyHandler implements InvocationHandler {
    private static final long TIMEOUT_MS = 60000; // 超时 60 秒
    private final Connection realConnection;
    private final ConnectionPool connectionPool;
    private long lastUsedTime;

    protected ConnectionProxyHandler(Connection realConnection, ConnectionPool connectionPool) {
        this.realConnection = realConnection;
        this.connectionPool = connectionPool;
        this.lastUsedTime = System.currentTimeMillis();
    }

    protected static PooledConnection createProxy(Connection realConnection, ConnectionPool connectionPool) {
        return (PooledConnection) Proxy.newProxyInstance(
                realConnection.getClass().getClassLoader(),
                new Class[]{PooledConnection.class},
                new ConnectionProxyHandler(realConnection, connectionPool)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 更新最后使用时间
        updateLastUsedTime();
        if ("close".equals(method.getName())) {
//            log.info("close=");
            connectionPool.releaseConnection((PooledConnection) proxy);
//                connectionPool.releaseConnection((Connection) proxy);
//            }
            return null;
        } else if ("forceClose".equals(method.getName())) {
            // 真实关闭连接，不返回到池
//            log.info("forceClose=");
            realConnection.close();  // 真正关闭物理连接
            return null;
        }

        // 代理其他方法
        return method.invoke(realConnection, args);
    }

    protected boolean isTimedOut() {
        return (System.currentTimeMillis() - lastUsedTime) > TIMEOUT_MS;
    }


    protected void updateLastUsedTime() {
        this.lastUsedTime = System.currentTimeMillis();
    }
}
