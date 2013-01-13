package com.tgbus.servermerger.config;

public class DBConfig {
	private String driverName;
	private String srcURL;
	private String srcPass;
	private String srcUser;
	//private String srcDBName;
	private String destURL;
	private String destPass;
	private String destUser;
	//private String destDBName;
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getSrcURL() {
		return srcURL;
	}
	public void setSrcURL(String srcURL) {
		this.srcURL = srcURL;
	}
	public String getSrcPass() {
		return srcPass;
	}
	public void setSrcPass(String srcPass) {
		this.srcPass = srcPass;
	}
	public String getSrcUser() {
		return srcUser;
	}
	public void setSrcUser(String srcUser) {
		this.srcUser = srcUser;
	}
	public String getDestURL() {
		return destURL; 
	}
	public void setDestURL(String destURL) {
		this.destURL = destURL;
	}
	public String getDestPass() {
		return destPass;
	}
	public void setDestPass(String destPass) {
		this.destPass = destPass;
	}
	public String getDestUser() {
		return destUser;
	}
	public void setDestUser(String destUser) {
		this.destUser = destUser;
	}
	
	
	
	
}
