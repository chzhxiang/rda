/*******************************************************************************
 * Project Key : et-win
 * author: lianrao
 * 2013-12-15 下午8:59:22
 ******************************************************************************/
package com.rda.test;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class Person {
	private String id;
	private String name;
	private int age;

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
}
