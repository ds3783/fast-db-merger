package com.tgbus.servermerger.config;

/**
 * Created by IntelliJ IDEA.
 * User: liuhy
 * Date: 2010-9-25
 * Time: 14:39:22
 * To change this template use File | Settings | File Templates.
 */
public class ReplaceCondition {
    private String columnName;
    private boolean canMiss;
    private String value;
    private String searchMetaId;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isCanMiss() {
        return canMiss;
    }

    public void setCanMiss(boolean canMiss) {
        this.canMiss = canMiss;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSearchMetaId() {
        return searchMetaId;
    }

    public void setSearchMetaId(String searchMetaId) {
        this.searchMetaId = searchMetaId;
    }
}
