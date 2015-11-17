/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.security.shiro.realms;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;

/**
 * <P>
 * sso环境中所有的用户都使用cas认证,jdbc不负责认证部分,只负责授权.<br/>
 * 此realm需和casRealm配合使用.此class只是简单覆盖supports方法.<br/>
 * 此jdbcRealm不需要设置authenticationQuery,saltStyle,credentialsMatcher.<br/>
 * </P>
 * @author lianrao
 * @since	0.0.1
 */
public class RdaSsoJdbcRealm extends JdbcRealm{

	@Override
	 public boolean supports(AuthenticationToken token) {
	        return false;
	    }
}
