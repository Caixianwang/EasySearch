package ca.wisecode.lucene.slave;

import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.slave.grpc.client.service.HealthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 9:18 AM
 * @Version: 1.0
 * @description:
 */
@SpringBootTest
@Slf4j
public class HealthTest {
    @Autowired
    private HealthService healthService;

    @Autowired
    private NodeChannel nodeChannel;

    @Test
    public void test01()  throws Exception {
        healthService.healthCheck(nodeChannel);
        log.info("======================================");
    }
}
