/*******************************************************************************
 * Project Key : fundpay
 * Create on May 31, 2013 4:35:35 PM
 * Copyright (c) 2008 - 2011.深圳市快付通金融网络科技服务有限公司版权所有. 粤ICP备10228891号
 * 注意：本内容仅限于深圳市快付通金融网络科技服务有限公司内部传阅，禁止外泄以及用于其他的商业目的
 ******************************************************************************/
package com.lianrao.test.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;


/**
 * <P>TODO</P>
 * @author lianrao
 */

/**
* Demonstrate asynchronous connection of a SocketChannel.
* @author Ron Hitchens (ron@ronsoft.com)
*/
public class ConnectAsync {
	public static void main(String[] argv) throws Exception {
		String host = "localhost";
		int port = 80;
		if (argv.length == 2) {
			host = argv[0];
			port = Integer.parseInt(argv[1]);
		}
		InetSocketAddress addr = new InetSocketAddress(host, port);
		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(false);
		System.out.println("initiating connection");
		sc.connect(addr);
		while (!sc.finishConnect()) {
			doSomethingUseful();
		}
		System.out.println("connection established");
		// Do something with the connected socket
		// The SocketChannel is still nonblocking
		sc.close();
	}

	private static void doSomethingUseful() {
		System.out.println("doing something useless");
	}
}
