/*******************************************************************************
 * rda
 ******************************************************************************/
package com.lianrao.test.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.spy.memcached.MemcachedClient;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class MemcachedTest {

	public static void main(String[] args) throws IOException{
		MemcachedClient c=new MemcachedClient(
			    new InetSocketAddress("localhost", 11211));
		c.set("someKey", 3600, "abcd");
		// Retrieve a value (synchronously).
		Object myObject=c.get("someKey");
		System.err.println(myObject);
	}
}
