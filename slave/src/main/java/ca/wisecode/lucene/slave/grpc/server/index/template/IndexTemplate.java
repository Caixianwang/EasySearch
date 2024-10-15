package ca.wisecode.lucene.slave.grpc.server.index.template;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.slave.service.StateBS;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/30/2024 2:55 PM
 * @Version: 1.0
 * @description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndexTemplate {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final IndexWriter indexWriter;
    private final StateBS stateBS;
    private final Object lock = new Object();

    public void addIndex(IndexOperation indexOperation) {
        try {
            int size = indexOperation.execute(indexWriter);
            indexWriter.flush();
            synchronized (lock) {
                int total = stateBS.getNewAddIndexSize() + size;
                stateBS.setNewAddIndexSize(total);
                if (total >= Constants.MEMORY_MAX_INDEX) {
                    indexWriter.commit();
                    stateBS.setNewAddIndexSize(0);
                }
            }
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
}
