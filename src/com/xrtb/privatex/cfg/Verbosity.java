package com.xrtb.privatex.cfg;

/**
 * The verbosity configuration object
 * @author Ben M. Faul
 *
 */
public class Verbosity {
	/** The logging level, 2 by default (1=fatal,2=error,3=warning,4=info,5=debug) */
	public int level = 2;
	/** Print on stdout if there is a no bid, true by default. Make false for operational */
	public boolean nobidReason = true;
	
	/**
	 * Default constructor
	 */
	public Verbosity() {
		
	}
		
}
