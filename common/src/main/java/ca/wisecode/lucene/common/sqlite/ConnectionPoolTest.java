package ca.wisecode.lucene.common.sqlite;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/18/2024 2:42 PM
 * @Version: 1.0
 * @description:
 */

public class ConnectionPoolTest {
    public static void main(String[] args) {
        SQLiteTemplate.execute(connection -> {
            // 创建表
            System.out.println("CREATE TABLE-------------------------"+connection);
            String createTableSQL = "CREATE TABLE IF NOT EXISTS ids (id TEXT PRIMARY KEY)";
            try (var stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
            }
        });

// 使用模板方法并传递 Lambda 表达式，插入数据
        SQLiteTemplate.execute(connection -> {
            System.out.println("insert-------------------------"+connection);
            String sql = "INSERT or ignore INTO ids (id) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "TestItem");
                statement.executeUpdate();
                System.out.println("Item inserted successfully.");
            }
        });

        // 使用模板方法并传递 Lambda 表达式，查询数据
        SQLiteTemplate.execute(connection -> {
            System.out.println("query1-------------------------"+connection);
            String sql = "SELECT id FROM ids";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.executeQuery();
                System.out.println("1.Query executed successfully.");
            }
        });
        // 使用模板方法并传递 Lambda 表达式，查询数据
        SQLiteTemplate.execute(connection -> {
            System.out.println("query2-------------------------"+connection);
            String sql = "SELECT id FROM ids";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeQuery();
                System.out.println("2.Query executed successfully.");
            }
        });



        // 关闭连接池
        try {
            Thread.sleep(60000);
            SQLiteTemplate.shutdownPool();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
