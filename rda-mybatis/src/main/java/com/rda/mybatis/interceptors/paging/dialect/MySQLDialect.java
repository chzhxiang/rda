/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.mybatis.interceptors.paging.dialect;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;


/**
 * 暂时未实现
 * @author lianrao
 */

public class MySQLDialect implements Dialect {
	
	private final static Log log = LogFactory.getLog(MySQLDialect.class);

	public String getPaginationSql(String sql) {

		return null;
	}

	public boolean supportPagination() {
		return true;
	}

	private String trim(String sql) {
		String sqlt = sql.trim();
		if (sqlt.endsWith(";")) {
			sqlt = sqlt.substring(0, sqlt.length() - 1 - ";".length());
		}
		return sqlt;
	}
}
