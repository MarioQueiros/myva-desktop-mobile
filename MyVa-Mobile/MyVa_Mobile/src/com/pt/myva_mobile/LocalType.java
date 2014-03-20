package com.pt.myva_mobile;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LocalType implements Serializable{

	private int id;
	private String type = null;
	
	public LocalType(){
	}
	
	public LocalType(String type){
		this.setId(0);
		this.setType(type);
	}

	String getType() {
		return type;
	}

	void setType(String type) {
		this.type = type;
	}

	int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}
}
