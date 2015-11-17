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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import de.idyl.winzipaes.AesZipFileDecrypter;
import de.idyl.winzipaes.AesZipFileEncrypter;
import de.idyl.winzipaes.impl.AESDecrypterBC;
import de.idyl.winzipaes.impl.AESEncrypterBC;


/**
 * <P>
 * 压缩工具类,支持压缩加密和解密
 * </P>
 * 
 * @author 汪一鸣
 */
public abstract class ZipUitls {
	private static Pattern	pattern	= Pattern.compile(RegularExpression.englishAndNumber);

	/**
	 * <p>
	 * 将1个或多个文件压缩到一个zip包中,并添加解码密码
	 * </p>
	 * 
	 * @param source
	 *            保存在压缩文件中的文件名和文件内容,注意文件名只允许英文,否则将抛异常
	 * @param password
	 *            需要给压缩包添加的密码,没有此密码无法解压缩文件
	 * @return
	 * @throws IOException
	 * @author 汪一鸣
	 */
	public static byte[] compressByte(Map<String, byte[]> source, String password) throws IOException {
		ByteArrayOutputStream ot = new ByteArrayOutputStream();
		AesZipFileEncrypter enc = new AesZipFileEncrypter(ot, new AESEncrypterBC());
		Matcher matcher = null;
		for (String fileName : source.keySet()) {
			matcher = pattern.matcher(fileName.replaceAll("\\.", ""));
			Assert.isTrue(matcher.matches(), "The file name[" + fileName + "] must be in English!");
			InputStream in = new ByteArrayInputStream(source.get(fileName));
			enc.add(fileName, in, password);
		}
		enc.close();
		return ot.toByteArray();
	}

	/**
	 * <p>
	 * 解压加过密的压缩文件
	 * </p>
	 * 
	 * @param zipFile
	 *            加过密的压缩文件
	 * @param fileNames
	 *            保存压缩包中文件名和解压缩后文件名的映射.key=压缩包中的文件名,value=解压后的文件名
	 * @param tempDir
	 *            存放解压文件的目录,不需要以'/'结尾.可以为null,如果NULL则会使用系统默认的临时目录
	 * @param password
	 *            解压密码
	 * @return
	 * @throws IOException
	 * @throws DataFormatException
	 * @author 汪一鸣
	 */
	public static List<File> uncompress(File zipFile, Map<String, String> fileNames, String tempDir, String password)
			throws IOException, DataFormatException {
		File outFile = null;
		List<File> outFiles = new ArrayList<File>(fileNames.size());
		AesZipFileDecrypter dec = new AesZipFileDecrypter(zipFile, new AESDecrypterBC());
		if (tempDir == null) {
			tempDir = System.getProperty("java.io.tmpdir");
		}
		for (String zipFileName : fileNames.keySet()) {
			String outFileName = fileNames.get(zipFileName);
			outFile = new File(tempDir + '/' + outFileName);
			if (outFile.exists()) {
				outFile.delete();
			}
			dec.extractEntryWithTmpFile(dec.getEntry(zipFileName), outFile, password);
			outFiles.add(outFile);
		}
		dec.close();
		return outFiles;
	}

}
