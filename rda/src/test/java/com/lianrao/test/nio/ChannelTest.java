/*******************************************************************************
 * Project Key : fundpay
 * Create on May 31, 2013 11:05:02 AM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.lianrao.test.nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class ChannelTest {
	
	public static void main(String[] args) throws IOException{
		FileInputStream input = 
				new FileInputStream("target/test-classes/com/lianrao/test/nio/ChannelTest.class");
		FileChannel channel = input.getChannel();
		ByteBuffer src = ByteBuffer.allocate(10);
		src.put((byte) 'h');
		channel.close();
		channel.write(src);
	}

}
