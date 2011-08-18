package com.infy.icode.downloader.server;

import java.util.Properties;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.infy.icode.downloader.client.DownloadService;
import com.infy.icode.downloader.server.eucalyptus.EucalyptusConnector;
import  com.xerox.amazonws.ec2.ReservationDescription.Instance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DownloadServiceImpl extends RemoteServiceServlet implements
		DownloadService {
	public static String PATH = "";
	
	private Instance instance =null;
	
	public DownloadServiceImpl() {
		super();
		
	}
	public String download(String input) throws IllegalArgumentException {
		PATH = getServletContext().getRealPath("");
		
		System.out.println("Servlet context path "+getServletContext().getContextPath());
		String downloadURL = "";
		try {
			Properties prop = (Properties)getServletContext().getAttribute("properties");
			EucalyptusConnector connector = new EucalyptusConnector("euca",prop);
			
			downloadURL = connector.start(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return downloadURL;
	}
}
