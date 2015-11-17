/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.mockito.test;

import static com.github.dreamhead.moco.Moco.*;

import java.io.IOException;

import com.github.dreamhead.moco.Runnable;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.testng.annotations.Test;

import com.github.dreamhead.moco.HttpServer;
/**
 * <P>TODO</P>
 * @author lianrao
 */
public class MocoTest {

	
	@Test
	public void test() throws Exception{
		
		HttpServer server = httpserver(12306);
	    server.response("foo");

	    System.out.println("MocoTest.test()");
	    running(server, new Runnable() {
	        public void run() {
	            Content content = null;
				try {
					content = Request.Get("http://localhost:12306").execute().returnContent();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	            assertThat(content.asString(), is("foo"));
	        }
	    });
	}
}
