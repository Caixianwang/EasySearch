import ca.wisecode.lucene.common.grpc.GrpcUtils;
import ca.wisecode.lucene.grpc.models.IndexServiceGrpc;
import ca.wisecode.lucene.grpc.models.JsonIn;
import ca.wisecode.lucene.grpc.models.JsonOut;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/17/2024 1:28 AM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class IndexTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test01() {
        String jsonString = "{\"_PRJ_\":22,\"rows\":[{\"name\": \"Alice\", \"age\": 31}, {\"name\": \"Bob\", \"age\": 26}]}";
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            log.info(jsonNode.toString());
            ManagedChannel channel = GrpcUtils.getManagedChannel("localhost", 50050);
            IndexServiceGrpc.IndexServiceBlockingStub stub = IndexServiceGrpc.newBlockingStub(channel);
            JsonIn jsonIn = JsonIn.newBuilder().setMsg(jsonNode.toString()).build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
