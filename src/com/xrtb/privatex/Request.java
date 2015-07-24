package com.xrtb.privatex;

import com.xrtb.pojo.BidResponse;
import com.xrtb.privatex.bidrequest.PvtBidRequest;

/**
 * A class used to transmit bid requests and win notifications to a publisher object. This class
 * is used by the Auction class and is transmitted over REDIS.
 * @author Ben M. Faul
 *
 */

public class Request {
	/** Code for a bidrequest */
	public static final int BIDREQUEST = 0;
	/** code for a win notify */
	public static final int WINNOTIFY = 1;
	/** The request type */
	public int type;
	/** A bid request object that travels with this request */
	public PvtBidRequest br;
	/** A unique user id for this request */
	String uuid;
	/** The publisher id this request relates to */
	String id;
	/** The cost associated with this request */
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
	public Request(String uuid, PvtBidRequest br) {
		this.uuid = uuid;
		this.br = br;
		this.type = BIDREQUEST;
	}
}
