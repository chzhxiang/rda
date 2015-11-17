/*******************************************************************************
 * Project Key : et-win
 * author: lianrao
 * 2013-11-12 下午1:37:56
 ******************************************************************************/
package com.rda.monitor.test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class MyTest {

	public static void main(String[] args){
		test();
	}
	
	public static void test(){
		Path myPath = Paths.get("MyTest.class");
		System.err.println(myPath.toAbsolutePath());
	}
}
