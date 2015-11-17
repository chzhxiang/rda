/*******************************************************************************
 * Copyright 2011 Soofa Team(www.soofa.com).
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
package com.rda.core.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;


/**
 * <P>字符串到byte[]数组转换器,规则如下:</P> <p>字符串必须以'['开头和以']'结尾,例如[51,52,51,52,112,97,115,119]转换为数组后,实际为byte[]
 * b = new byte[]{51,52,51,52,112,97,115,119}</p>
 * 
 * @author 汪一鸣
 */
public class StringToByteArrayConverter implements Converter<String, byte[]> {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 * @author 汪一鸣
	 */
	public byte[] convert(String source) {
		if (!StringUtils.hasText(source)) {
			return null;
		}
		String s = source.trim();
		if (s.startsWith("[") && s.endsWith("]")) {
			String[] split = (s.substring(1, s.length() - 1)).split(",");
			byte[] b = new byte[split.length];
			for (int i = 0; i < split.length; i++) {
				b[i] = Byte.valueOf(split[i]);
			}
			return b;
		} else {
			throw new IllegalArgumentException("excel cell [" + source + "] must starts with '[' and ends with ']'");
		}
	}

}
