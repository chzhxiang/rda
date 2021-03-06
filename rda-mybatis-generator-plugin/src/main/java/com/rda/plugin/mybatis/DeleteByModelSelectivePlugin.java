/*******************************************************************************
 * Project Key : fundpay
 * Create on Mar 5, 2013 2:51:42 PM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.rda.plugin.mybatis;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * <P>生成根据条件物理删除对应记录的mapper和sqlxml,例如:<br>
 * <sql id="deleteByModelSelective" parameterType="map" resultType="int">
 * 		delete from table 
		  	<where>
				<include refid="dynamicModelSql" />
			</where>
 * </sql>
 * </P>
 * 此plugin需要与DynamicModelSqlPlugin配合使用
 * @author lianrao
 */
public class DeleteByModelSelectivePlugin extends MyBatisPlugin{

	
	private String sqlId = "deleteByModelSelective";
	
	/* (non-Javadoc)
	 * @see org.mybatis.generator.api.Plugin#validate(java.util.List)
	 * @author lianrao
	 */
	public boolean validate(List<String> warnings) {
		return true;
	}
	

	/**
	 *生成方法:public List<Model> countByModelSelective(@Param("cond")cond ,@Param("strict")boolean strict);
	 */
	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		genDeleteByModelSelectiveMethod(interfaze, topLevelClass, introspectedTable);
		return true;
	}

	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		genDeleteByModelSql(document, introspectedTable);
		return true;
	}

	/**
	 * 
	 * <p>生成deleteByModelSeletive Mapper方法</p>
	 * @param interfaze
	 * @param topLevelClass
	 * @param introspectedTable
	 * @author lianrao
	 */
	private void genDeleteByModelSelectiveMethod(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		String modelName = introspectedTable.getTableConfiguration().getDomainObjectName();

		Method method = new Method(this.sqlId);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType("java.lang.Integer"));
		Parameter parameter = new Parameter(new FullyQualifiedJavaType(modelName), "cond", "@Param(\"cond\")");
		method.addParameter(parameter);
		parameter = new Parameter(new FullyQualifiedJavaType("java.lang.boolean"), "strict", "@Param(\"strict\")");
		method.addParameter(parameter);
		//		context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
		genDeleteByModelSelectiveComment(method, introspectedTable);
		interfaze.addMethod(method);
	}

	/**
	 * <p>生成comment</p>
	 * @param method
	 * @param introspectedTable
	 * @author lianrao
	 */
	private void genDeleteByModelSelectiveComment(Method method, IntrospectedTable introspectedTable) {

		method.addJavaDocLine("/**"); //$NON-NLS-1$
		method.addJavaDocLine(" * 此方法删除满足条件的记录数;<br/>");
		method.addJavaDocLine(" * 此条件判断其值不为 null且不为空'';<br/>");
		method.addJavaDocLine(" * 若strict为true则精确匹配所有值,若为false则模糊匹配所有类型为String的值;<br/>");
		method.addJavaDocLine(" * This method was generated by MyBatis Generator.<br/>"); //$NON-NLS-1$

		StringBuilder sb = new StringBuilder();
		sb.append(" * This method corresponds to the database table<br/> "); //$NON-NLS-1$
		sb.append(introspectedTable.getFullyQualifiedTable());
		method.addJavaDocLine(sb.toString());

		addJavadocTag(method, false);

		method.addJavaDocLine(" */"); //$NON-NLS-1$

	}

	/**
	 * 
	 * <p>生成selectCountByModel Sql</p>
	 * @param document
	 * @param introspectedTable
	 * @author lianrao
	 */
	private void genDeleteByModelSql(Document document, IntrospectedTable introspectedTable) {
		XmlElement element = new XmlElement("select");
		Attribute attribute = new Attribute("id", this.sqlId);
		element.addAttribute(attribute);
		attribute = new Attribute("parameterType", "map");
		element.addAttribute(attribute);
		attribute = new Attribute("resultType", "int");
		element.addAttribute(attribute);
		context.getCommentGenerator().addComment(element);

		StringBuilder sb = new StringBuilder();
		int level1 = 1;
		int level2 = 2;
		int level3 = 3;
		int level4 = 4;
		int level5 = 5;

		sb.append(indent(level1) + "delete from " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "\n");
		sb.append(indent(level2) + "<where>\n");
		sb.append(indent(level3) + "<include refid=\"" + DYNAMIC_SQL_ID + "\" />\n");
		sb.append(indent(level2) + "</where>");

		TextElement text = new TextElement(sb.toString());
		element.addElement(text);
		document.getRootElement().addElement(element);
	}

}
