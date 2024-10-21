package ca.wisecode.lucene.slave.grpc.server.query.sort;

import ca.wisecode.lucene.grpc.models.SortRule;
import org.apache.lucene.search.Sort;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/20/2024 1:24 PM
 * @Version: 1.0
 * @description:
 */
public interface IWrapSort {
    Sort build(List<SortRule> sortRules);
}
