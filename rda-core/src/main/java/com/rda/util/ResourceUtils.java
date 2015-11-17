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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;


/**
 * 代理并扩展spring的ResourceUtils,为soofa提供统一的工具视图
 * 
 * @see org.springframework.util.ResourceUtils
 * @author 汪一鸣
 */
public abstract class ResourceUtils extends org.springframework.util.ResourceUtils {
	/**
	 * JBOSS vfsfile协议头标识
	 */
	private static final Object VFS_PROTOCOL_FILE = "vfsfile";
	/**
	 * JBOSS vfszip协议头标识
	 */
	private static final Object VFS_PROTOCOL_ZIP = "vfszip";

	/**
	 * <p>
	 * 查找指定pattern的资源
	 * </p>
	 * 
	 * @param locationPattern 可以直接加载指定的文件,例如"file:C:/context.xml",也可以从classpath下加载,例如
	 *            "classpath:/context.xml" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器.另外,还可以在文件系统中查找相对于classpath的文件,例如:
	 *            "classpathfile:src/test/datas/example.xlsx",注意使用classpathfile时,相对路径不要以'/'开头
	 * @return
	 * @throws IOException
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 * @author 汪一鸣
	 */
	public static Resource[] getResources(String locationPattern) throws IOException {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
				ClassUtils.getDefaultClassLoader());
		if (locationPattern.startsWith("classpathfile:")) {
			String path = locationPattern.substring("classpathfile:".length());
			URL url = ClassUtils.getDefaultClassLoader().getResource("");
			locationPattern = "file:" + url.getPath() + path;
		}
		return resolver.getResources(locationPattern);
	}

	/**
	 * 扩展spring的getFile(String)方法
	 * 
	 * @see org.springframework.util.ResourceUtils#getFile(String)
	 */
	public static File getFileInJBoss6(String resourceLocation) throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith(org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX
					.length());
			String description = "class path resource [" + path + "]";
			URL url = ClassUtils.getDefaultClassLoader().getResource(path);
			if (url == null) {
				throw new FileNotFoundException(description + " cannot be resolved to absolute file path "
						+ "because it does not reside in the file system");
			}
			return getFile(url, description);
		}
		try {
			// try URL
			return getFile(new URL(resourceLocation), "URL");
		} catch (MalformedURLException ex) {
			// no URL -> treat as file path
			return new File(resourceLocation);
		}
	}

	/**
	 * 扩展了spring的ResourceUtils#getFile(URL, String)，使其支持 JBOSS的vfs 协议
	 * 
	 * @see org.springframework.util.ResourceUtils#getFile(URL, String)
	 * @author 汪一鸣
	 * 
	 */
	public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
		Assert.notNull(resourceUrl, "Resource URL must not be null");
		final String protocol = resourceUrl.getProtocol();
		if (!org.springframework.util.ResourceUtils.URL_PROTOCOL_FILE.equals(protocol)
				&& !VFS_PROTOCOL_FILE.equals(protocol) && !VFS_PROTOCOL_ZIP.equals(protocol)) {
			throw new FileNotFoundException(description + " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: " + resourceUrl);
		}
		try {
			File file = null;
			if (VFS_PROTOCOL_FILE.equals(protocol) || VFS_PROTOCOL_ZIP.equals(protocol)) {
				file = new File(resourceUrl.getFile());
			} else {
				file = new File(org.springframework.util.ResourceUtils.toURI(resourceUrl).getSchemeSpecificPart());
			}
			return file;
		} catch (URISyntaxException ex) {
			// Fallback for URLs that are not valid URIs (should hardly ever happen).
			return new File(resourceUrl.getFile());
		}
	}

	public static InputStream getResourceAsStream(String fileName) throws FileNotFoundException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		return is;
	}
}
