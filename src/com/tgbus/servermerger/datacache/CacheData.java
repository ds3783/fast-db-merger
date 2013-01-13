package com.tgbus.servermerger.datacache;
 
import java.io.Serializable;


public class CacheData implements Serializable {
	private String value;
	private CacheKey key;
	private CacheMeta meta; 
	public String getValue() { 
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public CacheKey getKey() {
		return key;
	}
	public void setKey(CacheKey key) {
		this.key = key;
	}
	public CacheMeta getMeta() {
		return meta;
	}
	public void setMeta(CacheMeta meta) {
		this.meta = meta;
	}
	
	
}
