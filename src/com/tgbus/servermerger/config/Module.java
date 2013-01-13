package com.tgbus.servermerger.config;

import java.util.List;
import java.util.Set;

public class Module {
	private String id;
	private List<String> tableNames;
	public String getId() {
		return id;
	}
	public void setId(String id) {  
		this.id = id;
	}

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }
}
