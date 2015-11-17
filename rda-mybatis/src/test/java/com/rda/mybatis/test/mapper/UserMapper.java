/*******************************************************************************
 * Project Key : fundpay
 * Create on Jul 15, 2013 11:40:47 AM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.rda.mybatis.test.mapper;

import org.apache.ibatis.annotations.Param;

import com.rda.mybatis.annotation.Mapper;
import com.rda.mybatis.test.model.User;

/**
 * <P>TODO</P>
 * @author lianrao
 */
@Mapper
public interface UserMapper {

//	 @Select("SELECT * FROM user WHERE id = #{id}")
	  User getUser(@Param("id") Integer id);
	  
	  User getUserWithEnum(@Param("id") Integer id);
}
