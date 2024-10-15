package ca.wisecode.lucene.master.grpc.server.index;

import ca.wisecode.lucene.common.model.PrjMeta;
import ca.wisecode.lucene.grpc.models.JsonIn;
import ca.wisecode.lucene.grpc.models.JsonOut;
import ca.wisecode.lucene.master.service.project.ProjectBS;
import com.fasterxml.jackson.databind.JsonNode;
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
public class ProjectImpl {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ProjectBS projectBS;

    public JsonOut readProject(final JsonIn req) {

        try {
            JsonNode jsonReq = objectMapper.readTree(req.getMsg());
            String prjID = jsonReq.get("prjID").asText();
            PrjMeta prjMeta = projectBS.readProjByID(prjID);
            String msg = objectMapper.writeValueAsString(prjMeta);
            return JsonOut.newBuilder().setMsg(msg).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String msg = String.format("{\"code\":400,\"tip\":\"%s\"}", e.getMessage());
            return JsonOut.newBuilder().setMsg(msg).build();
        }
    }
}
