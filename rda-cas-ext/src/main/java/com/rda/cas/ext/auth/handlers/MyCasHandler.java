/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.cas.ext.auth.handlers;

import org.jasig.cas.adaptors.jdbc.SearchModeSearchDatabaseAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;


/**
 * <P>扩展DB handler,在验证用户名、密码之后再验证此用户是否允许访问对应服务</P>
 * @author lianrao
 */
public class MyCasHandler extends SearchModeSearchDatabaseAuthenticationHandler {

	@Override
	protected boolean postAuthenticate(final Credentials credentials, final boolean authenticated) {
		if (authenticated) {
				
		}

		return authenticated;
	}

}
