package com.xrtb.privatex.cfg;

import com.xrtb.privatex.CampaignDefine;

public class App {
	public int connections = 10;
	public int ttl = 300;
	public Verbosity verbosity = new Verbosity();
	public Redis redis = new Redis();
	public CampaignDefine[] campaigns;
	
	public App() {
		
	}
}
