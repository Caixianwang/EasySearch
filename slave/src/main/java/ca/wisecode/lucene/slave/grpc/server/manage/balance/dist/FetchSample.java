package ca.wisecode.lucene.slave.grpc.server.manage.balance.dist;

import ca.wisecode.lucene.common.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/5/2024 1:30 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class FetchSample {
    private ScoreDoc lastScoreDoc = null;
    private final MatchAllDocsQuery query = new MatchAllDocsQuery();  // 可替换为其他查询

    public List<ScoreDoc> pagedSearch(IndexSearcher searcher, int pageSize, float percent) throws IOException {

        // 分批查询
        TopDocs topDocs = searcher.searchAfter(lastScoreDoc, query, pageSize);
        if (topDocs.scoreDocs.length < 1) {//太少就不用继续平衡了
            return new ArrayList<>();
        } else {
            List<ScoreDoc> scoreDocsList = new ArrayList<>();
            int lastIndex = topDocs.scoreDocs.length - 1;
            // 将除最后一个元素外的所有元素加入到列表中
            for (int i = 0; i < lastIndex; i++) {
                scoreDocsList.add(topDocs.scoreDocs[i]);
            }
            // 将结果打乱（随机化）
            Collections.shuffle(scoreDocsList, new Random());
            // 更新最后一个 ScoreDoc，用于下一次分页查询
            lastScoreDoc = topDocs.scoreDocs[lastIndex];
            log.info("=================="+searcher.doc(lastScoreDoc.doc).get(Constants._ID_));
            return scoreDocsList.subList(0, (int) (scoreDocsList.size() * percent));
        }
    }

}
