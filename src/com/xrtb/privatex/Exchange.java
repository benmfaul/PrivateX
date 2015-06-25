package com.xrtb.privatex;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.xrtb.bidder.RTBServer;
import com.xrtb.privatex.cfg.Database;

/**
 * Creates the HTTP handler for the advertising exchange. Receives ajax calls
 * for auctioning web page real estate through RTB.
 * 
 * @author Ben M. Faul.
 *
 */

public class Exchange implements Runnable {
	Thread me;
	int port = 8080;
	Database db;

	/**
	 * Creates the default exchange
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String configFile = "config.json";
		if (args.length > 0)
			configFile = args[0];
		new Exchange(configFile);
	}

	/**
	 * Creates the exchange and starts it. The Jetty side receives ajax calls
	 * for listings of web page space for sale. On the other side is an
	 * interface to RTB exchanges through an auction process.
	 * 
	 * @param redis
	 *            String. The "host:port" configuration for Redis.
	 */
	public Exchange(String configFile) throws Exception {
		db = new Database(configFile);
		me = new Thread(this);
		me.start();
	}

	/**
	 * Starts the JETTY server
	 */
	public void run() {
		Server server = new Server(db.port);
		server.setHandler(new ExchangeHandler());
		Database.log(2,"Exchange/run","Starting on port " + port);
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
 * The class that handles HTTP 'web space for sale' calls. An RTB auction is
 * created and if there is a buyer, then the HTML for that part of the web page
 * is returned.
 * 
 * @author Ben M. Faul
 *
 */
class ExchangeHandler extends AbstractHandler {

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

		Database.log(5,"ExchageHandler/hande",target);
		
		try {
			if (target.contains("auction")) {
				html = new AuctionRequest(body, ipAddress).process();
				if (html == null || html.length() == 0)
					code = 204;
			} else {
				if (target.contains("favicon"))
					html = "";
				else {
					if (target.toUpperCase().endsWith(".GIF")
							|| target.toUpperCase().endsWith(".PNG")
							|| target.toUpperCase().endsWith(".JPG")) {

						String type = target.substring(target.indexOf("."));
						type = type.toLowerCase().substring(1);

						response.setContentType("image/" + type);
						File f = new File("." + target);
						if (f.exists() == false) {
							int inx = target.indexOf("web");
							target = target.substring(inx);
							f = new File(target);
						}
						BufferedImage bi = ImageIO.read(f);
						OutputStream out = response.getOutputStream();
						ImageIO.write(bi, type, out);
						out.close();
						RTBServer.concurrentConnections--;
						return;
					} else {

						/**
						 * Handle in case NGINX is not being used (stand-alone testing)
						 */
						
						target = "www" + target;

						html = Charset
								.defaultCharset()
								.decode(ByteBuffer.wrap(Files
										.readAllBytes(Paths.get(target))))
								.toString();

						if (target.endsWith(".js") || target.endsWith(".JS"))
							response.setContentType("text/javascript");
						else
							response.setContentType("text/html");
						code = HttpServletResponse.SC_OK;
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		baseRequest.setHandled(true);
		response.getWriter().println(html);
		response.setStatus(code);
	}

	public String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

}
