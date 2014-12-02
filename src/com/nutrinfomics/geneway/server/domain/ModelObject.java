package com.nutrinfomics.geneway.server.domain;

import java.io.Serializable;

public class ModelObject implements Serializable{
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Integer getVersion(){
		return 1;
	}
	
}
