package ca.wisecode.lucene.slave.grpc.server.manage.distribute;

import ca.wisecode.lucene.slave.grpc.client.service.SearchManager;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 11:37 PM
 * @Version: 1.0
 * @description:
 */

public class BaseFetchRadio {
    protected SearchManager searchManager;

    public BaseFetchRadio(SearchManager searchManager) {
        this.searchManager = searchManager;
    }
}
