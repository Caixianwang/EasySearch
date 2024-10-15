package ca.wisecode.lucene.common.sqlite;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/18/2024 1:37 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class SQLiteTemplate {

    // 模板方法，接受业务逻辑作为参数
    public static void execute(DBOperation operation) {
        Connection connection = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            operation.execute(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // 3. 关闭连接或释放到连接池,非真的关闭连接
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void initDatabaseUrl(String url) {
        ConnectionFactory.initDatabaseUrl(url);
    }
    public static void shutdownPool() throws SQLException{
        ConnectionPool.getInstance().shutdownPool();
    }

    // 可选：处理异常
    private static void handleException(SQLException e) {
        log.error(e.getMessage());
    }
}
