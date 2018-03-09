package com.konsung.bean;

import java.io.Serializable;

public class QueryCondition implements Serializable {
	private static final long serialVersionUID = 643565414233321378L;

	private String key;

	private Object value;

	public QueryCondition(){}

	public QueryCondition(String key, String value){
		this.key = key;
		this.value = value;
	}

	public QueryCondition(String key, int value){
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getValue() {
		return this.value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

}
