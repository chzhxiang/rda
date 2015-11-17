/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.mybatis.interceptors.paging.dialect;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * <P>生成Oracle官方分页语句</P>
 * @author lianrao
 */
public class OracleDialect implements Dialect {

	private static final Log log = LogFactory.getLog(OracleDialect.class);
	
	/* (non-Javadoc)
	 * @see com.rda.mybatis.paging.dialect.Dialect#supportPagination()
	 * @author lianrao
	 */
	public boolean supportPagination() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.rda.mybatis.paging.dialect.Dialect#getPaginationSql(java.lang.String, int, int)
	 * @author lianrao
	 */
	public String getPaginationSql(String sql){ 
		
		sql = sql.trim();

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);

		pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");

		pagingSelect.append(sql);

		pagingSelect.append(" ) row_ ) where rownum_ > ?  and rownum_ <= ? ");

		
		log.debug("OracleDialect PaginationSql:" + pagingSelect.toString());

		return pagingSelect.toString();
	}

}
