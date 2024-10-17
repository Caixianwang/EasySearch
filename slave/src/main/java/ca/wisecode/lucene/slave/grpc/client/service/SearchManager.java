package ca.wisecode.lucene.slave.grpc.client.service;

import ca.wisecode.lucene.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/30/2024 9:49 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class SearchManager {

    private final Directory directory;
    private AtomicReference<IndexReader> currentReader;
    private AtomicReference<IndexSearcher> currentSearcher;

    public SearchManager(Directory directory) throws IOException {
        this.directory = directory;
        this.currentReader = new AtomicReference<>(DirectoryReader.open(directory));
        this.currentSearcher = new AtomicReference<>(new IndexSearcher(currentReader.get()));
    }

    public IndexSearcher getSearcher() {
        try {
            DirectoryReader newReader = DirectoryReader.openIfChanged((DirectoryReader) currentReader.get());
            if (newReader != null) {
                // 如果索引发生了变化，创建一个新的 IndexSearcher
                IndexReader oldReader = currentReader.get();
                currentReader.set(newReader);
                currentSearcher.set(new IndexSearcher(newReader));
//                oldReader.decRef();
            }
            // 返回缓存的 IndexSearcher
            return currentSearcher.get();
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    public IndexReader getReader() {
        try {
            DirectoryReader newReader = DirectoryReader.openIfChanged((DirectoryReader) currentReader.get());
            if (newReader != null) {
                // 如果索引发生了变化，创建一个新的 IndexSearcher
                IndexReader oldReader = currentReader.get();
                currentReader.set(newReader);
                currentSearcher.set(new IndexSearcher(newReader));
//                oldReader.decRef();
                return newReader;
            } else {
                return currentReader.get();
            }

        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

//    public synchronized IndexSearcher getSearcher() {
//        DirectoryReader newReader = null;  // 检查是否有新索引
//        try {
//            newReader = DirectoryReader.openIfChanged(reader);
//            if (newReader != null) {
//                // 如果索引有变动，更新 reader 和 searcher
//                reader.close(); // 关闭旧的 reader
//                reader = newReader;
//                searcher = new IndexSearcher(reader);
//                reader.decRef();
//
//            } else {
//                if (searcher == null) {
//                    searcher = new IndexSearcher(reader);
//                }
//            }
//            return searcher;  // 返回最新的 IndexSearcher
//        } catch (IOException e) {
//            throw new BusinessException(e);
//        }
//
//    }
//
//    public synchronized DirectoryReader getDirectoryReader() {
//        DirectoryReader newReader = null;  // 检查是否有新索引
//        try {
//            newReader = DirectoryReader.openIfChanged(reader);
//            if (newReader != null) {
//                // 如果索引有变动，更新 reader 和 searcher
//                reader.close(); // 关闭旧的 reader
//                reader = newReader;
//                searcher = new IndexSearcher(reader);
//            }
//            return reader;  // 返回最新的 IndexSearcher
//        } catch (IOException e) {
//            throw new BusinessException(e);
//        }
//
//    }
}
