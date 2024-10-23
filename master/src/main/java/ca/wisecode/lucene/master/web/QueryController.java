package ca.wisecode.lucene.master.web;

import ca.wisecode.lucene.master.service.query.QueryBS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/23/2024 11:58 PM
 * @Version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api/query")
@Slf4j
public class QueryController {

    @Autowired
    private QueryBS queryBS;

    @GetMapping("/queryList")
    public List<Map<String, String>> queryList(@RequestParam("content") String content) {
        log.info(content);
        List<Map<String, String>> list = queryBS.queryList(content);
        for (Map<String, String> map : list) {
            log.info(map.toString());
        }
        return list;
    }


}
