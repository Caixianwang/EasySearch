package ca.wisecode.lucene.master;

import ca.wisecode.lucene.common.util.HashUtil;
import ca.wisecode.lucene.master.grpc.server.index.dao.IndexDAO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;


/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 9:18 AM
 * @Version: 1.0
 * @description:
 */
@SpringBootTest
@Slf4j
public class UniqueTest {

    @Autowired
    private IndexDAO uniqueIndex;

    @Test
    public void test05() throws Exception {
        uniqueIndex.insert("123");

    }
    @Test
    public void test04() throws Exception {
        uniqueIndex.delete("123");
    }
    @Test
    public void test03() throws Exception {
        String ids[] = {"1","2"};

        boolean exists = uniqueIndex.allNotExist(List.of(ids));
        log.info(String.valueOf(exists));
    }

    @Test
    public void test02() throws Exception {
        boolean exists = uniqueIndex.exists("1212");
        log.info(String.valueOf(exists));
    }
    @Test
    public void test01() throws Exception {
        for (int i = 0; i < 100; i++) {
            String hashCode = HashUtil.getInstance().calcHash("" + new Random().nextDouble());
            uniqueIndex.insert(hashCode);
        }
    }
}
