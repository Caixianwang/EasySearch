package ca.wisecode.lucene.master.service.project;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.model.PrjMeta;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 2:02 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class ProjectBS {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private List<PrjMeta> projs = new ArrayList<>();

    private void readProjOfFile() {
        ClassPathResource resource = new ClassPathResource("project.json");
        try (InputStream inputStream = resource.getInputStream()) {
            projs = objectMapper.readValue(inputStream, new TypeReference<List<PrjMeta>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PrjMeta readProjByID(String prjID) {
        if (projs.isEmpty()) {
            readProjOfFile();
        }
        for (PrjMeta prj : projs) {
            if (prj.getPrjID().equals(prjID)) {
                return prj;
            }
        }
        throw new BusinessException("There is no metadata information for " + prjID + " project");
    }

    public static void main(String[] args) throws IOException {
        ProjectBS projectBS = new ProjectBS();
        PrjMeta prjMeta = projectBS.readProjByID("000");
        log.info(prjMeta.toString());
        // 创建 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 将对象转换为 JSON 字符串
            String jsonString = objectMapper.writeValueAsString(prjMeta);
            System.out.println("JSON String: " + jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
