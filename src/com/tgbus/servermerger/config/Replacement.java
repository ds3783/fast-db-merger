package com.tgbus.servermerger.config;

import java.util.List;
import java.util.Map;

public class Replacement {
    private String type;
    private String columnName;
    private boolean canMiss;
    private boolean missWriteBack = false;
    private String searchMetaId;
    private String rewriteValue;
    private boolean pk;
    private String determinter;
    private Map<String, String> columnAlias;
    private List<ReplaceCondition> replaceConditions;

    public String getRewriteValue() {
        return rewriteValue;
    }

    public void setRewriteValue(String rewriteValue) {
        this.rewriteValue = rewriteValue;
    }

    public boolean isCanMiss() {
        return canMiss;
    }

    public void setCanMiss(boolean canMiss) {
        this.canMiss = canMiss;
    }

    public boolean isMissWriteBack() {
        return missWriteBack;
    }

    public void setMissWriteBack(boolean missWriteBack) {
        this.missWriteBack = missWriteBack;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getSearchMetaId() {
        return searchMetaId;
    }

    public void setSearchMetaId(String searchMetaId) {
        this.searchMetaId = searchMetaId;
    }

    public Map<String, String> getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(Map<String, String> columnAlias) {
        this.columnAlias = columnAlias;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    public String getDeterminter() {
        return determinter;
    }

    public void setDeterminter(String determinter) {
        this.determinter = determinter;
    }

    public List<ReplaceCondition> getReplaceConditions() {
        return replaceConditions;
    }

    public void setReplaceConditions(List<ReplaceCondition> replaceConditions) {
        this.replaceConditions = replaceConditions;
    }
}
