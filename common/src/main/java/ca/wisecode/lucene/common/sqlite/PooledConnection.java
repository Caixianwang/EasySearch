package ca.wisecode.lucene.common.sqlite;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/20/2024 12:24 PM
 * @Version: 1.0
 * @description:
 */

public interface PooledConnection extends Connection {
    void forceClose() throws SQLException; // 添加 forceClose 方法
}
