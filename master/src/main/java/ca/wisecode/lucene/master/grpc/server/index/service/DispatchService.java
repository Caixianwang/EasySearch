package ca.wisecode.lucene.master.grpc.server.index.service;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.*;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import ca.wisecode.lucene.master.grpc.server.index.dao.IndexDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:35 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class DispatchService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private IDService idService;
    @Autowired
    private MasterNode masterNode;
    @Autowired
    private IndexDAO indexDAO;

    /**
     * @param rowsRequest
     */
    public void dispatchRows(final RowsRequest rowsRequest) {
        if (rowsRequest.getRowsList().size() == Constants.INSERT_ROWS) {
            insertRowOfNode(rowsRequest);
        } else {
            Iterator<Row> iterator = rowsRequest.getRowsList().iterator();
            List<Row> rows = new ArrayList<>();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                rows.add(row);
                if (rows.size() == Constants.INSERT_ROWS) {
                    this.handleRowsRequest(rows);
                }
            }
            if (!rows.isEmpty()) {
                this.handleRowsRequest(rows);
            }
        }
    }

    public void dispatchTable(final RowsRequest rowsRequest) {
        Iterator<Row> iterator = rowsRequest.getRowsList().iterator();
        List<Row> rows = new ArrayList<>();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            rows.add(row);
            if (rows.size() == Constants.INSERT_ROWS) {
                this.handleTableRequest(rows);
            }
        }
        if (!rows.isEmpty()) {
            this.handleTableRequest(rows);
        }
    }

    private void handleTableRequest(List<Row> rows) {
        TableRequest tableRequest = getTableRequest(rows);
        insertRowOfNode(tableRequest);
        insertIds(rows);
        rows.clear();
    }

    private TableRequest getTableRequest(RowsRequest rowsRequest) {
        return TableRequest.newBuilder()
                .setAuthor(Constants.MY_NAME).
                setRowsRequest(rowsRequest).build();
    }

    private void handleRowsRequest(List<Row> rows) {
        RowsRequest rowsRequest = getRowsRequest(rows);
        insertRowOfNode(rowsRequest);
        insertIds(rows);
        rows.clear();
    }

    private TableRequest getTableRequest(List<Row> rows) {
        RowsRequest.Builder builder = RowsRequest.newBuilder();
        for (Row row : rows) {
            builder.addRows(row);
        }
        return TableRequest.newBuilder()
                .setAuthor(Constants.MY_NAME).
                setRowsRequest(builder.build()).build();
    }

    private RowsRequest getRowsRequest(List<Row> rows) {
        RowsRequest.Builder builder = RowsRequest.newBuilder();
        for (Row row : rows) {
            builder.addRows(row);
        }
        return builder.build();
    }

    public void dispatchTable(final TableRequest tableRequest) {
        this.insertRowOfNode(tableRequest);
    }

    /**
     * 向节点插入索引数据
     *
     * @param tableRequest
     */
    private void insertRowOfNode(TableRequest tableRequest) {

        ManagedChannel managedChannel = randomManagedChannel();
        IndexServiceGrpc.IndexServiceBlockingStub stub = IndexServiceGrpc.newBlockingStub(managedChannel);
        JsonOut jsonOut = stub.insertTable(tableRequest);
    }

    private void insertRowOfNode(RowsRequest rowsRequest) {

        ManagedChannel managedChannel = randomManagedChannel();
        IndexServiceGrpc.IndexServiceBlockingStub stub = IndexServiceGrpc.newBlockingStub(managedChannel);
        JsonOut jsonOut = stub.insertRows(rowsRequest);
    }

    private ManagedChannel randomManagedChannel() {
        List<NodeChannel> nodeChannels = masterNode.availableChannels();
        if (nodeChannels.isEmpty()) {
            throw new BusinessException("No nodes are available.");
        } else {
            Random random = new Random();
            NodeChannel nodeChannel = nodeChannels.get(random.nextInt(nodeChannels.size()));
            return nodeChannel.getChannel();
        }
    }

    private void insertIds(List<Row> rows) {
        List<String> ids = new ArrayList<>();
        for (Row row : rows) {
            Iterator<Cell> iterator = row.getCellsList().iterator();
            while (iterator.hasNext()) {
                Cell cell = iterator.next();
                if (cell.getName().equals(Constants._ID_)) {
                    ids.add(cell.getStringVal());
                    break;
                }
            }
        }
        indexDAO.insertBatch(ids);
    }

}
