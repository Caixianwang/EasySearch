package ca.wisecode.lucene.slave.grpc.server.query.sort;

import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.grpc.models.SortRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/20/2024 1:25 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class DefaultSort implements IWrapSort {
    @Override
    public Sort build(List<SortRule> sortRules) {
        SortField[] sortFields = new SortField[sortRules.size()];

        for (int i = 0; i < sortRules.size(); i++) {
            SortRule rule = sortRules.get(i);
            switch (rule.getType()) {
                case FieldMeta.Type.DOUBLE ->
                        sortFields[i] = new SortField(rule.getName(), SortField.Type.DOUBLE, rule.getAsc());
                case FieldMeta.Type.LONG, FieldMeta.Type.DATE, FieldMeta.Type.DATETIME, FieldMeta.Type.TIME ->
                        sortFields[i] = new SortField(rule.getName(), SortField.Type.LONG, rule.getAsc());
                default -> sortFields[i] = new SortField(rule.getName(), SortField.Type.STRING, rule.getAsc());
            }
        }
        return new Sort(sortFields);
    }
}
