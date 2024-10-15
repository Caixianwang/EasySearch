package ca.wisecode.lucene.slave;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.slave.grpc.client.service.HealthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 9:18 AM
 * @Version: 1.0
 * @description:
 */
@SpringBootTest
@Slf4j
public class LuceneTest {
    @Autowired
    private IndexWriter indexWriter;

    @Autowired
    private DirectoryReader reader;

    @Test
    public void test01()  throws Exception {
        IndexSearcher searcher = new IndexSearcher(reader);
        TermQuery a;

        log.info("======================================");
    }
}
