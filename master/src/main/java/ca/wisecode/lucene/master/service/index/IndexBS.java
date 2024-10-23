package ca.wisecode.lucene.master.service.index;

import ca.wisecode.lucene.common.model.PrjMeta;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.RowsRequest;
import ca.wisecode.lucene.master.grpc.server.index.service.DispatchService;
import ca.wisecode.lucene.master.grpc.server.index.service.IDService;
import ca.wisecode.lucene.master.service.project.ProjectBS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 2:02 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class IndexBS extends IndexBase {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private DispatchService dispatchService;
    @Autowired
    private ProjectBS projectBS;
    @Autowired
    private IDService idService;

    public void indexByFile(String prjID, MultipartFile file) {
        PrjMeta prjMeta = projectBS.readProjByID(prjID);
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext(); // 读取 CSV 文件的第一行作为键名
            String[] line;
            List<Map<String, Object>> rowList = new ArrayList<>();
            while ((line = reader.readNext()) != null) {
                Map<String, Object> mapRow = this.lineToMap(prjID, header, line);
                this.addToList(mapRow, rowList);
                if (rowList.size() > Constants.MAX_ROWS) {
                    this.handleData(prjMeta, rowList);
                }
            }
            if (!rowList.isEmpty()) {
                this.handleData(prjMeta, rowList);
            }
        } catch (IOException | CsvException e) {

        }
    }

    private void addToList(Map<String, Object> mapRow, List<Map<String, Object>> rowList) {
        if (mapRow != null) {
            boolean exist = false;
            for (Map<String, Object> map : rowList) {
                String inVal = (String) map.get(Constants._ID_);
                String currVal = (String) mapRow.get(Constants._ID_);
                if (inVal.equals(currVal)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                rowList.add(mapRow);
            }
        }
    }

    private void handleData(PrjMeta prjMeta, List<Map<String, Object>> rowList) {
        idService.filterExistIds(rowList);
        RowsRequest rowsRequest = this.list2Rows(prjMeta, rowList);
        dispatchService.dispatchTable(rowsRequest);
        rowList.clear();
    }

}
