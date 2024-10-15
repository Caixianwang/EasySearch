package ca.wisecode.lucene.slave.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 9:46 AM
 * @Version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class TestController {
    @GetMapping("/test01")
    public String test01() {
        log.info("==================================================");
        return "test01";
    }
}
