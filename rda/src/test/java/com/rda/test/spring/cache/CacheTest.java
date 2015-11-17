/*******************************************************************************
 * Project Key : fundpay
 * Create on Jul 18, 2013 10:04:12 AM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.rda.test.spring.cache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rda.app.domain.model.User;
import com.rda.app.services.UserService;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class CacheTest {

	public static  void main(String[] args){
		ApplicationContext context =
				new ClassPathXmlApplicationContext(new String[] {"spring-context.xml"});
		UserService service = (UserService)context.getBean(UserService.class);
		User user = service.getUser(1);
		System.err.println(user.getName());
	}
}
