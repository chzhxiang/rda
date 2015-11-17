/*******************************************************************************
 * Project Key : fundpay
 * Create on Mar 5, 2013 2:55:57 PM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.rda.plugin.mybatis;

import java.util.Date;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.config.MergeConstants;


/**
 * <P>提供通用方法所有自定义plugin继承此类</P>
 * @author lianrao
 */
abstract class MyBatisPlugin extends PluginAdapter {


	/**
	 * 动态语句 sql id
	 */
	protected String DYNAMIC_SQL_ID = "dynamicModelSql";
	
	/**
	 * column id
	 */
	protected String BASE_COLUMN_LIST = "Base_Column_List";
	
	/**
	 * 
	 * <p>格式缩进指定tab</p>
	 * @param level
	 * @return
	 * @author lianrao
	 */
	protected String indent(int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append("\t");
		}
		return sb.toString();
	}

	/**
	 * This method adds the custom javadoc tag for. You may do nothing if you do
	 * not wish to include the Javadoc tag - however, if you do not include the
	 * Javadoc tag then the Java merge capability of the eclipse plugin will
	 * break.
	 * 
	 * @param javaElement
	 *            the java element
	 */
	protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
		javaElement.addJavaDocLine(" *"); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		sb.append(" * "); //$NON-NLS-1$
		sb.append(MergeConstants.NEW_ELEMENT_TAG);
		if (markAsDoNotDelete) {
			sb.append(" do_not_delete_during_merge"); //$NON-NLS-1$
		}
		String s = getDateString();
		if (s != null) {
			sb.append(' ');
			sb.append(s);
		}
		javaElement.addJavaDocLine(sb.toString());
	}
	
	/**
	 * This method returns a formated date string to include in the Javadoc tag
	 * and XML comments. You may return null if you do not want the date in
	 * these documentation elements.
	 * 
	 * @return a string representing the current timestamp, or null
	 */
	protected String getDateString() {
		return new Date().toString();
	}

}
