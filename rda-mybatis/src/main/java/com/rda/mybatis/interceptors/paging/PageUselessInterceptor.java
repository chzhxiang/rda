/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.mybatis.interceptors.paging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import com.rda.mybatis.interceptors.paging.dialect.Dialect;
import com.rda.mybatis.interceptors.paging.dialect.OracleDialect;
import com.rda.mybatis.interceptors.paging.model.Page;
import com.rda.util.ReflectionUtils;


/**
 * 此分页暂时未不能使用，不过思路不错，在拦截同时将总条数和总页数取出来，留待以后有需求在实现。
 * 参考：http://haohaoxuexi.iteye.com/blog/1851081
 */
/** 
* 
* 分页拦截器，用于拦截需要进行分页查询的操作，然后对其进行分页处理。 
* 利用拦截器实现Mybatis分页的原理： 
* 要利用JDBC对数据库进行操作就必须要有一个对应的Statement对象，Mybatis在执行Sql语句前就会产生一个包含Sql语句的Statement对象，而且对应的Sql语句 
* 是在Statement之前产生的，所以我们就可以在它生成Statement之前对用来生成Statement的Sql语句下手。在Mybatis中Statement语句是通过RoutingStatementHandler对象的 
* prepare方法生成的。所以利用拦截器实现Mybatis分页的一个思路就是拦截StatementHandler接口的prepare方法，然后在拦截器方法中把Sql语句改成对应的分页查询Sql语句，之后再调用 
* StatementHandler对象的prepare方法，即调用invocation.proceed()。 
* 对于分页而言，在拦截器里面我们还需要做的一个操作就是统计满足当前条件的记录一共有多少，这是通过获取到了原始的Sql语句后，把它改为对应的统计语句再利用Mybatis封装好的参数和设 
* 置参数的功能把Sql语句中的参数进行替换，之后再执行查询记录数的Sql语句进行总记录数的统计。 
* 
* @author lianrao
*/
@Intercepts({ @Signature(method = "prepare", type = StatementHandler.class, args = { Connection.class }) })
public class PageUselessInterceptor implements Interceptor {

	private static final Log log = LogFactory.getLog(PageUselessInterceptor.class);
	private Dialect dialect;
	private String databaseType;//数据库类型，不同的数据库有不同的分页方法  

	/** 
	 * 拦截后要执行的方法 
	 */
	public Object intercept(Invocation invocation) throws Throwable {

		StatementHandler delegate = getStatementHandler(invocation);

		BoundSql boundSql = delegate.getBoundSql();

		Object obj = boundSql.getParameterObject();

		Page page = getPageObject(obj);

		if (obj != null) {

			MappedStatement mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(delegate,
					"mappedStatement");

			Connection connection = (Connection) invocation.getArgs()[0];
			String sql = boundSql.getSql();

			getTotalRecord(page, mappedStatement, connection);

			String pageSql = this.getPageSql(page, sql);

			ReflectionUtils.setFieldValue(boundSql, "sql", pageSql);
		}
		return invocation.proceed();
	}
	
	/**
	 * <p>从方法参数中获取Page实例,若有表示需要分页处理，若无则不需要并返回null</p>
	 * @param obj
	 * @return
	 * @author lianrao
	 */
	@SuppressWarnings("unchecked")
	private Page getPageObject(Object obj) {
		if (obj instanceof Page) {
			return (Page) obj;
		}

		if (obj instanceof Map) {
			Map map = (Map) obj;
			Object page = map.get(Page.PAGE_PARAM);
			if (page instanceof Page) {
				return (Page) page;
			}

			Collection<Object> values = map.values();
			for (Object value : values) {
				if (value instanceof Page) {
					return (Page) value;
				}
			}
		}
		return null;
	}


	/**
	 *对于StatementHandler其实只有两个实现类，一个是RoutingStatementHandler，另一个是抽象类BaseStatementHandler，  
		BaseStatementHandler有三个子类，分别是SimpleStatementHandler，PreparedStatementHandler和CallableStatementHandler，  
		SimpleStatementHandler是用于处理Statement的，PreparedStatementHandler是处理PreparedStatement的，而CallableStatementHandler是  
		处理CallableStatement的。Mybatis在进行Sql语句处理的时候都是建立的RoutingStatementHandler，而在RoutingStatementHandler里面拥有一个  
		StatementHandler类型的delegate属性，RoutingStatementHandler会依据Statement的不同建立对应的BaseStatementHandler，即SimpleStatementHandler、  
		PreparedStatementHandler或CallableStatementHandler，在RoutingStatementHandler里面所有StatementHandler接口方法的实现都是调用的delegate对应的方法。  
		我们在PageInterceptor类上已经用@Signature标记了该Interceptor只拦截StatementHandler接口的prepare方法，又因为Mybatis只有在建立RoutingStatementHandler的时候  
		是通过Interceptor的plugin方法进行包裹的，所以我们这里拦截到的目标对象肯定是RoutingStatementHandler对象。 
	 * @param invocation
	 * @return
	 * @author lianrao
	 */
	protected StatementHandler getStatementHandler(Invocation invocation) {
		StatementHandler statement = (StatementHandler) invocation.getTarget();
		if (statement instanceof RoutingStatementHandler) {
			statement = (StatementHandler) ReflectionUtils.getFieldValue(statement, "delegate");
		}
		return statement;
	}

	/** 
	 * 拦截器对应的封装原始对象的方法 
	 */
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/** 
	 * 设置注册拦截器时设定的属性 
	 */
	public void setProperties(Properties properties) {
		this.databaseType = properties.getProperty("databaseType");
	}

	/** 
	 * 根据page对象获取对应的分页查询Sql语句，这里只做了两种数据库类型，Mysql和Oracle 
	 * 其它的数据库都 没有进行分页 
	 * 
	 * @param page 分页对象 
	 * @param sql 原sql语句 
	 * @return 
	 */
	private String getPageSql(Page page, String sql) {
		StringBuffer sqlBuffer = new StringBuffer(sql);
		if ("oracle".equalsIgnoreCase(databaseType)) {
//			return new OracleDialect().getPaginationSql(sql, page.getOffset(), page.getLimit());
		}
		return sqlBuffer.toString();
	}

	/** 
	 * 给当前的参数对象page设置总记录数 
	 * 
	 * @param page Mapper映射语句对应的参数对象 
	 * @param mappedStatement Mapper映射语句 
	 * @param connection 当前的数据库连接 
	 */
	private void getTotalRecord(Page page, MappedStatement mappedStatement, Connection connection) {

		BoundSql boundSql = mappedStatement.getBoundSql(page);

		String sql = boundSql.getSql();

		String countSql = getCountSql(sql);

		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, page);
		ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, page, countBoundSql);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(countSql);
			parameterHandler.setParameters(pstmt);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int totalRecord = rs.getInt(1);
				page.setTotalRecord(totalRecord);
			}
		} catch (SQLException e) {
			log.error("PageIntecepter getTotalRecord query error: ", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				log.error("PageIntecepter getTotalRecord close statement error: ", e);
			}
		}
	}

	/** 
	 * 根据原Sql语句获取对应的查询总记录数的Sql语句 
	 * @param sql 
	 * @return 
	 */
	private String getCountSql(String sql) {
		String countSql = "select count(*) from ( " + sql + ")";
		log.debug("PageIntercepteor Count Sql :" + countSql);
		return countSql;
	}

}
