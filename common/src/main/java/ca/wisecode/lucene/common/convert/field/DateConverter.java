package ca.wisecode.lucene.common.convert.field;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.grpc.models.Cell;
import lombok.val;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/27/2024 11:58 AM
 * @Version: 1.0
 * @description:
 */

public class DateConverter extends Converter {
    public DateConverter(String name,String format) {
        super(name,format);
    }

    @Override
    protected Cell convertValue(Object val) {
        if (val instanceof String) {
            LocalDate date = LocalDate.parse((String) val, DateTimeFormatter.ofPattern(format));
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()).build();
        } else if (val instanceof LocalDate) {
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(((LocalDate) val).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()).build();
        } else if (val instanceof Date) {
            return Cell.newBuilder().setType(FieldMeta.Type.LONG).setName(name).setLongVal(((Date) val).getTime()).build();
        }else {
            throw new BusinessException(val + " -> Cannot be converted to date");
        }
    }
}
