package ca.wisecode.lucene.master.web;

import ca.wisecode.lucene.master.grpc.client.distribute.balance.BalanceService;
import ca.wisecode.lucene.master.grpc.client.distribute.remove.RemoveService;
import ca.wisecode.lucene.master.service.index.IndexBS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/23/2024 11:58 PM
 * @Version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api/distribute")
@Slf4j
public class DistributeController {
    @Autowired
    private IndexBS indexBS;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private RemoveService removeService;

    @GetMapping("/balance")
    public String balance() {
        balanceService.balance();
        return "balanceService";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam("host") String host, @RequestParam("port") int port) {
        removeService.remove(host,port);
        return "removeService";
    }
}
