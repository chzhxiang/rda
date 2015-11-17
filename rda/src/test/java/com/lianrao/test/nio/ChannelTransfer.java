/*******************************************************************************
 * Project Key : fundpay
 * Create on May 31, 2013 3:31:39 PM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.lianrao.test.nio;

import java.io.FileInputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * <P>TODO</P>
 * @author lianrao
 */
/**
* Test channel transfer. This is a very simplistic concatenation
* program. It takes a list of file names as arguments, opens each
* in turn and transfers (copies) their content to the given
* WritableByteChannel (in this case, stdout).
* 
96
* Created April 2002
* @author Ron Hitchens (ron@ronsoft.com)
*/
public class ChannelTransfer {
	public static void main(String[] argv) throws Exception {
		if (argv.length == 0) {
			System.err.println("Usage: filename ...");
			return;
		}
		catFiles(Channels.newChannel(System.out), argv);
	}

	// Concatenate the content of each of the named files to
	// the given channel. A very dumb version of 'cat'.
	private static void catFiles(WritableByteChannel target, String[] files) throws Exception {
		for (int i = 0; i < files.length; i++) {
			FileInputStream fis = new FileInputStream(files[i]);
			FileChannel channel = fis.getChannel();
			channel.transferTo(0, channel.size(), target);
			channel.close();
			fis.close();
		}
	}
}
