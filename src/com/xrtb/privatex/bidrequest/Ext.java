package com.xrtb.privatex.bidrequest;

import java.util.HashMap;

/**
 * The RTB Extension object. 
 * @author Ben M. Faul
 *
 */
public class Ext extends HashMap {
	
	/**
	 * Create an extension object. By default, we create an RTB4FREE object, which is a hashmap.
	 * For other extensions merely call the put method with your object.
	 */
	public Ext() {
		put("rtb4free",new HashMap());
	}
}
