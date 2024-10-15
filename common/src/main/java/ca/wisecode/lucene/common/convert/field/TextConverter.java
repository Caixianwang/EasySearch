package ca.wisecode.lucene.common.convert.field;

import ca.wisecode.lucene.common.model.FieldMeta.Type;
import ca.wisecode.lucene.grpc.models.Cell;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/27/2024 11:53 AM
 * @Version: 1.0
 * @description:
 */

public class TextConverter extends Converter {


    public TextConverter(String name, String format) {
        super(name, format);
    }

    @Override
    protected Cell convertValue(Object val) {
        return Cell.newBuilder().setType(Type.TEXT).setName(name).setStringVal((String) val).build();
    }
}
