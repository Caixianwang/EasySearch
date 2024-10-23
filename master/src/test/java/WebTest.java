import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/17/2024 1:28 AM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class WebTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setup() {
        // 设置 RestAssured 基本 URI
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void test01() throws Exception {
        File file = new File("E:/IntelliJSpace/000.csv");
        log.info("-------------------------------");
        // 使用 RestAssured 进行文件上传测试
        RestAssured.given() // 添加文件
                .multiPart("prjID", "000") //
                .multiPart("file", file) //
                .log().all()  // 记录所有请求信息
                .when()
                .post("/api/file/upload-csv") // 发送 POST 请求
                .then()
                .log().all()  // 记录所有响应信息
                .statusCode(200) // 验证响应状态
                .body(equalTo("ok"));

    }
    @Test
    void balance() throws Exception {
        RestAssured.given()
                .log().all()  // 记录所有请求信息
                .when().get("/api/distribute/balance")
                .then()
                .log().all()  // 记录所有响应信息
                .statusCode(200);
    }
    @Test
    void remove() throws Exception {
        RestAssured.given()
                .log().all()  // 记录所有请求信息
                .queryParam("host", "192.168.1.89")  // Add the 'host' query parameter
                .queryParam("port", 50053)           // Add the 'port' query parameter
                .when().get("/api/distribute/remove")
                .then()
                .log().all()  // 记录所有响应信息
                .statusCode(200);
    }
    @Test
    void queryList() throws Exception {
        RestAssured.given()
                .log().all()  // 记录所有请求信息
                .queryParam("content", "健康饮食")  // Add the 'host' query parameter// Add the 'port' query parameter
                .when().get("/api/query/queryList")
                .then()
                .log().all()  // 记录所有响应信息
                .statusCode(200);
    }

}
