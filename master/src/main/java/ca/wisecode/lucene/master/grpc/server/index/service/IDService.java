package ca.wisecode.lucene.master.grpc.server.index.service;

import ca.wisecode.lucene.common.model.FieldMeta.Type;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.common.util.HashUtil;
import ca.wisecode.lucene.grpc.models.Cell;
import ca.wisecode.lucene.grpc.models.Row;
import ca.wisecode.lucene.grpc.models.RowsRequest;
import ca.wisecode.lucene.master.grpc.server.index.dao.IndexDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:35 PM
 * @Version: 1.0
 * @description:
 */
@Service
@Slf4j
public class IDService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private IndexDAO indexDAO;


    public void filterExistIds(List<Map<String, Object>> rowList) {
        List<String> ids = new ArrayList<>();
        for (Map<String, Object> map : rowList) {
            ids.add((String) map.get(Constants._ID_));
        }

    }

    /**
     * 批量验证数据是否已经存在，加快查询性能
     *
     * @param ids
     * @param rowList
     */
    private void availableIds(List<String> ids, List<Map<String, Object>> rowList) {
        boolean success = indexDAO.allNotExist(ids);
        if (!success) {// 如果有任何一个ID可能存在，重新每条检索
            Iterator<String> iterator = ids.iterator();
            while (iterator.hasNext()) {
                String id = iterator.next();
                // 2. 单条查询，验证存在性
                if (indexDAO.exists(id)) {
                    removeRow(id, rowList); // 移除已存在的ID
                }
            }
        }
    }

    private void removeRow(String id, List<Map<String, Object>> rowList) {
        Iterator<Map<String, Object>> iterator = rowList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();
            if (next.get(Constants._ID_).equals(id)) {
                iterator.remove();
            }
        }


    }
}
