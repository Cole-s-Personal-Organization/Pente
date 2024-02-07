package com.mycompany.app.WebServer;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        // unpack connection information
        // String url = ctx.getInitParameter("DBURL");
    	// String u = ctx.getInitParameter("DBUSER");
    	// String p = ctx.getInitParameter("DBPWD");

        RedisConnectionManager cacheManager = new RedisConnectionManager(null, null, null);
        context.setAttribute("cacheManager", cacheManager);
        System.out.println("Redis cache connection initialized for Application");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
    	RedisConnectionManager cacheManager = (RedisConnectionManager) context.getAttribute("cacheManager");
    	cacheManager.destroyPool();
    	System.out.println("Database connection closed for Application.");
    }
}
