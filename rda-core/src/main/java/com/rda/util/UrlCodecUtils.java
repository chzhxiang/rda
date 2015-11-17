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

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;


/**
 * 对URL中的字符串进行编码和解码的工具类,跟JQuery的编码解码相一致
 * 
 * @author 张凯锋
 * @since 2010-4-22
 */
public class UrlCodecUtils {
	/**
	 * Returns a string representation of the contents of the specified array. 使用自定义的分隔符
	 * 
	 * @param a
	 * @return
	 * @author 张凯锋
	 */
	public static String arrayToString(Object[] a, String delimiter) {
		if (a == null)
			return "";
		final int iMax = a.length - 1;
		if (iMax == -1)
			return "";
		final StringBuilder b = new StringBuilder();
		for (int i = 0;; i++) {
			b.append(a[i]);
			if (i == iMax)
				return b.toString();
			b.append(delimiter);
		}
	}

	/**
	 * <p>
	 * Title: base64Decode
	 * </p>
	 * <p>
	 * Description: 解码base64
	 * </p>
	 * 
	 * @param base64String
	 * @return 解码后的字符串
	 * @author 张凯锋 2010-5-31 下午02:12:14
	 */
	public static String base64Decode(String base64String) {
		if (StringUtils.isBlank(base64String))
			return base64String;
		try {
			return new String(decodeBase64(base64String), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(decodeBase64(base64String));
		}
	}

	/**
	 * Decodes a Base64 String into octets
	 * 
	 * @param base64String
	 *            String containing Base64 data
	 * @return Array containing decoded data.
	 * @since 1.4
	 */
	public static byte[] decodeBase64(String base64String) {
		return new Base64(true).decode(base64String);
	}

	/**
	 * <p>
	 * Title: encodeBase64
	 * </p>
	 * <p>
	 * Description: TODO
	 * </p>
	 * 
	 * @param base64String
	 * @return
	 * @author 李斌 2010-6-1 下午02:58:56
	 */
	public static byte[] encodeBase64(String base64String) {
		return new Base64(true).encode(base64String.getBytes());
	}

	/**
	 * <p>
	 * Title: base64Encode
	 * </p>
	 * <p>
	 * Description: TODO
	 * </p>
	 * 
	 * @param base64String
	 * @return
	 * @author 李斌 2010-6-1 下午02:59:54
	 */
	public static String base64Encode(String base64String) {
		if (StringUtils.isBlank(base64String))
			return base64String;
		try {
			return new String(encodeBase64(base64String), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(encodeBase64(base64String));
		}
	}
}
