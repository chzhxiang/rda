/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.security.shiro.advisor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;


/**
 * <P>
 * shiro 1.2.2对class level的annotation处理有bug, this advisor fix it.
 * </P>
 * @see https://issues.apache.org/jira/browse/SHIRO-343
 * @author lianrao
 */
@SuppressWarnings({"unchecked"})
public class RdaShiroAdvisor extends AuthorizationAttributeSourceAdvisor {

	private static final long serialVersionUID = 8599071272629444249L;

	private static final Logger log = LoggerFactory.getLogger(AuthorizationAttributeSourceAdvisor.class);

	private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES = new Class[] {
			RequiresPermissions.class, RequiresRoles.class, RequiresUser.class, RequiresGuest.class,
			RequiresAuthentication.class };

	public boolean matches(Method method, Class targetClass) {
		if (isAuthzAnnotationPresent(targetClass)) {
			return true;
		}
		return super.matches(method, targetClass);
	}

	private boolean isAuthzAnnotationPresent(Class cls) {
		for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
			Annotation a = AnnotationUtils.findAnnotation(cls, annClass);
			if (a != null) {
				return true;
			}
		}
		return false;
	}

}
