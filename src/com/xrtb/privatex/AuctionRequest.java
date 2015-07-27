package com.xrtb.privatex;

import java.io.InputStream;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xrtb.privatex.cfg.Database;

/**
 * Request an auction for a web page ad.
 * @author Ben M. Faul
 *
 */
public class AuctionRequest {
	JsonNode rootNode = null;
	ObjectMapper mapper = new ObjectMapper();
	String accountNumber;
	String ua;
	String campaignName;
	LatLong location;
	String html = "";
	Auction auction;
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	/**
	 * Creates an auction request in the thread of the Jetty handling this HTTP request.
	 * @param in InputStream. This is the body of the HTTP post with the ad request.
	 * @throws Exception on JSON errors.
	 */
	public AuctionRequest(InputStream in, String ipAddr) throws Exception {
		byte[] resultBuff = new byte[0];
	    byte[] buff = new byte[1024];
	    int k = -1;
	    while((k = in.read(buff, 0, buff.length)) > -1) {
	        byte[] tbuff = new byte[resultBuff.length + k]; // temp buffer size = bytes already read + bytes last read
	        System.arraycopy(resultBuff, 0, tbuff, 0, resultBuff.length); // copy previous bytes
	        System.arraycopy(buff, 0, tbuff, resultBuff.length, k);  // copy current lot
	        resultBuff = tbuff; // call the temp buffer as your result buff
	    }
	    String x = new String(resultBuff);
		
	    if (Database.logLevel >= 5) {
	    	Map<?, ?> m = gson.fromJson(x,Map.class);
	    	Database.log(5,"AUctionRequest/received message",gson.toJson(m));
	    }
		
		Command cmd = mapper.readValue(x,Command.class);
		
		Publisher pub = Database.publishers.get(cmd.accountNumber);				/** Get the campaign id */
		if (pub == null)
			return;
		Campaign campaign = pub.campaigns.get(cmd.campaign);
		if (campaign == null) {
			return;
		}
		auction = new Auction(cmd, pub, campaign, ipAddr);
	}
	
	
	/**
	 * Is the auction complete?
	 * @return boolean. Returns true if the auction completes.
	 */
	public boolean isDone() {
		if (auction == null)
			return false;
	
		return auction.isDone();
	}
	
	/**
	 * Process the auction. Waits until the auction finishes or times out.
	 * @return String. The HTML to return to the web user.
	 */
	public String process() {
		if (auction == null)
			return null;
		while(auction.isDone()==false);
		html = auction.process();
		Database.log(5,"AuctionRequest/process","Returning HTML " + html);
		return html;
	}
}
