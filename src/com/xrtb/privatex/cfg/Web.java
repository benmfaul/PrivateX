package com.xrtb.privatex.cfg;

import java.util.List;

/**
 * The web configuration object. Not used yet
 * @author  Ben M. Faul
 *
 */
public class Web {
	/** Web context names */
	public List<String> context;
	/** The container base address */
	public String base = ".";
	/** The class name of this web app */
	public String className;
	
	/**
	 * Default constructor 
	 */
	public Web() {
		
	}
	
}
