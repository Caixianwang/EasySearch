import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/17/2024 1:28 AM
 * @Version: 1.0
 * @description:
 */

public class SqliteTest {
    @Test
    public void test02() {
        List<String> l = new ArrayList<>();
        LinkedList<List> list = new LinkedList<>();

        list.add(l);
        list.add(list.get(0));
        list.add(list.get(0));

        System.out.println(list.size());

    }

    @Test
    public void test01() {
        URL resource = SQLiteConfig.class.getClassLoader().getResource("test.db");
        System.out.println(resource.getPath().toString());
        if (resource == null) {
            throw new IllegalArgumentException("Database file not found in resources!");
        }

        // 将 URL 转换为文件路径
        String databasePath = resource.getPath().toString();
        String databaseFile = "jdbc:sqlite:test.db";
        System.out.println(databaseFile);
        try (Connection conn = DriverManager.getConnection(databaseFile)) {
            if (conn != null) {
                // 创建表
//                String createTableSQL = "CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY, name TEXT UNIQUE)";
//                try (var stmt = conn.createStatement()) {
//                    stmt.execute(createTableSQL);
//                }
                conn.setAutoCommit(false); // 开启事务
                // 插入数据
                String insertSQL = "INSERT OR IGNORE INTO items (name) VALUES (?)";
//                try (var pstmt = conn.prepareStatement(insertSQL)) {
//                    long x = System.currentTimeMillis();
//                    for (int i = 10000000; i < 100000000; i++) {
//
//                        pstmt.setString(1, "315f5bdb76d078c43b8ac0064e4a0164612b1fce77c869345bfc94c75894edd3" + i);
//                        pstmt.addBatch(); //
//                        if (i % 50000 == 0) { // 每100条提交一次批处理
//                            pstmt.executeBatch();
//                            conn.commit();
//                        }
//                    }
//                    pstmt.executeBatch();
//                    conn.commit();
////                    if (pstmt != null) pstmt.close();
//                    long y = System.currentTimeMillis();
//                    System.out.println(y - x);
////                    pstmt.setString(1, "item5");
////                    pstmt.executeUpdate();
////                    pstmt.setString(1, "item6");
////                    pstmt.executeUpdate();
//                }

                // 查询数据
                String querySQL = "SELECT COUNT(*) FROM items WHERE name = ?";
//                String querySQL = "SELECT COUNT(*) FROM items ";
                try (var pstmt = conn.prepareStatement(querySQL)) {
                    pstmt.setString(1, "315f5bdb76d078c43b8ac0064e4a0164612b1fce77c869345bfc94c75894edd3100000");
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            System.out.println(count);
                        }
                    }

//                    pstmt.setString(1, "item4");
//                    try (ResultSet rs = pstmt.executeQuery()) {
//                        if (rs.next()) {
//                            int count = rs.getInt(1);
//                            System.out.println(count > 0 ? "item4 exists" : "item4 does not exist");
//                        }
//                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
