package ca.wisecode.lucene.slave.grpc.server.manage.balance.dist;

import ca.wisecode.lucene.common.convert.CellFactory;
import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.grpc.GrpcUtils;
import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.common.model.PrjMeta;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.*;
import ca.wisecode.lucene.slave.grpc.client.service.ProjectService;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 1:38 AM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class DistributeCommand implements IndexCommand {
    private ProjectService projectService;
    private IndexWriter indexWriter;

    public DistributeCommand(ProjectService projectService, IndexWriter indexWriter) {
        this.projectService = projectService;
        this.indexWriter = indexWriter;
    }

    /**
     * 向所有目标节点发送索引数据
     *
     * @param searcher
     * @param destNodes
     */
    @Override
    public void execute(IndexSearcher searcher, List<DestNode> destNodes) {
        for (DestNode destNode : destNodes) {
            if (!destNode.getList().isEmpty()) {
                this.execute(searcher, destNode);
            }
        }
    }

    /**
     * 向目标节点发送索引数据
     *
     * @param searcher
     * @param destNode
     */
    private void execute(IndexSearcher searcher, DestNode destNode) {
        ManagedChannel channel = GrpcUtils.getManagedChannel(destNode.getHost(), destNode.getPort());
        List<String> deleteIds = new ArrayList<>();
        try {
            for (ScoreDoc scoreDoc : destNode.getList()) {
                Document doc = searcher.doc(scoreDoc.doc);
                RowsRequest rowsRequest = covertData(doc);  // 将doc转换为RowsRequest
                this.insertRowOfNode(channel, rowsRequest);
                deleteIds.add(doc.get(Constants._ID_));
            }
            this.deleteDocuments(deleteIds);
        } catch (IOException e) {
            throw new BusinessException(e);
        } finally {
            if (!deleteIds.isEmpty()) {
                this.deleteDocuments(deleteIds);
            }
            if (channel != null) {
                channel.shutdown();
                try {
                    if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                        channel.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 删除分发出去的索引数据
     *
     * @param ids
     */
    private void deleteDocuments(List<String> ids) {
        Term[] deleteTerms = new Term[ids.size()];
        for (int i = 0; i < deleteTerms.length; i++) {
            deleteTerms[i] = new Term(Constants._ID_, ids.get(i));
        }
        try {
            indexWriter.deleteDocuments(deleteTerms);
            indexWriter.commit();
            ids.clear();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 转化gRPC接口需要的数据格式
     *
     * @param document
     * @return
     */
    private RowsRequest covertData(Document document) {
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
        return RowsRequest.newBuilder().addRows(builder.build()).build();
    }

    /**
     * 向节点插入数据
     *
     * @param channel
     * @param rowsRequest
     */
    private void insertRowOfNode(ManagedChannel channel, RowsRequest rowsRequest) {
        IndexServiceGrpc.IndexServiceBlockingStub stub = IndexServiceGrpc.newBlockingStub(channel);
        JsonOut jsonOut = stub.insertRows(rowsRequest);
    }
}
