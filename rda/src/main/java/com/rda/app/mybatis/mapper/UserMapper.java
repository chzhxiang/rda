/*******************************************************************************
 * rda
 ******************************************************************************/
package com.rda.app.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.rda.app.domain.model.User;
import com.rda.mybatis.annotation.Mapper;

/**
 * <P>TODO</P>
 * @author lianrao
 */
@Mapper
public interface UserMapper {

//	 @Select("SELECT * FROM user WHERE id = #{id}")
	  User getById(@Param("id") Integer id);

	/**
	 * <p>TODO</p>
	 * @param intValue
	 * @param string
	 * @author lianrao
	 */
	void add(@Param("id")int intValue, @Param("name")String string);
}
