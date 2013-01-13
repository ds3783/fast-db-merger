package com.tgbus.servermerger.config;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-9-26
 * Time: 21:45:35
 * To change this template use File | Settings | File Templates.
 */
public class NoInsertTableMerge {
    private String colname;
    private String operation;

    public String getColname() {
        return colname;
    }

    public void setColname(String colname) {
        this.colname = colname;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
