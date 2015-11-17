/*******************************************************************************
 * rda
 ******************************************************************************/
package com.rda.mybatis.test;

import com.rda.mybatis.interceptors.paging.model.Page;
import com.rda.mybatis.test.mapper.UserMapper;
import com.rda.mybatis.test.model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class MybatisTest {

	public static void main(String[] args) throws IOException{
		String resource = "mybatis-test-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		SqlSession openSession = sqlSessionFactory.openSession();
		Map map = new HashMap();
		map.put("name", "lianrao");
		Page page = new Page();
		page.setPageNo(1);
		page.setPageSize(10);
		map.put("page1", page);
		List<User> selectList = openSession.selectList("getUserByLike",map);
		System.out.println("page:"+page);
		UserMapper mapper = openSession.getMapper(UserMapper.class);
		User user = mapper.getUser(1);
		System.out.println("id:" + user.getId() + ";User name:" + user.getName());
	}
	
}