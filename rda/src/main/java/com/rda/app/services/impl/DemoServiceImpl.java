/*******************************************************************************
 * rda
 ******************************************************************************/
package com.rda.app.services.impl;

import com.rda.app.services.DemoService;


/**
 * <P>TODO</P>
 * @author lianrao
 */
public class DemoServiceImpl implements DemoService {

	/* (non-Javadoc)
	 * @see com.rda.services.DemoService#sayHello(java.lang.String)
	 * @author lianrao
	 */
	public String sayHello(String name) {
		return "Hello " + name;
	}

}
