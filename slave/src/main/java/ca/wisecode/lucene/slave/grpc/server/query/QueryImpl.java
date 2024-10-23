package ca.wisecode.lucene.slave.grpc.server.query;


import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.FilterRule;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.PagerRequest;
import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;
import ca.wisecode.lucene.slave.grpc.server.query.mode.ChainQueryFactory;
import ca.wisecode.lucene.slave.grpc.server.query.mode.IWrapQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/5/2024 10:15 AM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class QueryImpl {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private SearchManager searchManager;
    @Autowired
    private Analyzer analyzer;

    public JsonOut query(final PagerRequest pagerRequest) {
        try {
            List<FilterRule> filterRulesList = pagerRequest.getFilterRulesList();
            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            IWrapQuery iWrapQuery = ChainQueryFactory.getInstance().buildQuery(analyzer);
            for (FilterRule filterRule : filterRulesList) {
                iWrapQuery.build(queryBuilder, filterRule);
            }
            BooleanQuery query = queryBuilder.build();
            int pageSize = pagerRequest.getPageSize();
            log.info(query.toString());
            IndexSearcher searcher = searchManager.getSearcher();
            TopDocs topDocs = searcher.search(query, pageSize);
            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<span style='color:#f73131'>", "</span>");
            Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

            List<Map<String, String>> list = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                Map<String, String> map = new HashMap<>();
                for (IndexableField field : doc.getFields()) {
                    String fieldName = field.name();
                    String fieldValue = doc.get(fieldName);
                    String highlightedText = highlighter.getBestFragment(analyzer, "content", fieldValue);
                    if (highlightedText != null) {
                        fieldValue = highlightedText;
                    }
                    map.put(fieldName, fieldValue);
                }
                list.add(map);
            }
            String jsonString = objectMapper.writeValueAsString(list);
            return JsonOut.newBuilder()
                    .setMsg(jsonString)
                    .build();
        } catch (IOException | InvalidTokenOffsetsException e) {
            log.error(e.getMessage(), e);
            return JsonOut.newBuilder()
                    .setMsg(String.format("{\"code\":400,\"tip\":\"%s\"}", Constants.FAILURE))
                    .build();
        }

    }


}
