/*******************************************************************************
 * Project Key : et-win
 * author: lianrao
 * 2013-12-10 下午10:45:02
 ******************************************************************************/
package com.lianrao.test.misc;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class Jishui {
	
	public static void main(String[] args){
		
	}
	
	/**
	 * 
	 * <p>个人缴纳五险一金金额</p>
	 * @param salary
	 * @return
	 * @author lianrao
	 */
	public static double my_wxyj(double faxAmt){
		
	}
	
	/**
	 * 
	 * <p>公司缴纳五险一金金额</p>
	 * @param salary
	 * @return
	 * @author lianrao
	 */
	public static double ent_wxyj(double faxAmt){
		
	}
	
	/**
	 * 
	 * <p>个人缴纳的住房公积金</p>
	 * @param salary
	 * @return
	 * @author lianrao
	 */
	public static double gjj(double faxAmt){
		return faxAmt*0.05;
	}
	
	
	/**
	 * 
	 * <p>报销费用</p>
	 * @param salary
	 * @return
	 * @author lianrao
	 */
	public static double bx_fee(double bxAmount){
		return bxAmount*0.02;
	}

}
