/**
 * 
 */
package com.infy.icode.downloader.server;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Allahbaksh_Asadullah
 *
 */
public class DownloadListner implements ServletContextListener {

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent context) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent context) {
		Properties props = new Properties();
		String initParam = context.getServletContext().getInitParameter("type");
		try {
			props.load(getClass().getClassLoader().getResourceAsStream(initParam+".properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.getServletContext().setAttribute("prop" +
				"erties", props);
		// TODO Auto-generated method stub

	}

}
