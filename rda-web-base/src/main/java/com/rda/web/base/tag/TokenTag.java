package com.rda.web.base.tag;

import com.rda.web.base.token.Token;
import com.rda.web.base.token.TokenHelper;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class TokenTag extends RequestContextAwareTag {
    private static final long serialVersionUID = 2422549476354099201L;

    protected int doStartTagInternal()
            throws Exception {
        ServletRequest request = this.pageContext.getRequest();
        if ((request instanceof HttpServletRequest)) {
            HttpServletRequest req = (HttpServletRequest) request;
            Token token = TokenHelper.genToken(req);
            String tokenStr = "<input type=\"hidden\" id=\"token\" name=\"token\" value=\"%s\" />\r\n";
            this.pageContext.getOut().print(String.format(tokenStr, new Object[]{token.getKey()}));
        } else {
            throw new ServletException("Only Http request supported.");
        }
        return 0;
    }
}

