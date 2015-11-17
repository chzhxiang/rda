/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.web.base.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <P>
 * 在Spring MVC当使用RedirectView或者"redirect:"前缀来做重定向时，Spring MVC最后会调用：<br/>
 * response.sendRedirect(response.encodeRedirectURL(url));<br/>
 * 对于IE来说，打开一个新的浏览器窗口，第一次访问服务器时，encodeRedirectURL()会在url后面附加上一段jsessionid，<br/>
 * 如果初始的url为"http://www.sina.com.cn"，<br/>
 * 最终得到的url为"http://www.sina.com.cn;jsessionid=2jcligmgi6fh"。<br/>
 * 这是典型的Java做事的方式，其他语言的服务器端平台并不会这样做。<br/>
 * 这个jsessionid很多时候会引起严重的问题.<br/>
 * 例如，如果你使用上述带有jsessionid的url直接访问新浪的网站，IE会向你报告：找不到服务器。<br/>
 * <br/>
 * 此filter重写redirectUrl防止将 jsessionid rewrite到url中.<br/>
 * 也可以使用org.tuckey.urlrewritefilter来做此filter:{@link http://www.tuckey.org/urlrewrite/}<br/>
 * <br/>
 * 注意：当使用shiro时若其sessionManager为native模式则此filter不会起作用，<br/>
 * 因shiro在native模式下使用其自身的org.apache.shiro.web.servlet.ShiroHttpServletResponse类<br/>
 * 来包装response,在其sendRedirectUrl时会使用url rewrite机制来写sessionid到url中.<br/>
 * </P>
 * @see  {@link https://randomcoder.org/articles/jsessionid-considered-harmful}
 * @see  {@link http://stackoverflow.com/questions/6808331/why-is-jsessionid-appearing-in-wicket-urls-when-cookies-are-enabled}
 * @see  {@link http://blog.csdn.net/seakingwy/article/details/1933687}
 * @see  org.apache.shiro.web.servlet.ShiroHttpServletResponse
 * @author lianrao
 */
public class DisableUrlSessionFilter implements Filter {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		log.trace("DisableUrlSessionFilter called.");
		//if not a http request , pass by
		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		/**
		 * Invalidate any sessions that are backed by a URL-encoded session id.
		 * This prevents an attacker from generating a valid link.
		 * Just because we won't be generating session-encoded links doesn't mean 
		 * someone else won't try:
		*/
		if (httpRequest.isRequestedSessionIdFromURL()) {
			HttpSession session = httpRequest.getSession();
			if (session != null){
				log.trace("invalidate the session that the url contains the jessionid.");
				session.invalidate();
			}
		}
		
		
		/**
		 * To disable the default URL-encoding functionality,
		 * we need to wrap the existing HttpServletResponse object. 
		 * Fortunately, the Servlet API provides just such a class ready-made in HttpServletResponseWrapper. 
		 * We could subclass it to provide our own handling, 
		 * but this is a trivial enough change that an anonymous inner class will do nicely:
		 */
		HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse) {
			public String encodeRedirectUrl(String url) {
				return url;
			}

			public String encodeRedirectURL(String url) {
				return url;
			}

			public String encodeUrl(String url) {
				return url;
			}

			public String encodeURL(String url) {
				return url;
			}
		};
		
		chain.doFilter(request, wrappedResponse);
	}

	@Override
	public void destroy() {
	}
}
