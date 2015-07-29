package com.xrtb.privatex.bidrequest;

/**
 * The User object.
 * @author Ben M. Faul
 *
 */
public class User {
	public String id;
	public int yob; 
	public String gender;
	public String keywords;
	public UserGeo geo = new UserGeo(); 
	public Ext ext = new Ext();   // add user extensions here, like marital status, kids, income, dma, etc.
    
    public User() {
    	
    }
}
