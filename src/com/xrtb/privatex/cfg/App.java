package com.xrtb.privatex.cfg;

import com.xrtb.privatex.CampaignDefine;

/**
 * Configuration object for startup parameters. Used by jackson
 * @author Ben M. Fau
 *
 */
public class App {
	/** Max number of simultaneous connections */
	public int connections = 10;
	public int ttl = 300;
	/** The verbosity object */
	public Verbosity verbosity = new Verbosity();
	/** The redis object */
	public Redis redis = new Redis();
	/** The campaigns defined initially */
	public CampaignDefine[] campaigns;
	
	/**
	 * Default constructor
	 */
	public App() {
		
	}
}
