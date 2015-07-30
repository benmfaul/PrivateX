package com.xrtb.privatex;

/**
 * A Class that encapsulates the JSON from the PrivateX API
 * @author Ben M. Faul
 *
 */
public class API {
	/** Publisher account number */
	public String accountNumber;
	/** The campaign identifier */
	public String campaign;
	/** The user agent */
    public String ua;
    /** The latitude of the user */
    public  double lat;
    /** The longitude of the user */
    public double lon;
    /** The user's platform */
    public String platform;
    /** The user's connection type */
    public String connectionType;
    /** The user's downlink */
    public double maxDownLink;
    
    /**
     * The default constructor
     */
    public API() {
    	
    }
}
