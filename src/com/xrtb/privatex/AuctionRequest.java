package com.xrtb.privatex;

import java.io.InputStream;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

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
	
	/**
	 * Creates an auction request in the thread of the Jetty handling this HTTP request.
	 * @param in InputStream. This is the body of the HTTP post with the ad request.
	 * @throws Exception on JSON errors.
	 */
	public AuctionRequest(InputStream in, String ipAddr) throws Exception {
		byte [] bytes = new byte[4096];
		
		int rc = in.read(bytes);
		String x = new String(bytes,0,rc);
		rootNode = mapper.readTree(x);
		
		ua = rootNode.path("ua").getTextValue();
		accountNumber = rootNode.path("accountNumber").getTextValue();
		campaignName = rootNode.path("campaign").getTextValue();
		JsonNode loc = rootNode.path("location");
		if (loc != null) {
			double lat = loc.path("latitude").getDoubleValue();
			double lon = loc.path("longitude").getDoubleValue();
			location = new LatLong(lat,lon);
		}
		
		Publisher pub = Database.publishers.get(accountNumber);				/** Get the campaign id */
		if (pub == null)
			return;
		Campaign campaign = pub.campaigns.get(campaignName);
		if (campaign == null) {
			return;
		}
		auction = new Auction(campaign, ua,  location, ipAddr);
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
		System.out.println("HTML: " + html);
		return html;
	}
}
