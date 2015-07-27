package com.xrtb.privatex.cfg;

import java.util.List;

/**
 * The Top level object of the configuration of the exchange 
 * @author Ben M. Faul
 *
 */

public class Config {
	/** The configuration instance name */
    public String instance = "instance-default";
    /** The port the exchange listens to, 9090 by default */
    public int port = 9090;
    /** The application object */
    public App app = new App();
    /** The web object, unused */
    public List<Web> web;
    
    /**
     * Default constructor 
     */
	public Config() {
		
	}
}
