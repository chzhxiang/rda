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
package com.rda.security.utils;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;


/**
 * <P>用于各种加密的工具类,例如登录密码加密,支付密码加密,敏感的明文数据等</P>
 * 
 */
public abstract class EncryptionUtils {
	private static final Logger logger = LoggerFactory.getLogger(EncryptionUtils.class);

	private static final Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
	public static final String ENCRYPTION_RESULT_PREFIX = "{SOOFA-SHA}";
	/** 3DES算法名称 */
	public static final String TRIPLE_DES_ALGORITHM_NAME = "DESede";
	public static final String AES_KEY_ALGORITHM_NAME = "AES";
	/**
	 * ECB工作模式适合传递密钥时,对密钥进行加密,因为密钥比较短
	 */
	public static final String AES_CIPHER_ALGORITHM_NAME = "AES/CTR/PKCS7Padding";
	public static final String HMAC_MD5_ALGORITHM_NAME = "HmacMD5";
	public static final String HMAC_SHA224_ALGORITHM_NAME = "HmacSHA224";
	public static final String HMAC_SHA512_ALGORITHM_NAME = "HmacSHA512";
	public static final String TRIPLE_DES_TRANSFORMATION_NAME = "DESede/ECB/PKCS5Padding";
	public static final String CERTIFICATE_TYPE = "X.509";
	public static final String DEFAULT_CHARSET = "UTF-8";
	/**
	 * 必须16位长度
	 */
	private final static IvParameterSpec DEFAULT_IVS = new IvParameterSpec(new byte[] { 11, -19, 16, 33, -68, 88, 11,
			20, 24, 35, 68, 23, 60, 24, 29, 67 });
	public static final String DEFAULT_STRING_OUTPUT_TYPE = "base64";

	static {
		md5PasswordEncoder.setEncodeHashAsBase64(true);
		// 在调用Java API初始化相应的密钥工厂/密钥生成器等引擎类之前,手动注册Bouncy Castle(JCE提供者,实现了Java Cryptography
		// Extension接口包)
		Security.addProvider(new BouncyCastleProvider());
		logger.info("Java security provider[{}] is already installed", Security.getProperty("BC"));
	}

	/**
	 * <p>根据算法名称生成HMAC密钥</p>
	 * 
	 * @param algorithmName
	 * @param keySize 密钥长度
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @author 汪一鸣
	 */
	public static SecretKey initHmacKey(String algorithmName, int keySize) throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance(algorithmName);
		generator.init(keySize);
		SecretKey key = generator.generateKey();
		return key;
	}

	/**
	 * <p>随机生成HMAC + SHA224的密钥</p>
	 * 
	 * @param keySize 密钥长度
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @author 汪一鸣
	 */
	public static SecretKey initHmacSHA224Key(int keySize) throws NoSuchAlgorithmException {
		return initHmacKey(HMAC_SHA224_ALGORITHM_NAME, keySize);
	}

	/**
	 * <p>随机生成HMAC + SHA224的密钥</p>
	 * 
	 * @param keySize 密钥长度
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @author 汪一鸣
	 */
	public static SecretKey initHmacSHA512Key(int keySize) throws NoSuchAlgorithmException {
		return initHmacKey(HMAC_SHA512_ALGORITHM_NAME, keySize);
	}

	/**
	 * <p>随机生成HMAC-MD5的密钥</p>
	 * 
	 * @param keySize 密钥长度
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @author 汪一鸣
	 */
	public static SecretKey initHmacMD5Key(int keySize) throws NoSuchAlgorithmException {
		return initHmacKey(HMAC_MD5_ALGORITHM_NAME, keySize);
	}

	/**
	 * <p>生成用于单向加密普通密码(例如登录密码)的密钥</p>
	 * 
	 * @param keySize 密钥长度
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @author 汪一鸣
	 */
	public static SecretKey initGeneralPasswordKey(int keySize) throws NoSuchAlgorithmException {
		return initHmacSHA224Key(keySize);
	}

	/**
	 * <p>生成用于单向加密高强度密码的密钥</p>
	 * 
	 * @param keySize 密钥长度
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @author 汪一鸣
	 */
	public static SecretKey initStrongPasswordKey(int keySize) throws NoSuchAlgorithmException {
		return initHmacSHA512Key(keySize);
	}

	/**
	 * <p>生成用于单向加密传输信息的密钥</p>
	 * 
	 * @param keySize 密钥长度
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @author 汪一鸣
	 */
	public static SecretKey initSensitiveInformationKey(int keySize) throws NoSuchAlgorithmException {
		return initHmacMD5Key(keySize);
	}

	/**
	 * <p>生成用于双向加密传输信息的密钥</p>
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @author 汪一鸣
	 */
	public static byte[] initSensitiveInformationBidirectionalByteKey() throws Exception {
		SecretKey key = initSensitiveInformationBidirectionalKey();
		return key.getEncoded();
	}

	/**
	 * <p>生成用于双向加密传输信息的密钥</p>
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @author 汪一鸣
	 */
	public static SecretKey initSensitiveInformationBidirectionalKey() throws Exception {
		KeyGenerator generator = KeyGenerator.getInstance(AES_KEY_ALGORITHM_NAME, "BC");
		generator.init(128);// AES要求密钥长度为128/192/256
		SecretKey key = generator.generateKey();
		return key;
	}

	/**
	 * <p>根据指定HMAC算法计算摘要</p>
	 * 
	 * @param algorithmName
	 * @param data 必须UTF-8编码
	 * @param key
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @author 汪一鸣
	 */
	public static byte[] encodeHmac(String algorithmName, String data, byte[] key) throws Exception {
		// 还原密钥
		SecretKey secretKey = new SecretKeySpec(key, algorithmName);
		// 实例化MAC
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		// 初始化MAC
		mac.init(secretKey);
		// 执行消息摘要
		return mac.doFinal(StringUtils.getBytesUtf8(data));
	}

	/**
	 * <p>根据指定HMAC算法计算摘要</p>
	 * 
	 * @param algorithmName
	 * @param data 必须UTF-8编码
	 * @param key
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @author 汪一鸣
	 */
	public static byte[] encodeHmac(String algorithmName, byte[] data, byte[] key) throws Exception {
		// 还原密钥
		SecretKey secretKey = new SecretKeySpec(key, algorithmName);
		// 实例化MAC
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		// 初始化MAC
		mac.init(secretKey);
		// 执行消息摘要
		return mac.doFinal(data);
	}

	/**
	 * <p>便捷的普通密码(例如登录密码)加密,安全级别足够普通密码(例如登录密码)需求,进行HmacSHA224加密,注意,默认使用UTF-8编码</p>
	 * 
	 * @param password 必须UTF-8编码
	 * @param key 密钥
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @author 汪一鸣
	 */
	public static byte[] encodeGeneralPassword(String password, byte[] key) throws Exception {
		return encodeHmac(HMAC_SHA224_ALGORITHM_NAME, password, key);
	}

	/**
	 * <p>便捷的高强度密码加密,安全级别类似支付密码.注意,默认使用UTF-8编码</p>
	 * 
	 * @param password
	 * @param key
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encodeStrongPassword(String password, byte[] key) throws Exception {
		return encodeHmac(HMAC_SHA512_ALGORITHM_NAME, password, key);
	}

	/**
	 * <p>便捷的高强度密码加密,安全级别类似支付密码.注意,默认使用UTF-8编码</p>
	 * 
	 * @param password
	 * @param key
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encodeStrongPassword(byte[] password, byte[] key) throws Exception {
		return encodeHmac(HMAC_SHA512_ALGORITHM_NAME, password, key);
	}

	/**
	 * <p>加密高强度密码,从指定的keystore中获取密钥进行加密</p>
	 * 
	 * @param password 待加密的密码明文
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encodeStrongPassword(String password, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return encodeHmac(HMAC_SHA512_ALGORITHM_NAME, password, key);
	}

	/**
	 * <p>加密高强度密码,从指定的keystore中获取密钥进行加密</p>
	 * 
	 * @param password 待加密的密码明文
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encodeStrongPassword(byte[] password, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return encodeHmac(HMAC_SHA512_ALGORITHM_NAME, password, key);
	}

	/**
	 * <p>加密普通密码(例如登录密码),从指定的keystore中获取密钥进行加密(开销在Thinkpad T420S上,平均为17毫秒)</p>
	 * 
	 * @param password 待加密的密码明文
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encodeGeneralPassword(String password, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return encodeHmac(HMAC_SHA224_ALGORITHM_NAME, password, key);
	}

	/**
	 * <p>加密普通密码(例如登录密码),从指定的keystore中获取密钥进行加密</p>
	 * 
	 * @param password 待加密的密码明文
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encodeGeneralPassword(byte[] password, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return encodeHmac(HMAC_SHA224_ALGORITHM_NAME, password, key);
	}

	/**
	 * <p>便捷的普通密码(例如登录密码)加密,安全级别足够普通密码(例如登录密码)需求,进行HmacSHA224加密,注意,默认使用UTF-8编码</p>
	 * 
	 * @param password 必须UTF-8编码
	 * @param key 密钥
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @author 汪一鸣
	 */
	public static byte[] encodeGeneralPassword(byte[] password, byte[] key) throws Exception {
		return encodeHmac(HMAC_SHA224_ALGORITHM_NAME, password, key);
	}

	/**
	 * <p>单向加密敏感的信息,用于在传输过程中避免明文传输,从指定的keystore中获取密钥进行加密</p>
	 * 
	 * @param rawSensitiveInformation 待加密的明文敏感信息,UTF-8编码
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encodeSensitiveInformation(String rawSensitiveInformation, String keyStorePath,
			char[] keyStorePassword, String alias, char[] keyPasswork) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return encodeHmac(HMAC_MD5_ALGORITHM_NAME, rawSensitiveInformation, key);
	}

	/**
	 * <p>单向加密敏感的信息,用于在传输过程中避免明文传输,从指定的keystore中获取密钥进行加密</p>
	 * 
	 * @param rawSensitiveInformation 待加密的明文敏感信息,UTF-8编码
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encodeSensitiveInformation(byte[] rawSensitiveInformation, String keyStorePath,
			char[] keyStorePassword, String alias, char[] keyPasswork) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return encodeHmac(HMAC_MD5_ALGORITHM_NAME, rawSensitiveInformation, key);
	}

	/**
	 * <p>单向加密敏感的信息,用于在传输过程中避免明文传输</p>
	 * 
	 * @param rawMessage 明文敏感信息,UTF-8编码
	 * @param key 密钥
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static byte[] encodeSensitiveInformation(byte[] rawSensitiveInformation, byte[] key) throws Exception {
		return encodeHmac(HMAC_MD5_ALGORITHM_NAME, rawSensitiveInformation, key);
	}

	/**
	 * <p>单向加密敏感的信息,用于在传输过程中避免明文传输</p>
	 * 
	 * @param rawMessage 明文敏感信息,UTF-8编码
	 * @param key 密钥
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static byte[] encodeSensitiveInformation(String rawSensitiveInformation, byte[] key) throws Exception {
		return encodeHmac(HMAC_MD5_ALGORITHM_NAME, rawSensitiveInformation, key);
	}

	/**
	 * <p>转换密钥</p>
	 * 
	 * @param key
	 * @param algorithmName
	 * @return
	 */
	public static SecretKey toKey(byte[] key, String algorithmName) {
		// 还原密钥
		SecretKey secretKey = new SecretKeySpec(key, algorithmName);
		return secretKey;
	}

	/**
	 * <p>双向加密敏感信息,默认使用AES算法,从指定的keystore中获取密钥进行加盐</p>
	 * 
	 * @param data 待加密的密码明文
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encryptSensitiveInformation(byte[] data, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork, byte[] ivs) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return encryptSensitiveInformation(data, key, ivs);
	}

	/**
	 * <p>双向加密敏感信息,默认使用AES算法</p>
	 * 
	 * @param data 需要UTF-8编码
	 * @param key
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws Exception
	 */
	public static byte[] encryptSensitiveInformation(byte[] data, byte[] key, byte[] ivs) throws Exception {
		// 还原密钥
		Key k = toKey(key, AES_KEY_ALGORITHM_NAME);
		// 使用bouncy castle提供的PKCS7Padding填充方式,需要明确指定提供者"BC",因为JDK默认不提供此填充方式
		Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM_NAME, "BC");
		// 初始化,设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k, ivs == null ? DEFAULT_IVS : new IvParameterSpec(ivs));
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * <p>双向加密敏感信息,默认使用AES算法,从指定的keystore中获取密钥进行加盐</p>
	 * 
	 * @param data 待加密的密码明文
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encryptSensitiveInformation(String data, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork, byte[] ivs) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return encryptSensitiveInformation(data, key, ivs);
	}

	/**
	 * <p>双向加密敏感信息,默认使用AES算法</p>
	 * 
	 * @param data 需要UTF-8编码
	 * @param key 密钥
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws Exception
	 */
	public static byte[] encryptSensitiveInformation(String data, byte[] key, byte[] ivs) throws Exception {
		// 还原密钥
		Key k = toKey(key, AES_KEY_ALGORITHM_NAME);
		// 使用bouncy castle提供的PKCS7Padding填充方式,需要明确指定提供者"BC",因为JDK默认不提供此填充方式
		Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM_NAME, "BC");
		// 初始化,设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k, ivs == null ? DEFAULT_IVS : new IvParameterSpec(ivs));
		// 执行操作
		return cipher.doFinal(StringUtils.getBytesUtf8(data));
	}

	/**
	 * <p>双向解密敏感信息,默认使用AES算法</p>
	 * 
	 * @param data 需要UTF-8编码
	 * @param key 密钥
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws Exception
	 */
	public static byte[] decryptSensitiveInformation(byte[] data, byte[] key, byte[] ivs) throws Exception {
		// 还原密钥
		Key k = toKey(key, AES_KEY_ALGORITHM_NAME);
		// 使用bouncy castle提供的PKCS7Padding填充方式,需要明确指定提供者"BC",因为JDK默认不提供此填充方式
		Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM_NAME, "BC");
		// 初始化,设置为加密模式
		cipher.init(Cipher.DECRYPT_MODE, k, (ivs == null ? DEFAULT_IVS : new IvParameterSpec(ivs)));
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * <p>双向解密敏感信息,默认使用AES算法</p>
	 * 
	 * @param data 需要UTF-8编码
	 * @param key 密钥
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws Exception
	 */
	public static String decryptSensitiveInformationToString(byte[] data, byte[] key, byte[] ivs) throws Exception {
		byte[] info = decryptSensitiveInformation(data, key, ivs);
		return StringUtils.newStringUtf8(info);
	}

	/**
	 * <p>双向解密敏感信息,默认使用AES算法,从指定的keystore中获取密钥进行加盐</p>
	 * 
	 * @param data 待解密的数据
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] decryptSensitiveInformation(byte[] data, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork, byte[] ivs) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return decryptSensitiveInformation(data, key, ivs);
	}

	/**
	 * <p>双向解密敏感信息,默认使用AES算法,从指定的keystore中获取密钥进行加盐</p>
	 * 
	 * @param data 待解密的数据
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static String decryptSensitiveInformationToString(byte[] data, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork, byte[] ivs) throws Exception {
		byte[] info = decryptSensitiveInformation(data, keyStorePath, keyStorePassword, alias, keyPasswork, ivs);
		return StringUtils.newStringUtf8(info);
	}

	/**
	 * <p>双向解密敏感信息,默认使用AES算法</p>
	 * 
	 * @param data 需要UTF-8编码
	 * @param key 密钥
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws Exception
	 */
	public static byte[] decryptSensitiveInformation(String data, byte[] key, byte[] ivs) throws Exception {
		// 还原密钥
		Key k = toKey(key, AES_KEY_ALGORITHM_NAME);
		// 使用bouncy castle提供的PKCS7Padding填充方式,需要明确指定提供者"BC",因为JDK默认不提供此填充方式
		Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM_NAME, "BC");
		// 初始化,设置为加密模式
		cipher.init(Cipher.DECRYPT_MODE, k, (ivs == null ? DEFAULT_IVS : new IvParameterSpec(ivs)));
		// 执行操作
		return cipher.doFinal(StringUtils.getBytesUtf8(data));
	}

	/**
	 * <p>双向解密敏感信息,默认使用AES算法</p>
	 * 
	 * @param data 需要UTF-8编码
	 * @param key 密钥
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return 可使用org.apache.commons.codec.binary.Base64.encodeBase64String方法进行查看
	 * @throws Exception
	 */
	public static String decryptSensitiveInformationToString(String data, byte[] key, byte[] ivs) throws Exception {
		byte[] info = decryptSensitiveInformation(data, key, ivs);
		return StringUtils.newStringUtf8(info);
	}

	/**
	 * <p>双向解密敏感信息,默认使用AES算法,从指定的keystore中获取密钥进行加盐</p>
	 * 
	 * @param data 待解密的数据,UTF-8编码
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] decryptSensitiveInformation(String data, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork, byte[] ivs) throws Exception {
		byte[] key = getKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		return decryptSensitiveInformation(data, key, ivs);
	}

	/**
	 * <p>双向解密敏感信息,默认使用AES算法,从指定的keystore中获取密钥进行加盐</p>
	 * 
	 * @param data 待解密的数据,UTF-8编码
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥别名
	 * @param keyPasswork 密钥对应的密码
	 * @param ivs 初始化向量,必须16个元素的byte数组,如果为NULL,将使用默认值DEFAULT_IVS
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static String decryptSensitiveInformationToString(String data, String keyStorePath, char[] keyStorePassword,
			String alias, char[] keyPasswork, byte[] ivs) throws Exception {
		byte[] info = decryptSensitiveInformation(data, keyStorePath, keyStorePassword, alias, keyPasswork, ivs);
		return StringUtils.newStringUtf8(info);
	}

	/**
	 * <p>加载KeyStore</p>
	 * 
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param password 访问密钥库的密码
	 * @param keystoreType keystore的类型,如果为NULL,则默认使用KeyStore.getDefaultType()
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static KeyStore loadKeyStore(String keyStorePath, char[] password, String keystoreType) throws Exception {
		KeyStore ks = KeyStore.getInstance(keystoreType == null ? KeyStore.getDefaultType() : keystoreType);
		Resource[] resources = ResourceUtils.getResources(keyStorePath);
		if (resources.length == 1) {
			InputStream is = null;
			try {
				is = resources[0].getInputStream();
				ks.load(is, password);
				return ks;
			} finally {
				if (is != null) {
					is.close();
				}
			}
		} else if (resources.length < 1) {
			throw new IllegalArgumentException("In the path [" + keyStorePath + "], found less than one KeyStore");
		} else {
			throw new IllegalArgumentException("In the path [" + keyStorePath + "], found more than one KeyStore");
		}
	}

	/**
	 * <p>从keystore中获取密钥</p>
	 * 
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 密钥条目的别名
	 * @param keyPasswork 密钥条目对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] getKeyByKeyStore(String keyStorePath, char[] keyStorePassword, String alias, char[] keyPasswork)
			throws Exception {
		KeyStore keyStore = loadKeyStore(keyStorePath, keyStorePassword, "jceks");// 只有jceks类型才能保存单独的密钥
		SecretKeyEntry entry = (SecretKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(keyPasswork));
		return entry.getSecretKey().getEncoded();
	}

	/**
	 * <p>从证书文件读取证书.'.crt'和'.cer'文件都可以读取 .cer是IE导出的公钥证书（der格式）</p>
	 * 
	 * @param certificatePath 证书文件路径:可以直接加载指定的文件,例如"file:C:/kft.cer",也可以从classpath下加载,例如
	 *            "classpath:/kft.cer" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static Certificate getCertificate(String certificatePath) throws Exception {
		Resource[] resources = ResourceUtils.getResources(certificatePath);
		if (resources.length == 1) {
			InputStream inputStream = null;
			try {
				inputStream = resources[0].getInputStream();
				// 实例化证书工厂
				CertificateFactory cf = CertificateFactory.getInstance(CERTIFICATE_TYPE);
				Certificate cert = cf.generateCertificate(inputStream);
				return cert;
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} else if (resources.length < 1) {
			throw new IllegalArgumentException("In the path [" + certificatePath + "], found less than one certificate");
		} else {
			throw new IllegalArgumentException("In the path [" + certificatePath + "], found more than one certificate");
		}
	}

	/**
	 * <p>从KeyStore中读取证书</p>
	 * 
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 证书的别名
	 * @return
	 * @throws Exception
	 */
	public static Certificate getCertificate(String keyStorePath, char[] keyStorePassword, String alias)
			throws Exception {
		KeyStore ks = loadKeyStore(keyStorePath, keyStorePassword, null);
		return ks.getCertificate(alias);
	}

	/**
	 * <p>从密钥库KeyStore中获取私钥</p>
	 * 
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 私钥的别名
	 * @param keyPasswork 私钥条目对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static PrivateKey getPrivateKeyByKeyStore(String keyStorePath, char[] keyStorePassword, String alias,
			char[] keyPassword) throws Exception {
		KeyStore ks = loadKeyStore(keyStorePath, keyStorePassword, null);
		PrivateKey key = (PrivateKey) ks.getKey(alias, keyPassword);
		return key;
	}

	/**
	 * <p>从证书中获取公钥</p>
	 * 
	 * @param certificatePath 证书文件路径:可以直接加载指定的文件,例如"file:C:/kft.cer",也可以从classpath下加载,例如
	 *            "classpath:/kft.cer" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static PublicKey getPublicKeyByCertificate(String certificatePath) throws Exception {
		Certificate x509Certificate = getCertificate(certificatePath);
		return x509Certificate.getPublicKey();
	}

	/**
	 * <p>使用从KeyStore中取出的私钥,对数据进行加密</p>
	 * 
	 * @param data 待加密的数据
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 私钥的别名
	 * @param keyPasswork 私钥条目对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath, char[] keyStorePassword, String alias,
			char[] keyPasswork) throws Exception {
		PrivateKey privateKey = getPrivateKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		// 对数据进行加密
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * <p>利用从证书中取出的公钥,对私钥加密过的数据进行解密</p>
	 * 
	 * @param data 待解密的数据
	 * @param certificatePath 证书文件路径:可以直接加载指定的文件,例如"file:C:/kft.cer",也可以从classpath下加载,例如
	 *            "classpath:/kft.cer" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @return
	 * @author 汪一鸣
	 */
	public static byte[] decryptByPublicKey(byte[] data, String certificatePath) throws Exception {
		PublicKey publicKey = getPublicKeyByCertificate(certificatePath);
		// 对数据进行解密
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

	/**
	 * <p>利用从证书中取出的公钥,对数据进行加密</p>
	 * 
	 * @param data 待加密的数据
	 * @param certificatePath 证书文件路径:可以直接加载指定的文件,例如"file:C:/kft.cer",也可以从classpath下加载,例如
	 *            "classpath:/kft.cer" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @return
	 * @author 汪一鸣
	 */
	public static byte[] encryptByPublicKey(byte[] data, String certificatePath) throws Exception {
		PublicKey publicKey = getPublicKeyByCertificate(certificatePath);
		// 对数据进行加密
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

	/**
	 * <p>使用从KeyStore中取出的私钥,对私钥加密过的数据进行解密</p>
	 * 
	 * @param data 待解密的数据
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 私钥的别名
	 * @param keyPasswork 私钥条目对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String keyStorePath, char[] keyStorePassword, String alias,
			char[] keyPasswork) throws Exception {
		PrivateKey privateKey = getPrivateKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		// 对数据进行解密
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * <p>签名,利用从证书里取出的签名算法和keystore的私钥,对数据进行签名.可以由公钥验证签名</p>
	 * 
	 * @param data 待签名数据
	 * @param keyStorePath
	 *            keystore文件路径:可以直接加载指定的文件,例如"file:C:/KFTCIPKeystore.keystore",也可以从classpath下加载,例如
	 *            "classpath:/KFTCIPKeystore.keystore" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @param keyStorePassword 访问密钥库的密码
	 * @param alias 私钥的别名
	 * @param keyPasswork 私钥条目对应的密码
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static byte[] signByX509Certificate(byte[] data, String keyStorePath, char[] keyStorePassword, String alias,
			char[] keyPasswork) throws Exception {
		X509Certificate x509Certificate = (X509Certificate) getCertificate(keyStorePath, keyStorePassword, alias);
		Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
		PrivateKey privateKey = getPrivateKeyByKeyStore(keyStorePath, keyStorePassword, alias, keyPasswork);
		// 初始化签名,由私钥构建
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}

	/**
	 * <p>验证签名,从证书中获取公钥来验证签名是否正确</p>
	 * 
	 * @param data 传输的数据
	 * @param sign 对传输数据的签名
	 * @param certificatePath 证书文件路径:可以直接加载指定的文件,例如"file:C:/kft.cer",也可以从classpath下加载,例如
	 *            "classpath:/kft.cer" 支持ANT语法 注意:从classpath下加载时,优先使用 thread context
	 *            ClassLoader,没有找到的情况下,使用当前类加载器
	 * @return
	 * @throws Exception
	 * @author 汪一鸣
	 */
	public static boolean verifySign(byte[] data, byte[] sign, String certificatePath) throws Exception {
		X509Certificate certificate = (X509Certificate) getCertificate(certificatePath);
		Signature signature = Signature.getInstance(certificate.getSigAlgName());
		// 由证书初始化签名,使用了证书中的公钥
		signature.initVerify(certificate);
		signature.update(data);
		return signature.verify(sign);
	}

	//
	// /**
	// * <p>检查实际密码和期望的密码是否一致</p>
	// *
	// * @param expected 需要检验的密码,必须UTF-8编码
	// * @param actual 实际保存的密码,必须UTF-8编码
	// * @return
	// * @author 汪一鸣
	// */
	// public static boolean checkGeneralPassword(String expected, String actual) {
	// return passwordEncryptor.matches(expected, actual);
	// }
	//
	// /**
	// * <p>检查实际密码和期望的密码是否一致</p>
	// *
	// * @param expected 需要检验的密码,必须UTF-8编码
	// * @param actual 实际保存的密码,必须UTF-8编码
	// * @return
	// * @author 汪一鸣
	// */
	// public static boolean checkGeneralPassword(byte[] expected, byte[] actual) {
	// return passwordEncryptor.matches(StringUtils.newStringUtf8(expected),
	// StringUtils.newStringUtf8(actual));
	// }
	//
	// /**
	// * <p>使用3DES加密,默认字符编码为UTF-8</p>
	// *
	// * @param message 待加密的信息,必须UTF-8编码
	// * @param key 3DES密钥字符串
	// * @return
	// * @throws Exception
	// * @author 汪一鸣
	// */
	// public static byte[] DesAndBase64Encrypt(String message, String key) throws Exception {
	// Cipher cipher = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION_NAME);
	// // byte[] keybyte=new BASE64Decoder().decodeBuffer(“YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4″);
	// byte[] keybyte = key.getBytes(DEFAULT_CHARSET);
	// DESedeKeySpec desKeySpec = new DESedeKeySpec(keybyte);
	// SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM_NAME);
	// SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	// cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	// byte[] result = Base64.encodeBase64(cipher.doFinal(message.getBytes(DEFAULT_CHARSET)));
	// return result;
	// }
	//
	// /**
	// * <p>使用3DES加密,默认字符编码为UTF-8</p>
	// *
	// * @param message 待加密的信息,必须UTF-8编码
	// * @param key 3DES密钥字符串
	// * @return
	// * @throws Exception
	// * @author 汪一鸣
	// */
	// public static byte[] DesAndBase64Encrypt(byte[] message, String key) throws Exception {
	// Assert.notNull(message);
	// Cipher cipher = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION_NAME);
	// // byte[] keybyte=new BASE64Decoder().decodeBuffer(“YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4″);
	// byte[] keybyte = key.getBytes(DEFAULT_CHARSET);
	// DESedeKeySpec desKeySpec = new DESedeKeySpec(keybyte);
	// SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM_NAME);
	// SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	// cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	// byte[] result = Base64.encodeBase64(cipher.doFinal(message));
	// return result;
	// }
	//
	// /**
	// * <p>使用3DES加密,默认字符编码为UTF-8</p>
	// *
	// * @param message 待加密的信息,必须UTF-8编码
	// * @param key 3DES密钥字符串
	// * @return
	// * @throws Exception
	// * @author 汪一鸣
	// */
	// public static String DesAndBase64StringEncrypt(String message, String key) throws Exception {
	// Assert.hasText(message);
	// return StringUtils.newStringUtf8(DesAndBase64Encrypt(message, key));
	// }
	//
	// /**
	// * <p>使用3DES解密,默认字符编码为UTF-8</p>
	// *
	// * @param message 待解密的信息,必须UTF-8编码
	// * @param key 3DES密钥字符串
	// * @return
	// * @throws Exception
	// * @author 汪一鸣
	// */
	// public static byte[] DesAndBase64Decrypt(byte[] message, String key) throws Exception {
	// Assert.notNull(message);
	// Cipher cipher = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION_NAME);
	// // byte[] keybyte=new BASE64Decoder().decodeBuffer(“YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4″);
	// byte[] keybyte = key.getBytes(DEFAULT_CHARSET);
	// DESedeKeySpec desKeySpec = new DESedeKeySpec(keybyte);
	// SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM_NAME);
	// SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	// cipher.init(Cipher.DECRYPT_MODE, secretKey);
	// byte[] result = cipher.doFinal(Base64.decodeBase64(message));
	// return result;
	// }
	//
	// /**
	// * <p>使用3DES解密,默认字符编码为UTF-8</p>
	// *
	// * @param message 待解密的信息,必须UTF-8编码
	// * @param key 3DES密钥字符串
	// * @return
	// * @throws Exception
	// * @author 汪一鸣
	// */
	// public static byte[] DesAndBase64Decrypt(String message, String key) throws Exception {
	// Assert.notNull(message);
	// Cipher cipher = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION_NAME);
	// // byte[] keybyte=new BASE64Decoder().decodeBuffer(“YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4″);
	// byte[] keybyte = key.getBytes(DEFAULT_CHARSET);
	// DESedeKeySpec desKeySpec = new DESedeKeySpec(keybyte);
	// SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM_NAME);
	// SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	// cipher.init(Cipher.DECRYPT_MODE, secretKey);
	// byte[] result = cipher.doFinal(Base64.decodeBase64(message));
	// return result;
	// }
	//
	// /**
	// * <p>使用3DES解密,默认字符编码为UTF-8</p>
	// *
	// * @param message 待解密的信息,必须UTF-8编码
	// * @param key 3DES密钥字符串
	// * @return
	// * @throws Exception
	// * @author 汪一鸣
	// */
	// public static String DesAndBase64StringDecrypt(String message, String key) throws Exception {
	// Assert.notNull(message);
	// return StringUtils.newStringUtf8(DesAndBase64Decrypt(message, key));
	// }
	//
	// /**
	// * <p>判断是否用soofa的加密工具类加密过,不包括3DES加密</p>
	// *
	// * @param message 不能为空,且必须是UTF-8编码
	// * @return true标识是使用soofa加密工具加密过的
	// * @author 汪一鸣
	// */
	// public static boolean isEncrypted(String message) {
	// Assert.hasText(message);
	// return message.startsWith(ENCRYPTION_RESULT_PREFIX);
	// }
	//
	// /**
	// * <p>判断是否用soofa的加密工具类加密过,不包括3DES加密</p>
	// *
	// * @param message 不能为空,且必须是UTF-8编码
	// * @return true标识是使用soofa加密工具加密过的
	// * @author 汪一鸣
	// */
	// public static boolean isEncrypted(byte[] message) {
	// Assert.notNull(message);
	// return StringUtils.newStringUtf8(message).startsWith(ENCRYPTION_RESULT_PREFIX);
	// }
	//
	// //
	// //
	// // -----BEGIN CERTIFICATE-----开始 文件头不许有其它内容
	// // -----END CERTIFICATE-----
	// }
	//

	//
	// /**
	// * <p>读取PKCS12格式的key（私钥）pfx格式</p>
	// *
	// * @param certFile
	// * @param pfxPassword
	// * @throws Exception
	// * @author 汪一鸣
	// */
	// public void ReadPKCS12PrivateKey(File certFile, String pfxPassword) throws Exception {
	//
	// Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	// InputStream fis = null;
	// try {
	// fis = new FileInputStream(certFile);
	//
	// if (Security.getProvider("BC") == null) {
	// throw new Exception("Can't load BouncyCastle!");
	// }
	//
	// // Create a keystore object
	// // KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
	// KeyStore keyStore = KeyStore.getInstance("PKCS12", Security.getProvider("BC"));
	// // Load the file into the keystore
	// keyStore.load(fis, pfxPassword.toCharArray());
	//
	// // String aliaesName = "abcd";
	// Enumeration<String> aliases = keyStore.aliases();
	// String keyAlias = null;
	// if (aliases != null) {
	// while (aliases.hasMoreElements()) {
	// keyAlias = aliases.nextElement();
	// priKey = (PrivateKey) (keyStore.getKey(keyAlias, pfxPassword.toCharArray()));
	// }
	// }
	//
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// if (fis != null)
	// fis.close();
	// }
	//
	// }
	//
	// // 读取PKCS12格式的key（私钥）pfx格式 或JKS类型的私钥
	// public void ReadJKSPrivateKey(File file, String pfxPassword) throws Exception {
	// Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	// InputStream fis = null;
	// try {
	// fis = new FileInputStream(file);
	// if (Security.getProvider("BC") == null) {
	// throw new Exception("不能Load入BouncyCastle!");
	// }
	//
	// // 指定keystore类型
	// KeyStore keyStore = KeyStore.getInstance("JKS");
	// // 载入keystore
	// keyStore.load(fis, pfxPassword.toCharArray());
	// // 从keystore中读取私钥条目
	// Enumeration<String> aliases = keyStore.aliases();
	// String keyAlias = null;
	// if (aliases != null) {
	// while (aliases.hasMoreElements()) {
	// keyAlias = aliases.nextElement();
	// // 读取私钥
	// priKey = (PrivateKey) (keyStore.getKey(keyAlias, pfxPassword.toCharArray()));
	// }
	// }
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// if (fis != null)
	// fis.close();
	// }
	//
	// }
	//
	// // 读取PKCS8格式的私钥(openssl 生成的就是该格式)
	// // -----BEGIN CERTIFICATE-----开始 文件头不许有其它内容
	// // -----END CERTIFICATE-----
	// public void ReadPKCS8PrivateKey(String keyfile, String password) throws Exception {
	// BufferedReader br = new BufferedReader(new FileReader(keyfile));
	// String s = br.readLine();
	//
	// String str = "";
	// s = br.readLine();
	// while (s.charAt(0) != '-') {
	// str += s + "\r";
	// s = br.readLine();
	// }
	//
	// // 编码转换，进行BASE64解码
	// BASE64Decoder base64decoder = new BASE64Decoder();
	// byte[] b = base64decoder.decodeBuffer(str);
	// // 生成私匙
	// // KeyFactory kf = KeyFactory.getInstance("RSA");//"SunJSSE"
	// KeyFactory kf = KeyFactory.getInstance("RSA", new
	// org.bouncycastle.jce.provider.BouncyCastleProvider());
	// PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(b);
	// priKey = kf.generatePrivate(keySpec);
	// }
	//
	// // 读取原始私钥
	// public void ReadRawPrivateKey(String keyfile) throws Exception {
	//
	// FileInputStream br = new FileInputStream(keyfile);
	//
	// byte[] b1 = new byte[2000];
	// int k = br.read(b1);
	//
	// byte[] b = new byte[k];
	// System.arraycopy(b1, 0, b, 0, k);
	// // 生成私匙
	// // KeyFactory kf = KeyFactory.getInstance("RSA");//"SunJSSE"
	// KeyFactory kf = KeyFactory.getInstance("RSA", new
	// org.bouncycastle.jce.provider.BouncyCastleProvider());
	// PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(b);
	// priKey = kf.generatePrivate(keySpec);
	//
	// }
	//
	// // 读取原始公钥
	// public void ReadRawPublicKey(String keyfile) throws Exception {
	//
	// FileInputStream br = new FileInputStream(keyfile);
	//
	// byte[] b1 = new byte[2000];
	// int k = br.read(b1);
	//
	// byte[] b = new byte[k];
	// System.arraycopy(b1, 0, b, 0, k);
	// // 生成私匙
	// // KeyFactory kf = KeyFactory.getInstance("RSA");//"SunJSSE"
	// KeyFactory kf = KeyFactory.getInstance("RSA", new
	// org.bouncycastle.jce.provider.BouncyCastleProvider());
	// X509EncodedKeySpec keySpec = new X509EncodedKeySpec(b);
	// pubKey = kf.generatePublic(keySpec);
	// }
	//
	// // 读取原始公钥
	// public void ReadRawPublicKey(byte[] rawpublickey) throws Exception {
	//
	// // 生成私匙
	// // KeyFactory kf = KeyFactory.getInstance("RSA");//"SunJSSE"
	// KeyFactory kf = KeyFactory.getInstance("RSA", new
	// org.bouncycastle.jce.provider.BouncyCastleProvider());
	// X509EncodedKeySpec keySpec = new X509EncodedKeySpec(rawpublickey);
	// pubKey = kf.generatePublic(keySpec);
	// }
	//
	// // 根据128位大整数生成公钥
	// public void ReadRawPublicKeyFromBigInt(String base16pubkey) throws Exception {
	// byte e[] = { 0, 1, 0, 1 };
	//
	// byte[] bigint = Base16.hexStrToBytes("00" + base16pubkey);
	// try {
	// KeyFactory keyf = KeyFactory.getInstance("RSA");
	// RSAPublicKeySpec pubkf = new RSAPublicKeySpec(new BigInteger(bigint), new BigInteger(e));
	// pubKey = keyf.generatePublic(pubkf);
	// } catch (Exception ex) {
	//
	// }
	// }
	//
	// public String ReadCerFile(String certfile) throws Exception {
	// BufferedReader br = new BufferedReader(new FileReader(certfile));
	// String s = "";
	// String str = "";
	// boolean bFlag = false;
	// while (true) {
	// s = br.readLine();
	// if (s == null)
	// break;
	// s.trim();
	// if (s.equals(""))
	// continue;
	//
	// if (s.charAt(0) == '-') {
	// if (bFlag)
	// break;
	// bFlag = true;
	// continue;
	// }
	// // 提取证书
	// if (bFlag) {
	// str += s + "\r";
	// continue;
	// }
	//
	// }
	//
	// if (str.equals("")) {
	// throw new Exception("读取证书错误");
	// }
	// return str;
	// // 编码转换，进行BASE64解码
	// // BASE64Decoder base64decoder = new BASE64Decoder();
	// // byte[] b = base64decoder.decodeBuffer(str);
	//
	// }
	//
	// // 读取X509格式公钥pem证书
	// public void ReadX509PublicKey(String certfile) throws Exception {
	// String str = ReadCerFile(certfile);
	// // 编码转换，进行BASE64解码
	// BASE64Decoder base64decoder = new BASE64Decoder();
	// byte[] b = base64decoder.decodeBuffer(str);
	//
	// // 生成公钥
	// KeyFactory kf = KeyFactory.getInstance("RSA");
	// // KeyFactory kf = KeyFactory.getInstance("RSA", new
	// // org.bouncycastle.jce.provider.BouncyCastleProvider());
	// PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(b);
	//
	// pubKey = kf.generatePublic(keySpec);
	//
	// }
	//
	// // 签名algorithm="SHA1withRSA" "MD5withRSA"
	// public byte[] SignMemory(byte[] info, String algorithm) throws Exception {
	// // 用私钥对信息生成数字签名
	// java.security.Signature signet = java.security.Signature.getInstance(algorithm);//
	// MD5withRSA//
	// signet.initSign(priKey);
	// signet.update(info);
	// byte[] signed = signet.sign(); // 对信息的数字签名
	// return signed;
	// }
	//
	// // 验证内存签名
	// public boolean VerifyMemoryMD5(byte[] info, byte[] signed) throws Exception {
	// return VerifyMemory(info, signed, "MD5withRSA");
	// }
	//
	// public boolean VerifyMemorySHA1(byte[] info, byte[] signed) throws Exception {
	// return VerifyMemory(info, signed, "SHA1withRSA");
	// }
	//
	// public boolean VerifyMemory(byte[] info, byte[] signed, String method) throws Exception {
	// // method = "SHA1withRSA" "MD5withRSA"
	// java.security.Signature signetcheck = java.security.Signature.getInstance(method);
	// signetcheck.initVerify(pubKey);
	// signetcheck.update(info);
	// if (signetcheck.verify(signed)) {
	// return true;
	// } else {
	// return false;
	// }
	//
	// }
	//
	// public byte[] SignFile(String inFile) throws Exception {
	// byte[] buf = new byte[1024];
	// int num;
	// FileInputStream fin = new FileInputStream(inFile);
	// // String myinfo =
	// // "orderId=10dkfadsfksdkssdkd&amount=80&orderTime=20060509"; // 要签名的信息
	// // signet.update(myinfo.getBytes("ISO-8859-1"));
	//
	// // 用私钥对信息生成数字签名
	// java.security.Signature signet = java.security.Signature.getInstance("MD5withRSA");
	// signet.initSign(priKey);
	//
	// while ((num = fin.read(buf, 0, buf.length)) != -1) {
	// signet.update(buf, 0, num);
	// }
	//
	// byte[] signed = signet.sign(); // 对信息的数字签名
	//
	// return signed;
	// }
	//
	// public boolean VerifyFileMD5(String inFile, byte[] pSigBuf, int SigLen) throws Exception {
	// return VerifyFile(inFile, pSigBuf, SigLen, "MD5withRSA");
	// }
	//
	// public boolean VerifyFileSHA1(String inFile, byte[] pSigBuf, int SigLen) throws Exception {
	// return VerifyFile(inFile, pSigBuf, SigLen, "SHA1withRSA");
	// }
	//
	// public boolean VerifyFile(String inFile, byte[] pSigBuf, int SigLen, String method) throws
	// Exception {
	// // method = "MD5withRSA" "SHA1withRSA"
	// byte[] buf = new byte[1024];
	// int num;
	// FileInputStream fin = new FileInputStream(inFile);
	//
	// java.security.Signature signetcheck = java.security.Signature.getInstance(method);
	// signetcheck.initVerify(pubKey);
	// while ((num = fin.read(buf, 0, buf.length)) != -1) {
	// signetcheck.update(buf, 0, num);
	// }
	//
	// if (signetcheck.verify(pSigBuf, 0, SigLen)) {
	//
	// return true;
	// } else {
	//
	// return false;
	// }
	//
	// }
	//
	// // 公钥加密
	// public byte[] RSAEncrypt(byte[] buf) throws Exception {
	// Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	// // Cipher rsaCipher=Cipher.getInstance("RSA");
	// rsaCipher.init(Cipher.ENCRYPT_MODE, pubKey);
	// rsaCipher.update(buf);
	// return rsaCipher.doFinal();
	//
	// }
	//
	// // 私钥解密
	// public byte[] RSADecrypt(byte[] buf) throws Exception {
	// Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	//
	// rsaCipher.init(Cipher.DECRYPT_MODE, priKey);
	// rsaCipher.update(buf);
	// return rsaCipher.doFinal();
	//
	// }
	//
	// public byte[] PrivatekeyDecrypt(byte[] cipherText) throws Exception {
	// Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS5Padding");
	// cipher.init(Cipher.DECRYPT_MODE, priKey);
	// byte[] plainText = cipher.doFinal(cipherText);
	//
	// return plainText;
	//
	// }

	public static String decodeMD5(String encodedMD5String) {
		return encodedMD5String;

	}

	/**
	 * <p>判断是否是经过base64编码</p>
	 * 
	 * @param msg
	 * @return true表示是经过base编码的
	 * @author 汪一鸣
	 */
	public static boolean isBase64(String msg) {
		return Base64.isBase64(msg);
	}

	/**
	 * <p>判断是否是经过base64编码</p>
	 * 
	 * @param msg
	 * @return true表示是经过base编码的
	 * @author 汪一鸣
	 */
	public static boolean isBase64(byte[] msg) {
		return Base64.isBase64(msg);
	}

	/**
	 * <p>判断是否是经过base64编码</p>
	 * 
	 * @param msg
	 * @return true表示是经过base编码的
	 * @author 汪一鸣
	 */
	public static boolean isBase64(byte msg) {
		return Base64.isBase64(msg);
	}

	/**
	 * <p>解码base64编码的数据</p>
	 * 
	 * @param base64String 被base64编码前必须是 UTF-8编码
	 * @return
	 * @author 汪一鸣
	 */
	public static String decodeBase64(String base64String) {
		byte[] decoded = new Base64().decode(base64String);
		return StringUtils.newStringUtf8(decoded);
	}
}
