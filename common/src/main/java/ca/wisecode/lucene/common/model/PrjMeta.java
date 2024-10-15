package ca.wisecode.lucene.common.model;


import java.util.ArrayList;
import java.util.List;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/6/2024 11:25 AM
 * @Version: 1.0
 * @description:
 */

public class PrjMeta {

    private String prjID;
    private List<FieldMeta> fields = new ArrayList<>();

    public String getPrjID() {
        return prjID;
    }

    public void setPrjID(String prjID) {
        this.prjID = prjID;
    }

    public List<FieldMeta> getFields() {
        return fields;
    }

    public void setFields(List<FieldMeta> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "PrjMeta{" +
                "prjID='" + prjID + '\'' +
                ", fields=" + fields +
                '}';
    }
}
