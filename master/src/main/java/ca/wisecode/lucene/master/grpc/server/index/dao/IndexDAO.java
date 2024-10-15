package ca.wisecode.lucene.master.grpc.server.index.dao;

import ca.wisecode.lucene.common.sqlite.SQLiteTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/19/2024 1:31 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class IndexDAO {

    public IndexDAO() {
        initTable();
    }

    private void initTable() {
        SQLiteTemplate.execute(connection -> {
            // 创建表
            String createTableSQL = "CREATE TABLE IF NOT EXISTS ids (id TEXT PRIMARY KEY)";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
            }
        });
    }

    public void insert(String id) {
        SQLiteTemplate.execute(connection -> {
            String sql = "INSERT INTO ids (id) VALUES (?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, id);
                int rowsAffected = pstmt.executeUpdate();
                log.info("rowsAffected="+rowsAffected);
            }
        });
    }

    public void delete(String id) {
        SQLiteTemplate.execute(connection -> {
            String sql = "DELETE FROM ids WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, id);
                int rowsAffected = pstmt.executeUpdate();
                log.info("rowsAffected="+rowsAffected);
            }
        });
    }

    public void insertBatch(List<String> ids) {
        SQLiteTemplate.execute(connection -> {
            connection.setAutoCommit(false); // 开启事务
            String sql = "INSERT INTO ids (id) VALUES (?)";
            try (var pstmt = connection.prepareStatement(sql)) {
                long x = System.currentTimeMillis();
                for (int i = 0; i < ids.size(); i++) {
                    pstmt.setString(1, ids.get(i));
                    pstmt.addBatch(); //
                    if (i % 100 == 0) { // 每100条提交一次批处理
                        pstmt.executeBatch();
                        connection.commit();
                    }
                }
                pstmt.executeBatch();
                connection.commit();
            } finally {
                connection.setAutoCommit(true);
            }
        });
    }

    public boolean exists(String id) {
        final int[] count = {0};
        SQLiteTemplate.execute(connection -> {
            String sql = "SELECT COUNT(*) FROM ids WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        count[0] = rs.getInt(1);
                    }
                }
            }
        });
        return count[0] == 1;
    }

    /**
     * 在表里，所有的id都不存在
     *
     * @param ids
     * @return
     */
    public boolean allNotExist(List<String> ids) {
        final boolean[] notExist = {true};
        SQLiteTemplate.execute(connection -> {
            String sql = "SELECT 1 FROM ids WHERE EXISTS (SELECT 1 FROM ids WHERE id = ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (String id : ids) {
                    pstmt.setString(1, id);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            notExist[0] = false;  // 找到 id，则设置为 false
                            break;  // 一旦找到，退出循环
                        }
                    }
                }
            }
        });
        return notExist[0];
    }
}
