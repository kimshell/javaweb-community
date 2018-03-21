package io.javaweb.community.web.model;

import java.io.Serializable;
import java.util.List;

import io.javaweb.community.mybatis.domain.PageList;

public class Datagrid <T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3556839727783194619L;

	private Integer total;
	
	private List<T> rows;
	
	public Datagrid() {}
	
	public Datagrid(PageList<T> pageList) {
		this.total = pageList.getPaginator() == null ? 0 : pageList.getPaginator().getTotalCount();
		this.rows = pageList;
	}
	
	public Datagrid(Integer total,List<T> rows) {
		this.total = total;
		this.rows = rows;
	}
	
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	
	
	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
