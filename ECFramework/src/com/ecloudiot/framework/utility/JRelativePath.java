package com.ecloudiot.framework.utility;

public class JRelativePath {
	public JRelativePath(String dir,Boolean first)
	{
		this.dir=dir;
		this.first=first;
	}
	
	private String dir;
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public Boolean getFirst() {
		return first;
	}
	public void setFirst(Boolean first) {
		this.first = first;
	}
	private Boolean first;

}
