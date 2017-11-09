package com.hisilicon.videocenter.util;

public class MovieBaseInfo {

	String name,path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "MovieBaseInfo [name=" + name + ", path=" + path + "]";
	}
	
}
