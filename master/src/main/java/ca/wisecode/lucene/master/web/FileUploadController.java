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
@RequestMapping("/api/file")
@Slf4j
public class FileUploadController {
    @Autowired
    private IndexBS indexBS;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private RemoveService removeService;

    @PostMapping("/upload-csv")
    public String uploadCsvFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "prjID", defaultValue = "000") String prjID) {
        log.info("{},{}", file.getName(), prjID);
        indexBS.indexByFile(prjID, file);
        return "ok";
    }

}
