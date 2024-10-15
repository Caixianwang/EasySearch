package ca.wisecode.lucene.common.convert.field;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.grpc.models.Cell;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/27/2024 11:58 AM
 * @Version: 1.0
 * @description:
 */

public class DateTimeConverter extends Converter {

    public DateTimeConverter(String name, String format) {
        super(name, format);
    }

    @Override
    protected Cell convertValue(Object val) {
        if (val instanceof String) {
            LocalDateTime dateTime = LocalDateTime.parse((String) val, DateTimeFormatter.ofPattern(format));
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(dateTime.toInstant(ZoneOffset.UTC).toEpochMilli()).build();
        } else if (val instanceof LocalDateTime) {
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(((LocalDateTime) val).toInstant(ZoneOffset.UTC).toEpochMilli()).build();
        } else if (val instanceof Date) {
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(((Date) val).getTime()).build();
        } else {
            throw new BusinessException(val + " -> Cannot be converted to datetime");
        }
    }
}
