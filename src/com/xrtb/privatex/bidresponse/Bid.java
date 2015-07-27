package com.xrtb.privatex.bidresponse;

/**
 * The minimum bid object, that PrivateX is expecting.
 * @author Ben M. Faul
 *
 */
public class Bid {
	/** Impression id */
	public String impid;
	/** Bid id */
	public String id;
	/** Price of bid */
	public double price;
	/** Advertiser id */
	public String adid;
	/** The win URL */
	public String nurl;
	public String cid;
	public String crid;
	/** The image url */
	public String iurl;
	/** The advertiser's domain */
	public String adomain;
	/** The adm field, of the XML or html. will be encoded */
	public String adm;	
	
	public Bid() {
		
	}
}
