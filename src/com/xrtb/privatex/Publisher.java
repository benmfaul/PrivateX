package com.xrtb.privatex;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Publisher {
	 String name;
	 String address; 
	 String telephoneNumber;
	 String id;
	 String domain = "unknown.com";
	 Map<String,Campaign> campaigns = new HashMap();
	 
	 public Publisher() {
		 
	 }
	 
	 public static Publisher instance(Map m) {
		Gson gson = new Gson();
		String str = gson.toJson(m);
		Publisher p =  gson.fromJson(str, Publisher.class);
		return p;
	 }
}
