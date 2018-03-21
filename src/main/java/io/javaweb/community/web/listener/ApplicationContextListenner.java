package io.javaweb.community.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@WebListener
public class ApplicationContextListenner implements ServletContextListener {

	//private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextListenner.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		/**
		 * 
		 * 
		 * 
		 */
	}
}
