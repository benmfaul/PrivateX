package com.xrtb.privatex.cfg;

/**
 * The Redis configuration object
 * @author Ben M. Faul
 *
 */
public class Redis {
	/** The redis host, local by default */
	public String host = "localhost";
	/** The redis logging channel, xlog by default */
	public String logger = "xlog";
	/** The redis port, default is 6379 */
	public int port = 6379;
   
	/**
	 * Default constructor
	 */
	public Redis() {
		
	}
}
