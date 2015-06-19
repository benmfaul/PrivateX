package com.xrtb.privatex;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.xrtb.common.Node;
import com.xrtb.privatex.br.Impression;

public class Campaign {
	Double price;
    String identifier;
    long served;
    long requested;
	List<Node> attributes = new ArrayList();
	Impression impression = new Impression();
	List<String> cat = new ArrayList();
	String domain = "rtb4free.com";
	String keywords = "football,mixed martial arts";
	String ref = "referenceid";
	String page = "rtb4free.com/football-and-mma.html";
	
	public Campaign() {
		
	}
}
