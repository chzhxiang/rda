/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.mybatis.interceptors.paging.dialect;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * <P>H2分页语句(与Oracle分页语句一致)</P>
 * @author lianrao
 */
public class H2Dialect implements Dialect {

	private static final Log log = LogFactory.getLog(OracleDialect.class);
	
	/* (non-Javadoc)
	 * @see com.rda.mybatis.paging.dialect.Dialect#supportPagination()
	 * @author lianrao
	 */
	public boolean supportPagination() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.rda.mybatis.paging.dialect.Dialect#getPaginationSql(java.lang.String)
	 * @author lianrao
	 */
	public String getPaginationSql(String sql) {
		sql = sql.trim();

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);

		pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");

		pagingSelect.append(sql);

		pagingSelect.append(" ) row_ ) where rownum_ > ?  and rownum_ <= ? ");

		
		log.debug("H2Dialect PaginationSql:" + pagingSelect.toString());

		return pagingSelect.toString();
	}

}
