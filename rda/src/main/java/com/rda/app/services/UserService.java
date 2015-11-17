/*******************************************************************************
 * rda
 ******************************************************************************/
package com.rda.app.services;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.rda.app.domain.model.User;
import com.rda.app.mybatis.mapper.UserMapper;

/**
 * <P>TODO</P>
 * @author lianrao
 */
@Service("myUserService")
@RequiresAuthentication
public class UserService {

	private Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserMapper mapper;
	
	@Transactional(isolation=Isolation.READ_COMMITTED,readOnly=true , timeout=3)
	@RequiresRoles("abc")
	public User getUser(int id){
		logger.debug("getUser");
		return mapper.getById(id);
	}

	/**
	 * <p>add user.</p>
	 * @param intValue
	 * @param string
	 * @author lianrao
	 * @see #getUser(int)
	 */
	@Transactional(isolation=Isolation.READ_COMMITTED,readOnly=false , timeout=3)
	public void addUser(int intValue, String string) {
		mapper.add(intValue,string);
	}
}
