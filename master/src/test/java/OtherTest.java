import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.common.model.PrjMeta;
import ca.wisecode.lucene.grpc.models.Cell;
import ca.wisecode.lucene.grpc.models.Row;
import ca.wisecode.lucene.master.service.project.ProjectBS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/17/2024 1:28 AM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class OtherTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test04() throws Exception {
        ProjectBS projectBS = new ProjectBS();
        PrjMeta prjMeta = projectBS.readProjByID("000");
        log.info(prjMeta.getPrjID());
        List<FieldMeta> fields = prjMeta.getFields();
        for (FieldMeta fieldMeta:fields){
            log.info(fieldMeta.getName()+"-"+fieldMeta.getType());
        }

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
                String fieldValue = document.get(fieldName);  // 获取存储的值
                // 如果是存储的字段，输出字段名和对应的值
                if (fieldValue != null) {
                    log.info(fieldName + ": "+ fieldValue+":"+field.fieldType().omitNorms());
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
    public void test05() {
        Row.Builder builder = Row.newBuilder();
        Cell cell = Cell.newBuilder().setName("addr").setType("text").build();
        cell = null;
        builder.addCells(cell);
        Row row = builder.build();

        log.info(row.toString());
    }


    @Test
    public void test03() throws JsonProcessingException {

        // 要转换的日期字符串
        String dateString = "12:12:12"; // 可以根据需要更改格式
        LocalTime time = LocalTime.parse(dateString, DateTimeFormatter.ofPattern("HH:mm:ss"));
        log.info("" + time.toNanoOfDay() / 1000000);
        // 定义日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            // 将字符串转换为 Date 对象
            Date date = dateFormat.parse(dateString);
            System.out.println("Converted Date: " + date);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }

    }

    @Test
    public void test02() throws JsonProcessingException {

        Map<String, Object> root = new HashMap<>();
        root.put("_PRJ_", "001");
        root.put("rows", new ArrayList<>());

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("name", "AAA");
        rowMap.put("age", 20);
        ((List) root.get("rows")).add(rowMap);
        rowMap = new HashMap<>();
        rowMap.put("name", "BBB");
        rowMap.put("age", 25);
        ((List) root.get("rows")).add(rowMap);

        String jsonString = objectMapper.writeValueAsString(root);
        JsonNode jsonNode = objectMapper.convertValue(root, JsonNode.class);
        log.info(jsonString);
        log.info(jsonNode.toString());

    }

    @Test
    public void test01() {
        String jsonString = "{\"info\":20,\"rows\":[{\"createdAt\": \"2023-09-22T14:00:00Z\", \"age\": 30,\"birthDate\": \"2023-09-22\"}, {\"name\": \"Bob\", \"age\": \"2023-11-08 19:52:19\"}]}";
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            log.info(rootNode.toString());
            log.info(rootNode.textValue());
            JsonNode rowsNode = rootNode.get("rows");
            if (rowsNode.isArray()) {
                StringBuilder sb = new StringBuilder();
                for (JsonNode row : rowsNode) {
                    sb.setLength(0);
                    // 遍历 JSON 对象中的每个键值对
                    Iterator<Map.Entry<String, JsonNode>> fieldsIterator = row.fields();
                    while (fieldsIterator.hasNext()) {
                        Map.Entry<String, JsonNode> field = fieldsIterator.next();
                        JsonNode valueNode = field.getValue();
                        if (valueNode.isTextual()) {
                            String textValue = valueNode.asText();
                            if (isValidDate(textValue)) {
                                System.out.println(field.getKey() + " 是日期，值为: " + textValue);
                            } else {
                                System.out.println(field.getKey() + " 是字符串，值为: " + textValue);
                            }
                        }
                        sb.append(field.getValue().asText());
                    }
                    log.info(sb.toString());
                }
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidDate(String dateStr) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
        try {
            LocalDate.parse(dateStr, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
