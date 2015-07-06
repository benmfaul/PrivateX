package com.xrtb.privatex.cfg;

import java.util.List;


public class Config {
    public String instance = "instance-default";
    public int port = 9090;
    public App app = new App();
    public List<Web> web;
    
	public Config() {
		
	}
}
