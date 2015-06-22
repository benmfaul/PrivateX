package com.xrtb.privatex;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.xrtb.common.Node;
import com.xrtb.privatex.bidrequest.Impression;
import com.xrtb.privatex.bidrequest.PvtBidRequest;

public class Campaign {
	 Double price;
     String identifier;
    long served;
    long requested;
	String page;
	List<String> attributes;
	transient String attributesAsString;
	
	public Campaign() {
		
	}
	
	public String getAttributesAsString() throws Exception {
		if (attributesAsString == null) {
			attributesAsString = "";
			for (String s : attributes) {
				attributesAsString += s + "\n";
			}	
		}
		return attributesAsString;
	}
	
	
}
