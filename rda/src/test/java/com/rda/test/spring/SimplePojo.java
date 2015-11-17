/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.test.spring;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class SimplePojo implements Pojo{

	/* (non-Javadoc)
	 * @see com.rda.test.spring.Pojo#foo()
	 * @author lianrao
	 */
	@Override
	public void foo() {
		System.out.println("invoke foo.");
		this.bar();
		
	}

	/* (non-Javadoc)
	 * @see com.rda.test.spring.Pojo#bar()
	 * @author lianrao
	 */
	@Override
	public void bar() {
		System.out.println("invoke bar.");
	}
	

}
