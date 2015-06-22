package com.xrtb.privatex.bidrequest;

import java.util.ArrayList;
import java.util.List;

public class Site {
	public String id;
	public String name;
	public String domain;
	public List<String> cat = new ArrayList();
	public String keywords;
	public String page; 
	public String ref;
	public String search;
	public Publisher publisher = new Publisher();
	public Ext ext = new Ext();

    public Site() {
    	
    }
}
