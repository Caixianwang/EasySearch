package ca.wisecode.lucene.common.model;


/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/6/2024 11:25 AM
 * @Version: 1.0
 * @description:
 */

public class FieldMeta {

    private String name;
    private String type;
    private String format;

    public static class Type {
        public static final String STRING = "String";
        public static final String TEXT = "Text";
        public static final String DOUBLE = "Double";
        public static final String LONG = "Long";
        public static final String DATE = "Date";
        public static final String TIME = "Time";
        public static final String DATETIME = "Datetime";
    }
    public FieldMeta() {

    }
    public FieldMeta(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "FieldMeta{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}

