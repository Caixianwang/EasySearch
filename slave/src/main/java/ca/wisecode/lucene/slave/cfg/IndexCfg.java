package ca.wisecode.lucene.slave.cfg;


import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:02 PM
 * @Version: 1.0
 * @description:
 */
@Configuration
public class IndexCfg {
    @Value("${lucene.index-dir}")
    private String indexDir;

    @Bean
    public Directory luceneDirectory() throws IOException {
        return FSDirectory.open(Paths.get(indexDir));
    }

    @Bean
    public IndexWriterConfig indexWriterConfig() {
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        // 内存最大提交
        config.setMaxBufferedDocs(300);
        // 读写不实时
        config.setReaderPooling(false);
        // 定时合并
        config.setMergeScheduler(new SerialMergeScheduler());
        // 内存300
        config.setRAMBufferSizeMB(128);
        config.setCommitOnClose(true);
        return config;
    }

    @Bean
    public IndexWriter indexWriter(Directory directory, IndexWriterConfig config) throws IOException {
        return new IndexWriter(directory, config);
    }

    @Bean
    public DirectoryReader directoryReader(Directory directory) throws IOException {
        return DirectoryReader.open(directory);
    }


}

