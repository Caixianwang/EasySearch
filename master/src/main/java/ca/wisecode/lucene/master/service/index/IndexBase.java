package ca.wisecode.lucene.master.service.index;

import ca.wisecode.lucene.common.convert.CellFactory;
import ca.wisecode.lucene.common.model.PrjMeta;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.common.util.HashUtil;
import ca.wisecode.lucene.grpc.models.Cell;
import ca.wisecode.lucene.grpc.models.Row;
import ca.wisecode.lucene.grpc.models.RowsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 2:02 PM
 * @Version: 1.0
 * @description:
 */

@Slf4j
public class IndexBase {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected Map<String, Object> lineToMap(String prjID, String[] header, String[] line) {
        Map<String, Object> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append(prjID);
        map.put(Constants._PRJID_, prjID);
        for (int i = 0; i < header.length; i++) {
            if (!line[i].isEmpty()) {
                sb.append(line[i]);
                map.put(header[i], line[i]);
            }
        }
        String id = HashUtil.getInstance().calcHash(sb.toString());
        map.put(Constants._ID_, id);
        return map;
    }

    protected RowsRequest list2Rows(PrjMeta prjMeta, List<Map<String, Object>> rowList) {
        RowsRequest.Builder builder = RowsRequest.newBuilder();
        Iterator<Map<String, Object>> iterator = rowList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();
            Row row = map2Row(prjMeta, next);
            builder.addRows(row);
        }
        return builder.build();

    }

    protected Row map2Row(PrjMeta prjMeta, Map<String, Object> mapRow) {
        Row.Builder builder = Row.newBuilder();
        Iterator<Map.Entry<String, Object>> iterator = mapRow.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            Cell cell = CellFactory.convert(prjMeta, next.getKey(), next.getValue());
            if (cell != null) {
                builder.addCells(cell);
            }
        }
        return builder.build();
    }

}
