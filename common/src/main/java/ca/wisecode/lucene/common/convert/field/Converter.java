package ca.wisecode.lucene.common.convert.field;

import ca.wisecode.lucene.grpc.models.Cell;
import lombok.val;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/27/2024 11:52 AM
 * @Version: 1.0
 * @description:
 */

public abstract class Converter {
    protected String format;
    protected String name;

    public Converter(String name,String format) {
        this.name = name;
        this.format = format;
    }

    public Cell convert(Object val) {
        if (val == null || val.toString().isEmpty()) {
            return null;
        }
        return convertValue(val);
    }

    protected abstract Cell convertValue(Object val);
}

