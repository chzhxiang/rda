package com.rda.mybatis.interceptors.paging.model;

import com.rda.util.Assert;

/**
* 对分页的基本数据进行一个简单的封装 
* @author lianrao
*/
public class Page {

	/**
	 * 分页统一注解名称
	 */
	public static final String PAGE_PARAM = "page";
	
	/**
	 * 开始记录位置
	 */
	private int offset ;

	/**
	 * 第几页
	 */
	private int pageNo = 1;

	/**
	 * 分页记录条数
	 */
	private int pageSize = 10;

	/**
	 * 获取的记录条数
	 */
	private int limit ;

	/**
	 * 总记录数
	 */
	private int totalRecord;

	/**
	 * 总页数
	 */
	private int totalPage;

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		Assert.isTrue(pageNo > 0 , "pageNo必须从1开始");
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		Assert.isTrue(pageSize > 0 , "pageSize必须大于0");
		this.pageSize = pageSize;
	}


	
	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
		this.totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1;
	}

	public int getTotalPage() {
		return totalPage;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return (pageNo-1)*pageSize;
	}


	/**
	 * @return the limit
	 */
	public int getLimit() {
		return pageNo*pageSize;
	}

	@Override
	public String toString() {
		return "Page{" +
				"offset=" + getOffset() +
				", pageNo=" + pageNo +
				", pageSize=" + pageSize +
				", limit=" + getLimit() +
				", totalRecord=" + totalRecord +
				", totalPage=" + totalPage +
				'}';
	}

}
