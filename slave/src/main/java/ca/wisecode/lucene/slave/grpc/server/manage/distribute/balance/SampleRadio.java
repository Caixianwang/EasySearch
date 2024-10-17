package ca.wisecode.lucene.slave.grpc.server.manage.distribute.balance;

import ca.wisecode.lucene.grpc.models.DistributeRequest;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/14/2024 11:35 PM
 * @Version: 1.0
 * @description:
 */
public interface SampleRadio {

    /**
     * 当前节点抽取样本的比例
     *
     * @param balanceRequest
     * @return
     */
    float fetchPercent(DistributeRequest balanceRequest);
}
