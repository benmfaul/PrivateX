package com.xrtb.privatex.bidrequest;

public class User {
	public String id;
	public int yob; 
	public String gender;
	public String keywords;
	public UserGeo geo = new UserGeo(); 
	public Ext ext;   // add user extensions here, like marital status, kids, income, dma, etc.
    
    public User() {
    	
    }
}
