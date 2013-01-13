package com.tgbus.servermerger.datacache; 

import java.io.Serializable;

public class Column  implements Serializable {
	private String name;
	private boolean optional;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name; 
	}
	public boolean isOptional() {
		return optional;
	}
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
} 
