import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.model.QueryLogic;
import ca.wisecode.lucene.common.model.QueryMode;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.FilterRule;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import ca.wisecode.lucene.slave.grpc.server.query.mode.AbstractQuery;
import ca.wisecode.lucene.slave.grpc.server.query.mode.ChainQueryFactory;
import ca.wisecode.lucene.slave.grpc.server.query.mode.IWrapQuery;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 1:30 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class OtherTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void t0003() throws Exception {
        List<NodeChannel> channels = new ArrayList<>();
        String s = objectMapper.writeValueAsString(channels);
        log.info(channels.toString());
    }

    @Test
    public void t0002() throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/home/search/index3"));
        SearchManager searchManager = new SearchManager(directory);
        IndexSearcher searcher = searchManager.getSearcher();
        Analyzer analyzer = new IKAnalyzer(true);
        List<FilterRule> filterRulesList = new ArrayList<>();
        FilterRule filterRule1 = FilterRule.newBuilder()
                .setName("author")
                .setValue("刘超")
                .setQueryMode(QueryMode.Term)
                .setLogic(QueryLogic.AND).build();
//        filterRulesList.add(filterRule1);
        FilterRule filterRule2 = FilterRule.newBuilder()
                .setName("content")
                .setValue("健康饮食")
                .setQueryMode(QueryMode.Parser)
                .setLogic(QueryLogic.AND).build();
        filterRulesList.add(filterRule2);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        IWrapQuery iWrapQuery = ChainQueryFactory.getInstance().buildQuery(analyzer);
        for (FilterRule filterRule : filterRulesList) {
            iWrapQuery.build(builder,filterRule);
        }

        BooleanQuery query = builder.build();
        log.info(builder.build().toString());

        TopDocs topDocs = searcher.search(query, 10);
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<b>", "</b>");
        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
        log.info(topDocs.scoreDocs.length + "");
        List<Map<String, String>> list = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            Map<String, String> documentMap = new HashMap<>();
            // 获取文档的所有字段
            for (IndexableField field : doc.getFields()) {
                String fieldName = field.name();
                String fieldValue = doc.get(fieldName);
                String highlightedText = highlighter.getBestFragment(analyzer, "content", fieldValue);
                if (highlightedText != null) {
                    System.out.println("Highlighted: " + highlightedText);
                } else {
                    System.out.println("No highlights found for: " + fieldValue);
                }
                documentMap.put(fieldName, fieldValue);
            }
            list.add(documentMap);

        }
        String jsonString = objectMapper.writeValueAsString(list);
        List<Map<String, String>> listq = objectMapper.readValue(jsonString, new TypeReference<List<Map<String, String>>>() {});
        for(Map<String,String> map:listq){
            log.info(map.toString());
        }

    }

    @Test
    public void insert001() throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/home/search/index5"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Document doc = new Document();

        doc.add(new StringField("author", "刘超", Field.Store.YES));
        doc.add(new TextField("title", "效率想法思路社区趋势支持旅行信息心得交流", Field.Store.YES));
        indexWriter.addDocument(doc);
        doc = new Document();
        doc.add(new StringField("author", "刘敏", Field.Store.YES));
        doc.add(new TextField("title", "挑战经验失败总结潜力目标联系建议学习合作", Field.Store.YES));
        indexWriter.addDocument(doc);
        doc = new Document();
        doc.add(new StringField("author", "陈敏", Field.Store.YES));
        doc.add(new TextField("title", "成长乐趣工具技术成果挑战建议旅行实现成果", Field.Store.YES));
        indexWriter.addDocument(doc);

        indexWriter.commit();
        indexWriter.close();
        directory.close();
    }

    @Test
    public void t0001() throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/home/search/index"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));

        // 检查目录中是否存在索引，如果不存在则创建
        if (!DirectoryReader.indexExists(directory)) {
            System.out.println("索引不存在，正在创建一个空索引...");
            IndexWriter indexWriter = new IndexWriter(directory, config);
            indexWriter.commit(); // 提交空索引
            indexWriter.close();
        }

        // 打开 DirectoryReader
        DirectoryReader reader = DirectoryReader.open(directory);
        reader.close();
        directory.close();
    }


    @Test
    public void pageQuery() throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/home/search/index"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);
        SearchManager searchManager = new SearchManager(directory);
//        reader = searchManager.getReader();
//        log.info("" + reader.numDocs());
        ScoreDoc lastScoreDoc = null;
        MatchAllDocsQuery query = new MatchAllDocsQuery();  // 可替换为其他查询

        List<String> deleteIds = new ArrayList<>();
        IndexSearcher searcher = searchManager.getSearcher();
        TopDocs topDocs = searcher.searchAfter(lastScoreDoc, query, 100);
        while (topDocs.scoreDocs.length > 0) {

            List<ScoreDoc> scoreDocsList = new ArrayList<>();
            int lastIndex = topDocs.scoreDocs.length - 1;
            // 将除最后一个元素外的所有元素加入到列表中
            for (int i = 0; i < lastIndex; i++) {
                scoreDocsList.add(topDocs.scoreDocs[i]);
            }

            List<ScoreDoc> scoreDocs = scoreDocsList.subList(0, (int) (scoreDocsList.size() * 0.5));

            for (ScoreDoc scoreDoc : scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
//                log.info(doc.get(Constants._ID_));
                deleteIds.add(doc.get(Constants._ID_));
            }
            this.deleteDocByIds(indexWriter, deleteIds);
//            Document doc = new Document();
//            doc.add(new StringField(Constants._ID_, "id", Field.Store.YES));
//            doc.add(new StringField(Constants._PRJID_, "000", Field.Store.YES));
//            indexWriter.addDocument(doc);
//            indexWriter.commit();

            lastScoreDoc = topDocs.scoreDocs[lastIndex];

//            searcher = this.getSearcher();
            log.info("-------------------" + searcher.doc(lastScoreDoc.doc).get(Constants._ID_));
//            Thread.sleep(500);
//            log.info(searcher.toString());
            topDocs = searcher.searchAfter(lastScoreDoc, query, 100);
        }
        if (!deleteIds.isEmpty()) {
            this.deleteDocByIds(indexWriter, deleteIds);
        }
    }

    @Test
    public void pageQuery1() throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/home/search/index"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);
        SearchManager searchManager = new SearchManager(directory);
        MatchAllDocsQuery query = new MatchAllDocsQuery();  // 可替换为其他查询

        List<String> deleteIds = new ArrayList<>();
        IndexSearcher searcher = searchManager.getSearcher();
        TopDocs topDocs = searcher.search(query, 100);
        while (topDocs.scoreDocs.length > 0) {
            log.info("topDocs.scoreDocs.length=" + topDocs.scoreDocs.length);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
//                log.info(doc.get(Constants._ID_));
                deleteIds.add(doc.get(Constants._ID_));
            }
            this.deleteDocByIds(indexWriter, deleteIds);
            searcher = searchManager.getSearcher();
            topDocs = searcher.search(query, 100);
        }
        if (!deleteIds.isEmpty()) {
            this.deleteDocByIds(indexWriter, deleteIds);
        }
    }

    private void deleteDocByIds(IndexWriter indexWriter, List<String> ids) {
        Term[] deleteTerms = new Term[ids.size()];
        for (int i = 0; i < deleteTerms.length; i++) {
            deleteTerms[i] = new Term(Constants._ID_, ids.get(i));
            log.info("delete-----" + ids.get(i));
        }
        try {
            indexWriter.deleteDocuments(deleteTerms);
            indexWriter.commit();
            ids.clear();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    @Test
    public void deleteAll() throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/home/search/index3"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);

        indexWriter.deleteAll();
        indexWriter.commit();

        indexWriter.close();
        directory.close();

    }

    @Test
    public void test001() throws Exception {
        int cnt = 0;
        for (int i = 1; i < 4; i++) {
            Directory directory = FSDirectory.open(Paths.get("/home/search/index" + i));
            SearchManager searchManager = new SearchManager(directory);
            IndexReader reader = searchManager.getReader();
            int currCnt = reader.numDocs();
            cnt += currCnt;
            log.info("index" + i + " " + currCnt);
            reader.close();
            directory.close();
        }
        log.info("total " + cnt);
    }


    @Test
    public void insert() throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/home/search/index1"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);

        for (int i = 0; i < 10000000; i++) {
            Document doc = new Document();
            doc.add(new StringField(Constants._ID_, "id" + i, Field.Store.YES));
            doc.add(new StringField(Constants._PRJID_, "000", Field.Store.YES));
            doc.add(new StringField("author", "liu", Field.Store.YES));
            doc.add(new TextField("content", "test", Field.Store.YES));
            doc.add(new LongPoint("followers", 18));
            doc.add(new StoredField("followers", 18));
            doc.add(new DoublePoint("income", 22.5));
            doc.add(new StoredField("income", 22.5));
            doc.add(new LongPoint("created", 1718409600000L));
            doc.add(new StoredField("created", 1718409600000L));
            indexWriter.addDocument(doc);
        }
        indexWriter.commit();
        indexWriter.close();
        directory.close();
    }

    @Test
    public void insert01() throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/home/search/index1"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);


        Document doc = new Document();
        doc.add(new StringField(Constants._ID_, "id0", Field.Store.YES));
        doc.add(new LongPoint("followers", 18));
        indexWriter.addDocument(doc);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 10; i < 13; i++) {

                    Document doc = new Document();
                    doc.add(new StringField(Constants._ID_, "id" + i, Field.Store.YES));
                    doc.add(new LongPoint("followers", 18));
                    try {
                        Thread.sleep(500);
                        indexWriter.addDocument(doc);
                        indexWriter.commit();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 10; i < 13; i++) {
                    List<Document> documents = new ArrayList<>();
                    Document doc = new Document();
                    doc.add(new StringField(Constants._ID_, "A01", Field.Store.YES));
                    doc.add(new LongPoint("followers", 18));
                    documents.add(doc);
                    doc = new Document();
                    doc.add(new StringField(Constants._ID_, "B01", Field.Store.YES));
                    doc.add(new StringField("followers", "18", Field.Store.YES));
                    documents.add(doc);
                    try {
                        indexWriter.addDocuments(documents);
                        Thread.sleep(300);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
//                    indexWriter.commit();

            }
        });
        t2.start();
        t1.start();

        Thread.sleep(10000);

//        indexWriter.commit();
        indexWriter.close();
        directory.close();
    }


    @Test
    public void test07() throws Exception {

        Directory directory = FSDirectory.open(Paths.get("/home/search/index1"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);
        DirectoryReader directoryReader = DirectoryReader.open(indexWriter, true, true);

        IndexSearcher searcher = new IndexSearcher(directoryReader);
        ScoreDoc lastScoreDoc = null;
        Query query = new MatchAllDocsQuery();
        // 分批查询
        TopDocs topDocs = searcher.searchAfter(lastScoreDoc, query, 2);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc1 = searcher.doc(scoreDoc.doc);
            log.info(doc1.get("title"));
        }
        log.info("-------------------------------");
        // 更新最后一个 ScoreDoc，用于下一次分页查询
        lastScoreDoc = topDocs.scoreDocs[topDocs.scoreDocs.length - 1];
        Document doc = searcher.doc(lastScoreDoc.doc);
        log.info(doc.get("title"));
        log.info("================================");
        topDocs = searcher.searchAfter(lastScoreDoc, query, 2);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc1 = searcher.doc(scoreDoc.doc);
            log.info(doc1.get("title"));
        }
        // 更新最后一个 ScoreDoc，用于下一次分页查询
//        lastScoreDoc = topDocs.scoreDocs[topDocs.scoreDocs.length - 1];

    }

    @Test
    public void test06() throws Exception {

        Directory directory = FSDirectory.open(Paths.get("/home/search/index1"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);
        DirectoryReader directoryReader = DirectoryReader.open(indexWriter, true, true);
        IndexSearcher searcher = new IndexSearcher(directoryReader);
//        indexWriter.deleteAll();
//        indexWriter.commit();
        Document doc1 = new Document();
        doc1.add(new StringField("title", "Lucene11", Field.Store.YES));
        doc1.add(new IntPoint("age", 18));
        doc1.add(new StoredField("age", 18));
        indexWriter.addDocument(doc1);
        indexWriter.commit();
        indexWriter.deleteDocuments(new Term("title", new BytesRef("Lucene")));
        indexWriter.commit();
        Query query = new MatchAllDocsQuery();
        TopDocs docsFrom = searcher.search(query, 100);
        for (ScoreDoc scoreDoc : docsFrom.scoreDocs) {
//            log.info("" + scoreDoc.doc);
            if (scoreDoc.doc < 10) {

            }
            Document doc = searcher.doc(scoreDoc.doc);
            // 删除原索引中的该文档，假设有一个唯一字段 "docID"
            log.info(scoreDoc.doc + "-" + doc.get("title") + "-" + doc.get("age")); // 你需要根据实际情况选择合适的字段
            log.info(doc.toString());
        }

    }

    @Test
    public void test04() throws Exception {

        Directory directory = FSDirectory.open(Paths.get("/home/search/index"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);
        DirectoryReader directoryReader = DirectoryReader.open(indexWriter, true, true);
        log.info("" + directoryReader.numDocs());
        IndexSearcher searcher = new IndexSearcher(directoryReader);
        Query query = new MatchAllDocsQuery();
        TopDocs docsFrom = searcher.search(query, 100);
        for (ScoreDoc scoreDoc : docsFrom.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            for (IndexableField field : document.getFields()) {
                String fieldName = field.name();
                log.info("" + document.getField("followers").numericValue());
                String fieldValue = document.get(fieldName);  // 获取存储的值
                // 如果是存储的字段，输出字段名和对应的值
                if (fieldValue != null) {
                    log.info(fieldName + ": " + field.fieldType().indexOptions().name() + ":" + field.fieldType().omitNorms());
                } else {
                    // 如果没有存储值，可能是索引字段（如 LongPoint 或 IntPoint）
                    System.out.println(fieldName + " is indexed but not stored.");
                }
            }
            break;
        }

        indexWriter.close();
        directory.close();


    }

    @Test
    public void test03() throws Exception {

        Directory directory = FSDirectory.open(Paths.get("/home/search/index"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);
        DirectoryReader directoryReader = DirectoryReader.open(indexWriter, true, true);
        log.info("" + directoryReader.numDocs());
        IndexSearcher searcher = new IndexSearcher(directoryReader);
        Query query = new MatchAllDocsQuery();
        TopDocs docsFrom = searcher.search(query, 100);
        for (ScoreDoc scoreDoc : docsFrom.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            // 删除原索引中的该文档，假设有一个唯一字段 "docID"
            String docID = doc.get(Constants._ID_); // 你需要根据实际情况选择合适的字段
            if (docID.equals("7859838883263782b79d2d2d3ed7360bd717761e968bf7b8738ff1b484efaada")) {
                log.info("okokok");
                indexWriter.deleteDocuments(new Term(Constants._ID_, docID));
                indexWriter.commit();
            }
            log.info(doc.toString());
        }
        DirectoryReader newDirectoryReader = DirectoryReader.openIfChanged(directoryReader);  // 检查是否有新索引
        if (newDirectoryReader != null) {
            log.info("1111111111111111111111111111");
            // 如果索引有变动，更新 reader 和 searcher
//            directoryReader.close(); // 关闭旧的 reader
//            directoryReader = newDirectoryReader;
//            searcher = new IndexSearcher(directoryReader);
        } else {
//            searcher = new IndexSearcher(directoryReader);
        }
        directoryReader.close();
    }

    @Test
    public void test02() throws Exception {
        Document doc = new Document();
        doc.add(new StringField("title", "Lucene in Action", org.apache.lucene.document.Field.Store.YES));
        doc.add(new TextField("content", "Lucene is a powerful search library", org.apache.lucene.document.Field.Store.YES));

        // 遍历文档中的所有字段
        for (IndexableField field : doc.getFields()) {
            // 通过反射获取字段的具体类型
            System.out.println("Field Name: " + field.name());
            System.out.println("Field Type: " + field.getClass().getSimpleName());
        }
    }

    @Test
    public void test01() throws Exception {

        Directory directory = FSDirectory.open(Paths.get("/home/search/index1"));
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer(true));
        IndexWriter indexWriter = new IndexWriter(directory, config);
        DirectoryReader directoryReader = DirectoryReader.open(indexWriter, true, true);
//        indexWriter.deleteAll();
//        indexWriter.commit();
        Document doc1 = new Document();
        doc1.add(new StringField("title", "5", Field.Store.YES));
        indexWriter.addDocument(doc1);

        Document doc2 = new Document();
        doc2.add(new StringField("title", "6", Field.Store.YES));
        indexWriter.addDocument(doc2);

        indexWriter.commit();


        directoryReader.close();
        directory.close();

    }

    // 将字节数组转换为十六进制字符串
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
