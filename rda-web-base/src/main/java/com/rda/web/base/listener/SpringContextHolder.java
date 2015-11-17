package com.rda.web.base.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class SpringContextHolder
        implements ServletContextListener {
    private static transient WebApplicationContext springContext;

    public static ApplicationContext getApplicationContext() {
        return springContext;
    }

    public void contextDestroyed(ServletContextEvent sce) {
        springContext = null;
    }

    public void contextInitialized(ServletContextEvent event) {
        springContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
    }
}

