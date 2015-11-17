package com.rda.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;


/**
 * MD5工具类 , 从恒富老项目迁移过来
 * @author Hai
 *
 */
public final class MD5 {

	private static Logger log = LoggerFactory.getLogger(MD5.class);

	/**
	 * 将数据加密MD5值(默认编码格式为UTF-8)
	 * @param content 需要MD5的值
	 * @return
	 */
	public final static String encrypt(String content) {
		return encrypt(content, "UTF-8");
	}

	/**
	 * 将数据加密MD5值
	 * @param content 需要MD5的值
	 * @param encoding 内容的编码格式 
	 * @return 返回MD5值
	 */
	public final static String encrypt(String content, String encoding) {
		String md5 = null;
		if (content == null) {
			content = "";
		}
		if (encoding == null || "".equals(encoding)) {
			encoding = "UTF-8";
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = content.getBytes(encoding);
			md.update(bytes);
			byte md5Bytes[] = md.digest();
			md5 = HexUtil.bytesToHexString(md5Bytes);
		} catch (Exception e) {
			log.error("MD5 encry error ;", e);
			throw  new RuntimeException(e);
		}
		return md5;
	}


}
