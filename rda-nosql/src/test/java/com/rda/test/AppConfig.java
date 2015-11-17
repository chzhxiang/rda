/*******************************************************************************
 * Project Key : et-win
 * author: lianrao
 * 2013-12-15 下午9:13:42
 ******************************************************************************/
package com.rda.test;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <P>TODO</P>
 * @author lianrao
 */
@Configuration
public class AppConfig {
	
	@Bean
	public MongoFactoryBean mongo() {
		MongoFactoryBean mongo= new MongoFactoryBean();
		mongo.setHost("localhost");
		return mongo;
	}
	
	public MongoTemplate mongoTemplate(){
		return new MongoTemplate(mongo());
	}

}
