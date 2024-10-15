package ca.wisecode.lucene.common.convert.field;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.grpc.models.Cell;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/27/2024 11:58 AM
 * @Version: 1.0
 * @description:
 */

public class TimeConverter extends Converter {


    public TimeConverter(String name, String format) {
        super(name, format);
    }

    @Override
    protected Cell convertValue(Object val) {
        if (val instanceof String) {
            LocalTime time = LocalTime.parse((String) val, DateTimeFormatter.ofPattern(format));
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(time.toNanoOfDay() / 1000000).build();
        } else if (val instanceof LocalTime) {
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(((LocalTime) val).toNanoOfDay() / 1000000).build();
        }else {
            throw new BusinessException(val + " -> Cannot be converted to time");
        }
    }
}
