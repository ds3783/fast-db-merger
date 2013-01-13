package com.tgbus.servermerger.config;


import java.util.List;
import java.util.Set;

public class PrimaryKey {
    private List<String> columns;
    private boolean unset;

    public boolean isUnset() {
        return unset;
    }

    public void setUnset(boolean unset) {
        this.unset = unset;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
