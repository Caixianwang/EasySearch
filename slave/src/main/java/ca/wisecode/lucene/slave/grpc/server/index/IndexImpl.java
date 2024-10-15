package ca.wisecode.lucene.slave.grpc.server.index;

import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.grpc.models.*;
import ca.wisecode.lucene.slave.grpc.server.index.template.IndexTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.*;
import org.springframework.stereotype.Service;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:35 PM
 * @Version: 1.0
 * @description:
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexImpl extends IndexBase {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final IndexTemplate indexTemplate;

    public JsonOut insertTable(final TableRequest table) {
        String author = table.getAuthor();
        return insertRows(table.getRowsRequest());
    }

    public JsonOut insertRows(RowsRequest rowsRequest) {
        final int[] sizeHolder = new int[1];
        indexTemplate.addIndex(indexWriter -> {
            for (Row row : rowsRequest.getRowsList()) {
                Document doc = new Document();
                for (Cell cell : row.getCellsList()) {
                    switch (cell.getType()) {
                        case FieldMeta.Type.STRING -> {
                            doc.add(new StringField(cell.getName(), cell.getStringVal(), Field.Store.YES));
                        }
                        case FieldMeta.Type.TEXT -> {
                            doc.add(new TextField(cell.getName(), cell.getStringVal(), Field.Store.YES));
                        }
                        case FieldMeta.Type.DOUBLE -> {
                            doc.add(new DoublePoint(cell.getName(), cell.getDoubleVal()));
                            doc.add(new StoredField(cell.getName(), cell.getDoubleVal()));
                        }
                        case FieldMeta.Type.LONG -> {
                            doc.add(new LongPoint(cell.getName(), cell.getLongVal()));
                            doc.add(new StoredField(cell.getName(), cell.getLongVal()));
                        }
                    }
                }
                indexWriter.addDocument(doc);
            }
            sizeHolder[0] = rowsRequest.getRowsCount();
            return sizeHolder[0];
        });
        String msg = String.format("{\"code\":200,\"rows\":\"%d\"}", sizeHolder[0]);
        return JsonOut.newBuilder().setMsg(msg).build();
    }

}

