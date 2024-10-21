import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/18/2024 2:19 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class LuceneBase {

    @Test
    public void query01() throws Exception {
        Directory index = FSDirectory.open(Paths.get("/home/search/index4"));
        Analyzer analyzer = new IKAnalyzer();
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 3.1 使用 QueryParser 解析分词字段（content）的查询
        QueryParser parser = new QueryParser("content", analyzer);
        Query contentQuery = parser.parse("构建系统");

        // 3.2 查询作者为 "张三"
        Query authorQuery = new WildcardQuery(new Term("author", "*李四*"));

        // 3.3 使用 BooleanQuery 组合查询
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        booleanQuery.add(authorQuery, BooleanClause.Occur.MUST);
        booleanQuery.add(contentQuery, BooleanClause.Occur.MUST);

        Query combinedQuery = booleanQuery.build();

        executeQuery(searcher, combinedQuery);
        // 4. 关闭资源
        reader.close();
        index.close();
    }

    @Test
    public void insert01() throws Exception {
        Directory index = FSDirectory.open(Paths.get("/home/search/index4"));
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        // 2. 添加中文文档到索引
        addDoc(writer, "张三", "今天我们讨论了Lucene搜索引擎的工作原理。");
        addDoc(writer, "李四", "Lucene 是一个高效的全文检索库，非常适合构建搜索系统。");
        addDoc(writer, "王五", "本文将详细介绍如何使用Lucene和IKAnalyzer构建中文搜索。");
        writer.close();  // 关闭写入器
        index.close();

    }

    private static void executeQuery(IndexSearcher searcher, Query query) throws IOException {
        TopDocs results = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            log.info("Found document: ");
            log.info("Author: " + doc.get("author"));
            log.info("Content: " + doc.get("content"));
        }
    }

    private static void addDoc(IndexWriter writer, String author, String content) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("author", author, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));  // 使用 TextField 进行分词
        writer.addDocument(doc);
    }

    @Test
    public void test01() {
        String text = "刘超";
        // 使用不同的分析器
        Analyzer[] analyzers;
        analyzers = new Analyzer[]{
                new StandardAnalyzer(),           // 标准分析器
                new EnglishAnalyzer(),            // 英文分析器
                new IKAnalyzer(true)
        };

        for (Analyzer analyzer : analyzers) {
            log.info("Using Analyzer: " + analyzer.getClass().getSimpleName());
            try (TokenStream tokenStream = analyzer.tokenStream("field", new StringReader(text))) {
                CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
                tokenStream.reset();
                StringBuilder sb = new StringBuilder();
                while (tokenStream.incrementToken()) {
                    sb.append(charTermAttribute.toString() + " | ");
                }
                log.info(sb.toString());

                tokenStream.end();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("---------------------");
        }
    }
}
