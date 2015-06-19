package com.xrtb.privatex;

import com.xrtb.pojo.BidResponse;

/**
 * A class used to transmit bid requests and win notifications to a publisher object. This class
 * is used by the Auction class.
 * @author Ben M. Faul
 *
 */

public class Request {
	public static final int BIDREQUEST = 0;
	public static final int WINNOTIFY = 1;
	public int type;
	String ipAddr;
	String uuid;
	Campaign campaign;
	String ua;
	LatLong loc;
	String from;
	String id;
	double cost;
	
	/**
	 * Empty constructor for JSON
	 */
	public Request() {
		
	}
	
	/**
	 * Create a win notify using the bid request number and the cost,
	 * @param uuid String. The bid request id.
	 * @param cost double. The cost of the impression.
	 */
	public Request(String uuid, double cost) {
		this.uuid = uuid;
		this.cost = cost;
		this.type = WINNOTIFY;
	}
	
	/**
	 * Create a bid request to subscriber RTB bidders.
	 * @param uuid String. The bid request id.
	 * @param campaign Campaign. The campaign to use in creating the request.
	 * @param ua String. The web site user agent.
	 * @param loc LatLong. The web user's location.
	 */
	public Request(String uuid, Campaign campaign, String ua, LatLong loc, String ipAddr) {
		this.uuid = uuid;
		this.campaign = campaign;
		this.ua = ua;
		this.loc = loc;
		this.ipAddr = ipAddr;
		this.type = BIDREQUEST;
	}
}
