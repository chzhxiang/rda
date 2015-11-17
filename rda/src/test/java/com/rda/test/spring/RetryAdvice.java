/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.test.spring;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class RetryAdvice implements MethodInterceptor{


	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 * @author lianrao
	 */
	@Override
	public Object invoke(org.aopalliance.intercept.MethodInvocation invocation) throws Throwable {
		System.out.println("retry advice be invoked.");
		return invocation.proceed();
	}

	
}
