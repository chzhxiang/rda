/*******************************************************************************
 * Project Key : CPPII
 * Create on 2012-9-6 上午8:49:26
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.rda.plugin.mybatis;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * add import statement and annotation in Mapper interface java file
 * @author lianrao
 */
public class AddAnnotationsAndImportsPlugin extends MyBatisPlugin {

	private String imports="";
	private String annotations="";
	/* (non-Javadoc)
	 * @see org.mybatis.generator.api.Plugin#validate(java.util.List)
	 * @author lianrao
	 */
	public boolean validate(List<String> warnings) {
		
		imports= this.properties.getProperty("imports");
		annotations  = this.properties.getProperty("annotations");
		boolean valid = stringHasValue(imports)||stringHasValue(annotations);
		
		if(!valid)
		{
			warnings.add(getString("ValidationError.18", //$NON-NLS-1$
					"AddAnnotationPlugin", //$NON-NLS-1$
					"imports or annotations")); //$NON-NLS-1$
		}
		
		return valid;
	}
	
	 public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
	            IntrospectedTable introspectedTable)
	 {
		 //add import statement in Mapper interface file
		 if(stringHasValue(imports))
		 {
			 String[] imp = imports.split(",");
			 for(String s : imp)
			 {
				 FullyQualifiedJavaType type = new FullyQualifiedJavaType(s);
				 interfaze.addImportedType(type);
			 }
		 }
//		 interfaze.addFormattedAnnotations(new StringBuilder("@SoofaMapper"), 0);
		 //add annotation in Mapper interface file
		 if(stringHasValue(annotations))
		 {
			 String [] ann = annotations.split(",");
			 for(String s : ann)
			 {
				 interfaze.addAnnotation(s);
//				 topLevelClass.addAnnotation(s);
			 }
		 }
		 
		return true;
	 }

}
