package com.xrtb.privatex;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A class that defines a web publisher.
 * @author Ben M. Faul
 *
 */
public class Publisher {
	/** The name of the publisher */
	public  String name;
	 /** The address of the publisher */
	public String address; 
	 /** The telephone number of the publisher */
	public String telephoneNumber;
	 /** The id of the publisher */
	public String id;
	 /** The publisher domain name */
	public  String domain = "unknown.com";
	 /** A map of campaigns used by this publisher */
	public Map<String,Campaign> campaigns = new HashMap();
	 
	 /**
	  * Default constructor
	  */
	 public Publisher() {
		 
	 }
	 
	 /**
	  * Creates an instance of a publisher from a Map object
	  * @param m Map. A Map that can be converted to a Publisher class.
	  * @return Publisher. The map converted to a publisher
	  */
	 public static Publisher instance(Map m) {
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		String str = gson.toJson(m);
		Publisher p =  gson.fromJson(str, Publisher.class);
		return p;
	 }
}
