/*******************************************************************************
 * rda
 ******************************************************************************/
package com.lianrao.test.misc;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


/**
 * <P>TODO</P>
 * @author lianrao
 */
public class ClassLoaderTest {
	public static void main1(String[] args) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, MalformedURLException {
		String url = "file://" + System.getProperty("user.dir").replaceAll("\\\\", "/") + "/target/test-classes/";
		System.out.println(url);
		ClassLoader c1 = new URLClassLoader(new URL[] { new URL(url) }, null);
		System.out.println("c1的父类加载器: " + c1.getParent());
		System.out.println("SystemClassLoader: " + ClassLoader.getSystemClassLoader());
		Class<?> class1 = c1.loadClass("com.lianrao.test.misc.Person");
		Object o = class1.newInstance();
		System.out.println("Person:" + o);
		System.out.println("Test的定义类装载器: " + ClassLoaderTest.class.getClassLoader());
		System.out.println("Test中直接使用Person使用的ClassLoader: " + Person.class.getClassLoader());
		System.out.println("自定义装载器装载Person的定义类加载器: " + o.getClass().getClassLoader());
		Person p = (Person) o;
	}

	public static void main2(String[] args) throws ClassNotFoundException, MalformedURLException,
			InstantiationException, IllegalAccessException {
		String url = "file://" + System.getProperty("user.dir").replaceAll("\\\\", "/") + "/target/test-classes/";
		System.out.println(url);
		ClassLoader c1 = new URLClassLoader(new URL[] { new URL(url) }, null);
		System.out.println("c1的父类加载器: " + c1.getParent());
		System.out.println("SystemClassLoader: " + ClassLoader.getSystemClassLoader());
		Class<?> class1 = c1.loadClass("java.lang.String");
		Object o = class1.newInstance();
		System.out.println("Person:" + o);
		System.out.println("Test的定义类装载器: " + ClassLoaderTest.class.getClassLoader());
		System.out.println("Test中直接使用Person使用的ClassLoader: " + Person.class.getClassLoader());
		System.out.println("自定义装载器装载Person的定义类加载器: " + o.getClass().getClassLoader());

		String p = (String) o;
	}

	public static void main(String[] args) throws Exception {
		String url = "file://" + System.getProperty("user.dir").replaceAll("\\\\", "/") + "/target/test-classes/";
		System.out.println(url);
		ClassLoader c1 = new URLClassLoader(new URL[] { new URL(url) });
		System.out.println("c1的父类加载器: " + c1.getParent());
		System.out.println("SystemClassLoader: " + ClassLoader.getSystemClassLoader());
		Class<?> class1 = c1.loadClass("com.lianrao.test.misc.Person");
		Object o = class1.newInstance();
		System.out.println("Person:" + o);
		System.out.println("Test的定义类装载器: " + ClassLoaderTest.class.getClassLoader());
		System.out.println("Test中直接使用Person使用的ClassLoader: " + Person.class.getClassLoader());
		System.out.println("自定义装载器装载Person的定义类加载器: " + o.getClass().getClassLoader());

		Person p = (Person) o;
	}
}
