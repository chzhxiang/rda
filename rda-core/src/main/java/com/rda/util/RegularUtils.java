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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 通用正则验证工具类
 * 
 * @author wym
 * 
 */
public class RegularUtils {
	private static Pattern	emailPattern	= Pattern.compile(RegularExpression.email, Pattern.CASE_INSENSITIVE
													| Pattern.UNICODE_CASE);

	public static boolean isEmail(String email) {
		Matcher matcher = emailPattern.matcher(email);
		return matcher.matches();
	}
}
