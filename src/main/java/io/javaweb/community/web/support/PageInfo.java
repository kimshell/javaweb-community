package io.javaweb.community.web.support;

import io.javaweb.community.mybatis.domain.Paginator;

public class PageInfo {
	
	private int totalCount;
	
	private int totalPage;
	
	private int limit;
	
	private int page;
	
	public PageInfo() {}
	
	public PageInfo(Paginator paginator) {
		this.limit = paginator.getLimit();
	    this.page = paginator.getPage();
	    this.totalCount = paginator.getTotalCount();
	    this.totalPage = paginator.getTotalPages();
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	@Override
	public String toString() {
		return "PageInfo [totalCount=" + totalCount + ", totalPage=" + totalPage + ", limit=" + limit + ", page=" + page
				+ "]";
	}
}
