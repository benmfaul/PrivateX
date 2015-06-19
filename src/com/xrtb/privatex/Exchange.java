package com.xrtb.privatex;

import java.io.IOException;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Creates the HTTP handler for the advertising exchange. Receives ajax calls for auctioning web page
 * real estate through RTB.
 * @author Ben M. Faul.
 *
 */

public class Exchange implements Runnable {
	Thread me;
	int port = 8080;
	Database db;
	
	/**
	 * Creates the default exchange
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String redis = "redis:6379";
		if (args.length > 0)
			redis = args[0];
		new Exchange(redis);
	}
	
	/**
	 * Creates the exchange and starts it. The Jetty side receives ajax calls for listings
	 * of web page space for sale. On the other side is an interface to RTB exchanges through an
	 * auction process. 
	 * @param redis String. The "host:port" configuration for Redis.
	 */
	public Exchange(String redis) {
		db = new Database(redis);
		me = new Thread(this);
		me.start();
	}
	
	/**
	 * Starts the JETTY server
	 */
	public void run() {
		Server server = new Server(port);
		server.setHandler(new ExchangeHandler());
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/**
 * The class that handles HTTP 'web space for sale' calls. An RTB auction is created and if there
 * is a buyer, then the HTML for that part of the web page is returned.
 * @author Ben M. Faul
 *
 */
class ExchangeHandler extends AbstractHandler  {

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		String ipAddress = getIpAddress(request);
		
		int code = 200;
		String html = "";
		
		InputStream body = request.getInputStream();		
		baseRequest.setHandled(true);
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		
		try {
			if (target.contains("auction")) {
				html = new AuctionRequest(body,ipAddress).process();
				if (html == null || html.length() == 0)
					code = 204;
			}
			else {
				if (target.contains("favicon"))
					html = "";
				else
					html = Charset
						.defaultCharset()
						.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
						.get("web/pvx.html")))).toString();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.getWriter().println(html);
		response.setStatus(code);
	}
	
	public String getIpAddress(HttpServletRequest request) {      
		   String ip = request.getHeader("x-forwarded-for");      
		   if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
		       ip = request.getHeader("Proxy-Client-IP");      
		   }      
		   if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
		       ip = request.getHeader("WL-Proxy-Client-IP");      
		   }      
		   if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
		       ip = request.getRemoteAddr();      
		   }      
		   return ip;      
		}
	
}
