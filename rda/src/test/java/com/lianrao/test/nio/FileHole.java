/*******************************************************************************
 * Project Key : fundpay
 * Create on May 31, 2013 11:44:56 AM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.lianrao.test.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class FileHole {
	public static void main(String[] argv) throws IOException {
		// Create a temp file, open for writing, and get
		// a FileChannel
		File temp = File.createTempFile("holy", null);
		RandomAccessFile file = new RandomAccessFile(temp, "rw");
		FileChannel channel = file.getChannel();
		// Create a working buffer
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100);
		putData(0, byteBuffer, channel);
		putData(5000000, byteBuffer, channel);
		putData(50000, byteBuffer, channel);
		// Size will report the largest position written, but
		// there are two holes in this file. This file will
		// not consume 5 MB on disk (unless the filesystem is 
		// extremely brain-damaged)
		System.out.println("Wrote temp file '" + temp.getPath() + "', size=" + channel.size());
		channel.close();
		file.close();
	}

	private static void putData(int position, ByteBuffer buffer, FileChannel channel) throws IOException {
		String string = "*<-- location " + position;
		buffer.clear();
		buffer.put(string.getBytes("US-ASCII"));
		buffer.flip();
		channel.position(position);
		channel.write(buffer);
	}
}