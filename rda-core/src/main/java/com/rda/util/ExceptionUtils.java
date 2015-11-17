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

import java.io.PrintWriter;

import com.rda.core.io.UnsafeStringWriter;


/**
 * <P> 代理apache commons-lang的ExceptionUtils,为soofa提供统一的工具视图 </P>
 * 
 * @see org.apache.commons.lang.exception.ExceptionUtils
 * @author 汪一鸣
 */
public class ExceptionUtils extends org.apache.commons.lang.exception.ExceptionUtils {
	public static String toString(Throwable e) {
		UnsafeStringWriter w = new UnsafeStringWriter();
		PrintWriter p = new PrintWriter(w);
		p.print(e.getClass().getName());
		if (e.getMessage() != null) {
			p.print(": " + e.getMessage());
		}
		p.println();
		try {
			e.printStackTrace(p);
			return w.toString();
		} finally {
			p.close();
		}
	}

	public static String toString(String msg, Throwable e) {
		UnsafeStringWriter w = new UnsafeStringWriter();
		w.write(msg + "\n");
		PrintWriter p = new PrintWriter(w);
		try {
			e.printStackTrace(p);
			return w.toString();
		} finally {
			p.close();
		}
	}
}
