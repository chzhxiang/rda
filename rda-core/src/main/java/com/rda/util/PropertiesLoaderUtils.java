/*******************************************************************************
 * Copyright 2011 Soofa Team(www.soofa.org).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.rda.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.Resource;


/**
 * <P>
 * 代理spring的PropertiesLoaderUtils工具类,为soofa提供统一工具视图
 * </P>
 * 
 * @see org.springframework.core.io.support.PropertiesLoaderUtils
 * @author 汪一鸣
 */
public class PropertiesLoaderUtils extends org.springframework.core.io.support.PropertiesLoaderUtils {
	/**
	 * 支持从classpath或文件系统中寻找properties文件,具体路径写法见com.rda.util.ResourceUtils.getResources(String
	 * locationPattern)
	 * 
	 * @see com.rda.util.ResourceUtils#getResources(String)
	 * @param path 用法见ResourceUtils.getResources方法
	 * @return
	 * @author 汪一鸣 2010-10-19 下午03:57:34
	 * @throws IOException
	 */
	public static Properties loadProperties(String path) throws IOException {
		Resource[] resources = ResourceUtils.getResources(path);
		if (resources.length > 1) {
			throw new RuntimeException("指定的路径[" + path + "]存在不止一个文件,系统不知选择哪一个!");
		}
		return PropertiesLoaderUtils.loadProperties(resources[0]);
	}
}
