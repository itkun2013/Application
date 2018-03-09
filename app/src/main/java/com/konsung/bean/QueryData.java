package com.konsung.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryData implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2757267350173355405L;

	//分页
	private PageStore pageStore = new PageStore();

	//查询出的数据
	private List<Object> queryDataList;

	//查询条件
	private List<QueryCondition> queryConditionList = new ArrayList<QueryCondition>();

	public List<Object> getQueryDataList() {
		return this.queryDataList;
	}

	public void setQueryDataList(List<Object> queryDataList) {
		this.queryDataList = queryDataList;
	}

	public PageStore getPageStore() {
		return this.pageStore;
	}

	public void setPageStore(PageStore pageStore) {
		this.pageStore = pageStore;
	}

	public List<QueryCondition> getQueryConditionList() {
		return this.queryConditionList;
	}

	public void setQueryConditionList(List<QueryCondition> queryConditionList) {
		this.queryConditionList = queryConditionList;
	}


}
