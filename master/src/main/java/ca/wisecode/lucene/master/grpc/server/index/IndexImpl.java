package ca.wisecode.lucene.master.grpc.server.index;

import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.grpc.models.RowsRequest;
import ca.wisecode.lucene.grpc.models.TableRequest;
import ca.wisecode.lucene.master.grpc.server.index.service.DispatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:35 PM
 * @Version: 1.0
 * @description:
 */

@Service
@Slf4j
public class IndexImpl {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private DispatchService dispatchService;

    public JsonOut insertTable(final TableRequest tableRequest) {
        try {
            dispatchService.dispatchTable(tableRequest);
            int size = tableRequest.getRowsRequest().getRowsList().size();
            String msg = String.format("{\"code\":200,\"rows\":\"%d\"}", size);
            return JsonOut.newBuilder().setMsg(msg).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String msg = String.format("{\"code\":400,\"tip\":\"%s\"}", e.getMessage());
            return JsonOut.newBuilder().setMsg(msg).build();
        }
    }

    public JsonOut insertRows(final RowsRequest rowsRequest) {

        try {
            dispatchService.dispatchRows(rowsRequest);
            int size = rowsRequest.getRowsList().size();

            String msg = String.format("{\"code\":200,\"rows\":\"%d\"}", size);
            return JsonOut.newBuilder().setMsg(msg).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String msg = String.format("{\"code\":400,\"tip\":\"%s\"}", e.getMessage());
            return JsonOut.newBuilder().setMsg(msg).build();
        }
    }
}
