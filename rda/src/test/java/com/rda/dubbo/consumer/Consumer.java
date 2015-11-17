/*******************************************************************************
 * Project Key : fundpay
 * Create on Jul 17, 2013 10:43:55 AM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.rda.dubbo.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.dubbo.rpc.service.EchoService;
import com.rda.app.services.DemoService;


/**
 * <P>TODO</P>
 * @author lianrao
 */
public class Consumer {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring-remote-consumer.xml" });
		context.start();

		DemoService demoService = (DemoService) context.getBean("demoService"); // get service invocation proxy
		String hello = demoService.sayHello("world"); // do invoke!

		EchoService echoService = (EchoService) demoService;
		String $echo = (String)echoService.$echo("OK");
		System.out.println(hello); // cool, how are you~
		System.out.println($echo);
		System.out.println("Press any key to exit.");
        System.in.read();

	}

}
