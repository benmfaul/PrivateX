package com.xrtb.privatex;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;

import com.xrtb.bidder.Controller;
import com.xrtb.bidder.MimeTypes;
import com.xrtb.bidder.RTBServer;
import com.xrtb.bidder.WebCampaign;
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
		Database.log(2,"Exchange/run","Starting on port " + db.port);
		try {
			ExchangeHandler handler = new ExchangeHandler();
			SessionHandler sh = new SessionHandler(); // org.eclipse.jetty.server.session.SessionHandler
			sh.setHandler(handler);
			server.setHandler(sh); // set session handle
			db.log(2, "initialization",
					("System start on port: " + db.port));
			db.log(2, "initialization",
					("System start with log level: " + db.logLevel));
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
@MultipartConfig
class ExchangeHandler extends AbstractHandler {
	
	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(
			System.getProperty("java.io.tmpdir"));
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		String ipAddress = getIpAddress(request);
		String type = null;
		String json = null;

		int code = 200;
		String html = "";

		InputStream body = request.getInputStream();
		baseRequest.setHandled(true);

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		Database.log(5,"ExchageHandler/handle",target);
		
		try {
			if (target.contains("auction")) {
				html = new AuctionRequest(body, ipAddress).process();
				if (html == null || html.length() == 0)
					code = 204;
			} else {
				//target = target = target.replaceAll("xrtb/simulator/", "");
				int x = target.lastIndexOf(".");
				if (x >= 0) {
					type = target.substring(x);
				}
				if (type != null && type.contains("multipart/form-data")) {
					try {
						json = WebCampaign.getInstance().multiPart(baseRequest,
								request, MULTI_PART_CONFIG);
						response.setStatus(HttpServletResponse.SC_OK);
					} catch (Exception err) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						Controller.getInstance().sendLog(2, "Handler:handle",
								"Bad non-bid transaction on multiform reqeues");
					}
					baseRequest.setHandled(true);
					response.getWriter().println(json);
					return;
				}

				if (target.contains("favicon")) {
					response.setStatus(HttpServletResponse.SC_OK);
					baseRequest.setHandled(true);
					response.getWriter().println("");
					return;
				}
				if (type != null) {
					type = type.toLowerCase().substring(1);
					type = MimeTypes.substitute(type);
					response.setContentType(type);
					File f = new File("./www/" + target);
					if (f.exists() == false) {
						f = new File("./web/" + target);
						if (f.exists() == false) {
							f = new File(target);
							if (f.exists() == false) {
								f = new File("." + target);
								if (f.exists() == false) {
									response.setStatus(HttpServletResponse.SC_NOT_FOUND);
									baseRequest.setHandled(true);
									return;
								}
							}
						}
					}
						FileInputStream fis = new FileInputStream(f);
						OutputStream out = response.getOutputStream();

						// write to out output stream
						while (true) {
							int bytedata = fis.read();

							if (bytedata == -1) {
								break;
							}

							try {
								out.write(bytedata);
							} catch (Exception error) {
								break; // screw it, pray that it worked....
							}
						}

						// flush and close streams.....
						fis.close();
						try {
							out.close();
						} catch (Exception error) {

						}
						return;

					}

				/**
				 * Ok, we don't have a .type on the file, so we are assuming .html
				 */
				target = "www" + target;


				String page = Charset
						.defaultCharset()
						.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
								.get(target)))).toString();

				response.setContentType("text/html");
				response.setStatus(HttpServletResponse.SC_OK);
				baseRequest.setHandled(true);
				response.getWriter().println(page);
				RTBServer.concurrentConnections--;

				////////////////////////
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
