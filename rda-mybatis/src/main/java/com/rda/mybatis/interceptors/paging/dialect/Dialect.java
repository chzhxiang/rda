/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.mybatis.interceptors.paging.dialect;

/**
 * @author lianrao
 */
public interface Dialect {

	static final String SQL_END_DELIMITER = ";";

	public abstract boolean supportPagination();

	public abstract String getPaginationSql(String sql);
}
