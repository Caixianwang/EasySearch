package ca.wisecode.lucene.common.sqlite;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/18/2024 1:34 PM
 * @Version: 1.0
 * @description:
 */
@FunctionalInterface
public interface DBOperation {
    void execute(Connection connection) throws SQLException;
}
