import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/15/2024 11:07 AM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class WebTest01 {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setup() {
        // 设置 RestAssured 基本 URI
        RestAssured.baseURI = "http://localhost:8081";
    }

    @Test
    void test01() throws Exception {
        RestAssured.given()
                .log().all()  // 记录所有请求信息
                .when().get("/api/test01")
                .then()
                .log().all()  // 记录所有响应信息
                .statusCode(200);
    }
    @Test
    void test02() throws Exception {
        RestAssured.given()
                .when().get("/api/test01")
                .then()
                .statusCode(200)
                .body(equalTo("test01"));
    }
    @Test
    void test03() {
        RestAssured.given()
                .log().headers()  // 记录请求头
                .when().get("/api/test01")
                .then()
                .log().headers()  // 记录响应头
                .statusCode(200);
    }
    @Test
    void test04() {
        RestAssured.given()
                .log().body()  // 记录请求体
                .when().get("/api/test01")
                .then()
                .log().body()  // 记录响应体
                .statusCode(200);
    }
    @Test
    void test05() {
        String response = RestAssured.given()
                .when().get("/api/test01")
                .then()
                .statusCode(200)
                .extract().asString();

        // 打印格式化的 JSON 响应体
        System.out.println(response);
    }

}
