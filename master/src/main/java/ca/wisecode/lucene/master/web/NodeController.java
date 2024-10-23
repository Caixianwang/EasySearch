package ca.wisecode.lucene.master.web;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.master.grpc.client.distribute.balance.BalanceService;
import ca.wisecode.lucene.master.grpc.client.distribute.remove.RemoveService;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import ca.wisecode.lucene.master.service.index.IndexBS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/23/2024 11:58 PM
 * @Version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api/node")
@Slf4j
public class NodeController {

    @Autowired
    private MasterNode masterNode;

    @GetMapping("/queryNodes")
    public List<NodeChannel> queryNodes() {
        return masterNode.getChannels();
    }


}
