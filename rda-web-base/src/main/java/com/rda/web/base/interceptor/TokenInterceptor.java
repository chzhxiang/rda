package com.rda.web.base.interceptor;

import com.rda.web.base.token.TokenHelper;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenInterceptor extends HandlerInterceptorAdapter
{
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    throws Exception
  {
    if (("POST".equals(request.getMethod())) && (!TokenHelper.validToken(request))) {
      return false;
    }
    return true;
  }
}

