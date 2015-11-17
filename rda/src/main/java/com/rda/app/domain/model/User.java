/*******************************************************************************
 * rda
 ******************************************************************************/
package com.rda.app.domain.model;

import javax.validation.constraints.Min;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class User {

	@Min(2)
	private int id;
	private String name;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
