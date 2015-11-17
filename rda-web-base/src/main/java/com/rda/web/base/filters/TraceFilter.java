package com.rda.web.base.filters;

import com.rda.core.support.ThreadContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * logback mdc trace filter Created by lianrao on 2014/11/28.
 * 在需要的trace日志时打印详细日志.
 */
public class TraceFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest req = request;
		String parameter = req.getParameter("trace");
		if (StringUtils.isNotEmpty(parameter)) {
			ThreadContext.put("trace", "true");
		}
		try {
			filterChain.doFilter(request, response);
		} finally {
			ThreadContext.remove("trace");
		}

	}

}
