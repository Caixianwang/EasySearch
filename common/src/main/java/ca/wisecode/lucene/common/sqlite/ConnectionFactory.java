package ca.wisecode.lucene.common.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/18/2024 1:51 PM
 * @Version: 1.0
 * @description:
 */

public class ConnectionFactory {
    private static String DB_URL = "jdbc:sqlite:easy.db";

    // 工厂方法 - 创建数据库连接
    protected static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    protected static void initDatabaseUrl(String url) {
        DB_URL = "jdbc:sqlite:" + url;
    }

}
