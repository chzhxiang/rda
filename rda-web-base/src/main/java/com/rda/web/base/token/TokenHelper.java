package com.rda.web.base.token;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class TokenHelper {
    private static final Logger logger = LoggerFactory.getLogger(TokenHelper.class);
    public static final String TOKEN_NAME = "token";

    public static Token genToken(HttpServletRequest request) {
        Token token = Token.build().prefixKey("tk-").expiredAfterMins(10);
        HttpSession session = request.getSession();
        session.setAttribute(token.getKey(), token);
        logger.debug("请求[{}:{}]产生的Token：{}", new Object[]{uri(request), session.getId(), token});
        return token;
    }

    private static String getTokenKey(HttpServletRequest request) {
        Map params = request.getParameterMap();
        if (!params.containsKey(TOKEN_NAME)) {
            return null;
        }
        String[] tokenNames = (String[]) params.get(TOKEN_NAME);
        if ((tokenNames == null) || (tokenNames.length < 1)) {
            return null;
        }
        String tokenString = tokenNames[0];
        return tokenString;
    }

    public static boolean validToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String tokenKey = getTokenKey(request);
        logger.debug("请求[{}:{}]提交的token key：{}", new Object[]{uri(request), sessionId, tokenKey});
        if (StringUtils.isBlank(tokenKey)) {
            return false;
        }

        synchronized (sessionId.intern()) {
            Token token = (Token) session.getAttribute(tokenKey);
            logger.debug("请求[{}:{}]缓存的token为:{}", new Object[]{uri(request), sessionId, token});
            if (token == null || !token.isValid()) {
                logger.debug("请求[{}:{}]提交的token验证失败", new Object[]{uri(request), sessionId});
                session.removeAttribute(tokenKey);
                return false;
            }
            session.removeAttribute(tokenKey);
            logger.debug("请求[{}:{}]提交的token验证通过", new Object[]{uri(request), sessionId});
        }
        return true;
    }

    private static String uri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}

