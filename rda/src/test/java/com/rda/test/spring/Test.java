/*******************************************************************************
 * Project Key : fundpay
 * Create on Jul 15, 2013 8:39:52 AM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.rda.test.spring;

import java.io.IOException;
import java.util.UUID;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * <P>TODO</P>
 * @author lianrao
 */
public class Test {

	private static  ClassPathXmlApplicationContext context ; 
	
	public static void main(String[] args) throws IOException {
		factory();
	}
	
	public static void setAppContext(){
		context = new ClassPathXmlApplicationContext(
				new String[] { "spring-context.xml" });
		context.start();
	}
	
	public static void uuid(){
		UUID randomUUID = UUID.randomUUID();
		System.out.println(randomUUID);
	}
	
	public static void factory(){
		ProxyFactory factory = new ProxyFactory(new SimplePojo());
		factory.addAdvice(new RetryAdvice());
		Pojo pojo = (Pojo) factory.getProxy();
		// this is a method call on the proxy!
		pojo.foo();
	}
}
