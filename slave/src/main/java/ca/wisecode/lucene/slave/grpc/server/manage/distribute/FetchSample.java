package ca.wisecode.lucene.slave.grpc.server.manage.distribute;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.*;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/5/2024 1:30 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class FetchSample {
    private ScoreDoc lastScoreDoc = null;
    private int pageSize = 3000;
    private final MatchAllDocsQuery query = new MatchAllDocsQuery();  // 可替换为其他查询

    public List<ScoreDoc> pagedSearch(IndexSearcher searcher, float percent) throws IOException {
        if (percent > 0.9999) {
            return fullPagedSearch(searcher);
        } else {
            return partPagedSearch(searcher, percent);
        }

    }


    public List<ScoreDoc> partPagedSearch(IndexSearcher searcher, float percent) throws IOException {

        // 分批查询
        TopDocs topDocs = searcher.searchAfter(lastScoreDoc, query, pageSize);
        if (topDocs.scoreDocs.length < 10) {//太少就不用继续平衡了
            return new ArrayList<>();
        } else {
            int lastIndex = topDocs.scoreDocs.length - 1;
            // 将除最后一个元素外的所有元素加入到列表中
            List<ScoreDoc> scoreDocsList = new ArrayList<>(Arrays.asList(topDocs.scoreDocs).subList(0, lastIndex));
            // 将结果打乱（随机化）
            Collections.shuffle(scoreDocsList, new Random());
            // 更新最后一个 ScoreDoc，用于下一次分页查询
            lastScoreDoc = topDocs.scoreDocs[lastIndex];
//            log.info("=================="+searcher.doc(lastScoreDoc.doc).get(Constants._ID_));
            return scoreDocsList.subList(0, (int) (scoreDocsList.size() * percent));
        }
    }

    public List<ScoreDoc> fullPagedSearch(IndexSearcher searcher) throws IOException {
        TopDocs topDocs = searcher.search(query, pageSize);
        List<ScoreDoc> scoreDocsList = new ArrayList<>();
        Collections.addAll(scoreDocsList, topDocs.scoreDocs);
        // 将结果打乱（随机化）
        Collections.shuffle(scoreDocsList, new Random());

        return scoreDocsList;
    }

}
