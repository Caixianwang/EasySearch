package ca.wisecode.lucene.common.convert.field;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.grpc.models.Cell;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/27/2024 11:55 AM
 * @Version: 1.0
 * @description:
 */

public class LongConverter extends Converter {
    public LongConverter(String name,String format) {
        super(name,format);
    }

    @Override
    protected Cell convertValue( Object val) {
        if (val instanceof Number) {
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(((Number) val).longValue()).build();
        } else if (val instanceof String) {
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(Long.parseLong((String) val)).build();
        } else {
            throw new BusinessException(val + " -> Cannot be converted to long");
        }
    }
}
