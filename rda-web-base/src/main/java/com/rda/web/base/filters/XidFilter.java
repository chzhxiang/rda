package com.rda.web.base.filters;

import com.rda.core.support.ThreadContext;
import com.rda.util.UUIDShort;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 此filter从request中获取xid值,若无则自动生成一个xid放入threadContext中;
 * Created by lianrao on 2015/7/8.
 */
public class XidFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String xid = request.getParameter("xid");
            if (StringUtils.isEmpty(xid)) {
                xid = UUIDShort.Generate();
            }
            ThreadContext.setXid(xid);
            filterChain.doFilter(request, response);
        }finally {
            ThreadContext.removeXid();
        }

    }

}
