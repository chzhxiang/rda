/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.mybatis.interceptors;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMapping.Builder;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;

import com.rda.mybatis.interceptors.paging.dialect.Dialect;
import com.rda.mybatis.interceptors.paging.dialect.OracleDialect;
import com.rda.mybatis.interceptors.paging.model.Page;
import com.rda.mybatis.interceptors.rw.datasource.AbstractRWRoutingDataSourceProxy;
import com.rda.mybatis.interceptors.rw.datasource.ConnectionProxy;
import com.rda.util.ReflectionUtils;


/**
 * <P>TODO</P>
 * @author lianrao
 */
@Intercepts({ @org.apache.ibatis.plugin.Signature(type = StatementHandler.class, method = "prepare", args = { java.sql.Connection.class }) })
public class RdaMybatisPlugins implements Interceptor {

	private static final Log log = LogFactory.getLog(RdaMybatisPlugins.class);
	private Dialect dialect = new OracleDialect();
	private boolean enableRW = false;
	private boolean enablePage = false;
	
	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.plugin.Interceptor#intercept(org.apache.ibatis.plugin.Invocation)
	 * @author lianrao
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		if(this.enablePage){
			pageIntercept(invocation);
		}
		
		if(this.enableRW){
			rwIntercept(invocation);
		}
		
		return invocation.proceed();
	}

	private void rwIntercept(Invocation invocation) {
		Connection conn = (Connection) invocation.getArgs()[0];

		//如果是采用了我们代理,则路由数据源
		if (conn instanceof ConnectionProxy) {
			Object target = invocation.getTarget();
			StatementHandler statementHandler = (StatementHandler) target;

			MappedStatement mappedStatement = null;
			if (statementHandler instanceof RoutingStatementHandler) {
				StatementHandler delegate = (StatementHandler) ReflectionUtils.getFieldValue(statementHandler,
						"delegate");
				mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(delegate, "mappedStatement");
			} else {
				mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(statementHandler, "mappedStatement");
			}
			String key = AbstractRWRoutingDataSourceProxy.WRITE;

			if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
				key = AbstractRWRoutingDataSourceProxy.READ;
			} else {
				key = AbstractRWRoutingDataSourceProxy.WRITE;
			}

			ConnectionProxy conToUse = (ConnectionProxy) conn;
			conToUse.getTargetConnection(key);

		}
	}

	private void pageIntercept(Invocation invocation) {
		StatementHandler statementHandler = getStatementHandler(invocation);

		Configuration configuration = (Configuration) ReflectionUtils.getFieldValue(statementHandler, "configuration");
		MetaObject metaStatementHandler = configuration.newMetaObject(statementHandler);

		String originalSql = (String) metaStatementHandler.getValue("boundSql.sql");
		BoundSql boundSql = statementHandler.getBoundSql();
		Object obj = boundSql.getParameterObject();
		Page page = getPageObject(obj);

		if (page != null) {
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

	private StatementHandler getStatementHandler(Invocation invocation) {
		Object target = invocation.getTarget();
		StatementHandler statement = (StatementHandler) target;
		if (statement instanceof RoutingStatementHandler) {
			statement = (StatementHandler) ReflectionUtils.getFieldValue(statement, "delegate");
		}
		return statement;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.plugin.Interceptor#plugin(java.lang.Object)
	 * @author lianrao
	 */
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.plugin.Interceptor#setProperties(java.util.Properties)
	 * @author lianrao
	 */
	@Override
	public void setProperties(Properties properties) {

		String enableRW = properties.getProperty("enableRW","false");
		String enablePage = properties.getProperty("enablePage","false");
		
		if("true".equalsIgnoreCase(enableRW)){
			this.enableRW = true;
		}
		
		if("true".equalsIgnoreCase(enablePage)){
			this.enablePage = true;
		}
	}

}
