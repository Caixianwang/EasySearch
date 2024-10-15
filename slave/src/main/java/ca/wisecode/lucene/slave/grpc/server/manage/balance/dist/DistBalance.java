package ca.wisecode.lucene.slave.grpc.server.manage.balance.dist;

import ca.wisecode.lucene.common.convert.CellFactory;
import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.grpc.GrpcUtils;
import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.common.model.FieldMeta.Type;
import ca.wisecode.lucene.common.model.PrjMeta;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.*;
import ca.wisecode.lucene.slave.grpc.client.service.ProjectService;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/5/2024 1:22 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class DistBalance {
    @Autowired
    private SearchManager searchManager;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private IndexWriter indexWriter;

    /**
     * 当前节点将进行索引数据分发到其它节点
     * 1、首先计算需要分发的文档数
     * 2、计算从当前节点抽取分发的文档比例
     * 3、分页查询当前索引数据
     * 4、分发下去的每个节点得到的文档数
     * 5、向各节点发送索引数据
     * 6、删除已经纷纷的索引数据
     *
     * @param balanceRequest
     */
    public void distribute(BalanceRequest balanceRequest) {
        int distNums = this.distributeNums(balanceRequest);
        float percent = this.fetchPercent(distNums);
        if (percent > 0) {
            FetchSample fetchSample = new FetchSample();
            try {
                IndexSearcher searcher = searchManager.getSearcher();
                //5000记录分页查询，从5000中按比例抽取样本
                List<ScoreDoc> scoreDocs = fetchSample.pagedSearch(searcher, 5000, percent);
                while (!scoreDocs.isEmpty()) {
                    log.info("-----------------------" + scoreDocs.size());
                    // 对所有待分配的目标节点，进行的索引数量的分配
                    List<DestNode> destNodes = this.buildDestination(distNums, balanceRequest, scoreDocs);
                    // 各节点进行索引数据分配
                    this.sendDestination(searcher, destNodes);
                    scoreDocs = fetchSample.pagedSearch(searcher, 5000, percent);
                }
            } catch (IOException e) {
                throw new BusinessException(e);
            }
        }
    }

    /**
     * 组装所有目标节点分配的文档数
     *
     * @param balanceRequest
     * @param scoreDocs
     * @return
     */
    private List<DestNode> buildDestination(int distNums, BalanceRequest balanceRequest, List<ScoreDoc> scoreDocs) {
        int start = 0;
        List<DestNode> destNodes = new ArrayList<>();
        for (TargetNode targetNode : balanceRequest.getTargetNodesList()) {
            DestNode destNode = new DestNode(targetNode.getHost(), targetNode.getPort());
            int len = (int) (targetNode.getCnt() / distNums * scoreDocs.size());
            if ((len + start) <= scoreDocs.size()) {
                destNode.setList(scoreDocs.subList(start, len + start));
                start += len;
            }
            destNodes.add(destNode);
        }
        return destNodes;
    }

    /**
     * 从当前节点需要分发出去的总数
     *
     * @param balanceRequest
     * @return
     */
    private int distributeNums(BalanceRequest balanceRequest) {

        int distNums = 0; // 分发的总数
        for (TargetNode targetNode : balanceRequest.getTargetNodesList()) {
            distNums += targetNode.getCnt();
        }

        return distNums;
    }

    /**
     * 从当前节点抽取的索引比例
     *
     * @param distNums
     * @return
     */
    private float fetchPercent(int distNums) {
        int numDocs = searchManager.getReader().numDocs();
        if (numDocs != 0 && distNums != 0) {
            return (float) distNums / numDocs;
        }
        return 0;
    }

    /**
     * 向所有目标节点发送索引数据
     *
     * @param searcher
     * @param destNodes
     */
    private void sendDestination(IndexSearcher searcher, List<DestNode> destNodes) {
        for (DestNode destNode : destNodes) {
            if (!destNode.getList().isEmpty()) {
                this.sendDestination(searcher, destNode);
            }
        }
    }

    /**
     * 向目标节点发送索引数据
     *
     * @param searcher
     * @param destNode
     */
    private void sendDestination(IndexSearcher searcher, DestNode destNode) {
        ManagedChannel managedChannel = null;
        List<String> deleteIds = null;
        try {
            managedChannel = GrpcUtils.getManagedChannel(destNode.getHost(), destNode.getPort());
            Iterator<ScoreDoc> iterator = destNode.getList().iterator();
            deleteIds = new ArrayList<>();
            while (iterator.hasNext()) {
                ScoreDoc scoreDoc = iterator.next();
                Document doc = searcher.doc(scoreDoc.doc);
                RowsRequest rowsRequest = this.covertData(doc);
                this.insertRowOfNode(managedChannel, rowsRequest);
                deleteIds.add(doc.get(Constants._ID_));
            }
            this.deleteDocByIds(deleteIds);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (deleteIds != null && !deleteIds.isEmpty()) {
                this.deleteDocByIds(deleteIds);
            }
            if (managedChannel != null) {
                managedChannel.shutdown();
                try {
                    if (!managedChannel.awaitTermination(5, TimeUnit.SECONDS)) {
                        managedChannel.shutdownNow();
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
    private void deleteDocByIds(List<String> ids) {
        Term[] deleteTerms = new Term[ids.size()];
        for (int i = 0; i < deleteTerms.length; i++) {
            deleteTerms[i] = new Term(Constants._ID_, ids.get(i));
        }
        // indexWriter.deleteDocuments(deleteTerms.toArray(new Term[0]));
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
            Cell cell = CellFactory.balanceConvert(Type.STRING, null, key, document.get(key));
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
     * @param managedChannel
     * @param rowsRequest
     */
    private void insertRowOfNode(ManagedChannel managedChannel, RowsRequest rowsRequest) {

        IndexServiceGrpc.IndexServiceBlockingStub stub = IndexServiceGrpc.newBlockingStub(managedChannel);
        JsonOut jsonOut = stub.insertRows(rowsRequest);
    }
}
