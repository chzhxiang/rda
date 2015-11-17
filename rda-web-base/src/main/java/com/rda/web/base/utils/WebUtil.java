package com.rda.web.base.utils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 与web相关的工具类
 * 
 * @author lianrao
 * 
 */
public abstract class WebUtil {

	/**
	 * 判断当前请求是否是json请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isJsonRequest(ServletRequest request) {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			String requestURI = req.getRequestURI();
			String accept = req.getHeader("accept");
			String xReq = req.getHeader("X-Requested-With");
			if (((accept == null || accept.indexOf("application/json") <= -1) && (xReq == null || xReq
					.indexOf("XMLHttpRequest") <= -1))
					&& (!requestURI.endsWith(".json"))) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 将异常信息以json格式返回到response
	 * 
	 * @param res
	 * @param msg
	 */
	public static void sendExceptionJsonRespone(ServletResponse response, int status,
			String msg) {
		if (response instanceof HttpServletResponse) {
			HttpServletResponse res = (HttpServletResponse) response;
			res.setStatus(status);
			res.setContentType("application/json");
			String exInfo = "{\"msg\":\"" + msg + "\"}";

			try {
				PrintWriter ex = res.getWriter();
				ex.print(exInfo);
				ex.flush();
			} catch (IOException var9) {
				res.addHeader("error_msg", exInfo);
			}
		} else {
			throw new UnsupportedOperationException("不支持此response");
		}

	}
}
