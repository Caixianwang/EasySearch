package ca.wisecode.lucene.slave.grpc.server.manage.distribute;

import ca.wisecode.lucene.common.convert.CellFactory;
import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.common.model.PrjMeta;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.Cell;
import ca.wisecode.lucene.grpc.models.Row;
import ca.wisecode.lucene.grpc.models.RowsRequest;
import ca.wisecode.lucene.slave.grpc.client.service.ProjectService;
import org.apache.lucene.document.Document;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 8:56 PM
 * @Version: 1.0
 * @description:
 */

public class CovertData {
    private ProjectService projectService;

    public CovertData(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 转化gRPC接口需要的数据格式
     *
     * @param document
     * @return
     */
    public RowsRequest covert(Document document) {
        Row row = this.covertRow(document);
        return RowsRequest.newBuilder().addRows(row).build();
    }

    public RowsRequest covert(List<Document> documents) {
        RowsRequest.Builder builder = RowsRequest.newBuilder();

        for (Document doc : documents) {
            Row row = this.covertRow(doc);
            builder.addRows(row);
        }
        return builder.build();
    }

    private Row covertRow(Document document) {
        PrjMeta prjMeta = projectService.readProject(document.get(Constants._PRJID_));
        Row.Builder builder = Row.newBuilder();
        for (String key : new String[]{Constants._ID_, Constants._PRJID_}) {
            Cell cell = CellFactory.balanceConvert(FieldMeta.Type.STRING, null, key, document.get(key));
            builder.addCells(cell);
        }
        for (FieldMeta fieldMeta : prjMeta.getFields()) {
            String value = document.get(fieldMeta.getName());
            Cell cell = CellFactory.balanceConvert(fieldMeta.getType(), fieldMeta.getFormat(), fieldMeta.getName(), value);
            builder.addCells(cell);
        }
        return builder.build();
    }
}
