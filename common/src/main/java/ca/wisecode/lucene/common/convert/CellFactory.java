package ca.wisecode.lucene.common.convert;

import ca.wisecode.lucene.common.convert.field.*;
import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.model.FieldMeta;
import ca.wisecode.lucene.common.model.FieldMeta.Type;
import ca.wisecode.lucene.common.model.PrjMeta;
import ca.wisecode.lucene.common.util.Constants;
import ca.wisecode.lucene.grpc.models.Cell;

import java.util.Set;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/7/2024 7:59 PM
 * @Version: 1.0
 * @description:
 */

public class CellFactory {


    /**
     * 对插入lucene的数据进行类型转换，所有进入的数据都要满足要求
     *
     * @param prjMeta
     * @param key
     * @param val
     * @return
     */
    public static Cell convert(PrjMeta prjMeta, String key, Object val) {
        if (key.equals(Constants._ID_) || key.equals(Constants._PRJID_)) {
            return Cell.newBuilder().setType(FieldMeta.Type.STRING).setName(key).setStringVal((String) val).build();
        }
        for (FieldMeta field : prjMeta.getFields()) {
            if (key.equals(field.getName())) {
                Converter converter;
                switch (field.getType()) {
                    case Type.STRING -> converter = new StringConverter(key, field.getFormat());
                    case Type.TEXT -> converter = new TextConverter(key, field.getFormat());
                    case Type.LONG -> converter = new LongConverter(key, field.getFormat());
                    case Type.DOUBLE -> converter = new DoubleConverter(key, field.getFormat());
                    case Type.DATE -> converter = new DateConverter(key, field.getFormat());
                    case Type.TIME -> converter = new TimeConverter(key, field.getFormat());
                    case Type.DATETIME -> converter = new DateTimeConverter(key, field.getFormat());
                    default -> throw new BusinessException("Unsupported type > key: " + key);
                }
                return converter.convert(val);
            }
        }
        throw new BusinessException("There is no corresponding configuration in the project > key:" + key);
    }

    public static Cell balanceConvert(String type, String format, String key, Object val) {
        if (key.equals(Constants._ID_) || key.equals(Constants._PRJID_)) {
            return Cell.newBuilder().setType(FieldMeta.Type.STRING).setName(key).setStringVal((String) val).build();
        }
        Set<String> types = Set.of(Type.DATE, Type.TIME, Type.DATETIME);
        if (types.contains(type)) {
            type = Type.LONG;
        }
        Converter converter;
        switch (type) {
            case Type.STRING -> converter = new StringConverter(key, format);
            case Type.TEXT -> converter = new TextConverter(key, format);
            case Type.LONG -> converter = new LongConverter(key, format);
            case Type.DOUBLE -> converter = new DoubleConverter(key, format);
            default -> throw new BusinessException("Unsupported type > key: " + key);
        }
        return converter.convert(val);

    }
}
