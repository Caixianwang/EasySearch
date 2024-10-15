package ca.wisecode.lucene.slave.grpc.server.manage.balance.dist;

import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 1:37 AM
 * @Version: 1.0
 * @description: 处理分发和删除
 */
public interface IndexCommand {
    void execute(IndexSearcher searcher, List<DestNode> destNodes) ;
}
