/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.mybatis.interceptors.paging;

import com.rda.mybatis.interceptors.paging.dialect.Dialect;
import com.rda.mybatis.interceptors.paging.dialect.OracleDialect;
import com.rda.mybatis.interceptors.paging.model.Page;
import com.rda.util.ReflectionUtils;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMapping.Builder;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * 
 * <P>此interceptor使用只需要在传入mapper中的参数中包含了Page实体就会进行分页处理,此暂时只支持Oracle语法的分页。</P>
 * @author lianrao
 */

@Intercepts({ @org.apache.ibatis.plugin.Signature(type = StatementHandler.class, method = "prepare", args = { java.sql.Connection.class }) })
public class PageInterceptor implements Interceptor {
	private static final Log log = LogFactory.getLog(PageInterceptor.class);
	private Dialect dialect = new OracleDialect();

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = getStatementHandler(invocation);

		Configuration configuration = (Configuration) ReflectionUtils.getFieldValue(statementHandler, "configuration");
		MetaObject metaStatementHandler = configuration.newMetaObject(statementHandler);

		String originalSql = (String) metaStatementHandler.getValue("boundSql.sql");
		BoundSql boundSql = statementHandler.getBoundSql();
		Object obj = boundSql.getParameterObject();
		Page page = getPageObject(obj);

		if (page != null) {


			getTotalRecord(page,statementHandler,invocation);
			//获取分页数据
			metaStatementHandler.setValue("boundSql.sql", this.dialect.getPaginationSql(originalSql));
			//设置动态生成的page属性,以绑定变量的形式组装sql语句，提高sql语句的处理性能
			Builder offsetBuilder = new ParameterMapping.Builder(configuration, "page.offset", Integer.class);
			offsetBuilder.jdbcType(JdbcType.NUMERIC);
			ParameterMapping offsetParameterMapping = offsetBuilder.build();
			Builder limitBuilder = new ParameterMapping.Builder(configuration, "page.limit", Integer.class);
			limitBuilder.jdbcType(JdbcType.NUMERIC);
			ParameterMapping limitParameterMapping = limitBuilder.build();

			boundSql.getParameterMappings().add(offsetParameterMapping);
			boundSql.getParameterMappings().add(limitParameterMapping);
			//设置附加参数匹配动态生成的属性
			boundSql.setAdditionalParameter("page", page);


			if (log.isDebugEnabled()) {
				log.debug("生成分页SQL : " + statementHandler.getBoundSql().getSql());
			}

		}

		return invocation.proceed();
	}

	private void getTotalRecord(Page page,StatementHandler statementHandler , Invocation invocation) throws SQLException {

		MappedStatement mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(statementHandler,
				"mappedStatement");

		Connection connection = (Connection) invocation.getArgs()[0];
		BoundSql boundSql = statementHandler.getBoundSql();
		Object parameterObject = boundSql.getParameterObject();

		String sql = boundSql.getSql();

		String countSql = getCountSql(sql);

		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, parameterObject);
		ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, countBoundSql);
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
			log.error("PageIntecepter 查询总条数报错: ", e);
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				log.error("PageIntecepter  查询总条数关闭statment报错,error: ", e);
				throw  e;
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
		log.debug("PageIntercepteor 生产查询总记录数 Sql :" + countSql);
		return countSql;
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
			Object page = null;
			try {
				page = map.get(Page.PAGE_PARAM);
			} catch (BindingException e) {
				return null;
			}
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

	protected StatementHandler getStatementHandler(Invocation invocation) {
		Object target = invocation.getTarget();
		StatementHandler statement = (StatementHandler) target;
		if (statement instanceof RoutingStatementHandler) {
			statement = (StatementHandler) ReflectionUtils.getFieldValue(statement, "delegate");
		}
		return statement;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
	}
}