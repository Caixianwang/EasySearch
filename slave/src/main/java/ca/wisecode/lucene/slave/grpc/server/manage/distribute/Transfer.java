package ca.wisecode.lucene.slave.grpc.server.manage.distribute;

import ca.wisecode.lucene.common.grpc.GrpcUtils;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.IndexServiceGrpc;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.RowsRequest;
import ca.wisecode.lucene.slave.grpc.client.service.ProjectService;
import ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance.DestNode;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 8:53 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class Transfer {
    private int MAX_DOC = 1500;
    private CovertData covertData;
    private IndexWriter indexWriter;

    public Transfer(IndexWriter indexWriter, ProjectService projectService) {
        this.indexWriter = indexWriter;
        this.covertData = new CovertData(projectService);

    }

    /**
     * 向所有目标节点传送索引数据
     *
     * @param searcher
     * @param destNodes
     */
    public void sendDestination(IndexSearcher searcher, List<DestNode> destNodes) {
        for (DestNode destNode : destNodes) {
            if (!destNode.getList().isEmpty()) {
                this.sendDestination(searcher, destNode);
            }
        }
    }

    /**
     * 向目标节点传送索引数据
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
            List<Document> docs = new ArrayList<>();
            while (iterator.hasNext()) {
                ScoreDoc scoreDoc = iterator.next();
                Document doc = searcher.doc(scoreDoc.doc);
                docs.add(doc);
                if (docs.size() > MAX_DOC) {
                    this.documentHandle(managedChannel, docs);
                }
            }
            if (!docs.isEmpty()) {
                this.documentHandle(managedChannel, docs);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
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

    private void documentHandle(ManagedChannel managedChannel, List<Document> docs) {
        Term[] deleteTerms = new Term[docs.size()];
        for (int i = 0; i < docs.size(); i++) {
            deleteTerms[i] = new Term(Constants._ID_, docs.get(i).get(Constants._ID_));
        }
        RowsRequest rowsRequest = this.covertData.covert(docs);
        docs.clear();
        this.insertOfNode(managedChannel, rowsRequest);
        this.deleteDocuments(deleteTerms);
    }

    /**
     * 删除分发出去的索引数据
     *
     * @param deleteTerms
     */
    private void deleteDocuments(Term[] deleteTerms) {
        try {
            indexWriter.deleteDocuments(deleteTerms);
            indexWriter.commit();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 向节点插入数据
     *
     * @param managedChannel
     * @param rowsRequest
     */
    private JsonOut insertOfNode(ManagedChannel managedChannel, RowsRequest rowsRequest) {

        IndexServiceGrpc.IndexServiceBlockingStub stub = IndexServiceGrpc.newBlockingStub(managedChannel);
        return stub.insertRows(rowsRequest);
    }
}
