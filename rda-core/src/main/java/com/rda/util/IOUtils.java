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
package com.rda.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * <P>代理apache commons-io的IOUtils,为soofa提供统一的工具视图 </P>
 * 
 * @author 汪一鸣
 */
public class IOUtils extends org.apache.commons.io.IOUtils {
	/**
	 * read lines.
	 * 
	 * @param is input stream.
	 * @return lines.
	 * @throws IOException.
	 */
	public static String[] readLine(InputStream is) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = reader.readLine()) != null)
				lines.add(line);
			return lines.toArray(new String[0]);
		} finally {
			reader.close();
		}
	}

	public static InputStream limitedInputStream(final InputStream is, final int limit) throws IOException {
		return new InputStream() {
			private int mPosition = 0, mMark = 0;
			private final int mLimit = Math.min(limit, is.available());

			@Override
			public int read() throws IOException {
				if (mPosition < mLimit) {
					mPosition++;
					return is.read();
				}
				return -1;
			}

			@Override
			public int read(byte b[], int off, int len) throws IOException {
				if (b == null)
					throw new NullPointerException();

				if (off < 0 || len < 0 || len > b.length - off)
					throw new IndexOutOfBoundsException();

				if (mPosition >= mLimit)
					return -1;

				if (mPosition + len > mLimit)
					len = mLimit - mPosition;

				if (len <= 0)
					return 0;

				is.read(b, off, len);
				mPosition += len;
				return len;
			}

			@Override
			public long skip(long len) throws IOException {
				if (mPosition + len > mLimit)
					len = mLimit - mPosition;

				if (len <= 0)
					return 0;

				is.skip(len);
				mPosition += len;
				return len;
			}

			@Override
			public int available() {
				return mLimit - mPosition;
			}

			@Override
			public boolean markSupported() {
				return is.markSupported();
			}

			@Override
			public void mark(int readlimit) {
				is.mark(readlimit);
				mMark = mPosition;
			}

			@Override
			public void reset() throws IOException {
				is.reset();
				mPosition = mMark;
			}

			@Override
			public void close() throws IOException {
			}
		};
	}
}
