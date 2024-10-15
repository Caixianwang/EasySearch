package ca.wisecode.lucene.slave.grpc.server.index.template;

import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/30/2024 2:59 PM
 * @Version: 1.0
 * @description:
 */

@FunctionalInterface
public interface IndexOperation {
    int execute(IndexWriter indexWriter) throws IOException;
}
