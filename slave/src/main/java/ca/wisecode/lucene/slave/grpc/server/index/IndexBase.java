package ca.wisecode.lucene.slave.grpc.server.index;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.common.model.PrjMeta;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/3/2024 2:35 PM
 * @Version: 1.0
 * @description:
 */

@Slf4j
public class IndexBase {
    private static final ObjectMapper objectMapper = new ObjectMapper();

}