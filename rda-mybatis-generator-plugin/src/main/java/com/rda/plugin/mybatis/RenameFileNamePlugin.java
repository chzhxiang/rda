/*******************************************************************************
 * Project Key : CPPII Create on 2012-9-5 下午8:00:58 Copyright (c) 2008 -
 * 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.rda.plugin.mybatis;

import java.util.List;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedTable;


/**
 * <P>
 * TODO
 * </P>
 * 
 * @author lianrao
 */
public class RenameFileNamePlugin extends MyBatisPlugin {

	private String prefix = "";
	private String suffix ="_SqlMap.xml";

	/*
	 * (non-Javadoc)
	 * @see org.mybatis.generator.api.Plugin#validate(java.util.List)
	 * @author lianrao
	 */
	public boolean validate(List<String> warnings) {
		// TODO Auto-generated method stub
		prefix = properties.getProperty("prefix") == null ? prefix : properties.getProperty("prefix"); //$NON-NLS-1$
		suffix = properties.getProperty("suffix") == null ? suffix : properties.getProperty("suffix"); //$NON-NLS-1$


		return true;
	}

	@Override
	public void initialized(IntrospectedTable introspectedTable) {
		//rename the sqlmap file name
		FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
//		String tableName = table.getIntrospectedTableName();
		String tableName = table.getDomainObjectName();
//		String genTableName = tableName.replace(searchString,replaceString);
//		genTableName = prefix + JavaBeansUtil.getCamelCaseString(genTableName, true)+ suffix;
		introspectedTable.setMyBatis3XmlMapperFileName(prefix + tableName + suffix);

	}
	
	
	public static void main(String[] args){
		String tableName = "T_USER";
		System.out.print(tableName.replace("T_", ""));
	}

}
