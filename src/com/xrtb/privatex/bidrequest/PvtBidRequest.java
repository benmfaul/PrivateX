package com.xrtb.privatex.bidrequest;

import java.util.ArrayList;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A class that constructs a minimum bid request.
 * @author Ben M. Faul
 *
 */

public class PvtBidRequest {
	public String id;
	public int at = 2;
	public List <Impression> imp = new ArrayList();
	public Site site = new Site();
	public Device device = new Device();
	public User user = new User();
	
	
	/**
	 * A test for making a bid request object.
	 * @param args String[]. Unused.
	 * @throws Exception on JSON errors.
	 */
	public static void main(String [] args) throws Exception {
		PvtBidRequest r = new PvtBidRequest();
		String s = r.toJson();
		System.out.println(s);
	}
	
	/**
	 * Empty constructor for JSON
	 */
	public PvtBidRequest() {
		
	}
    
	/**
	 * Return a string represntation (fast) JSON of this bid request.
	 * @return String. The JSON string representing this object
	 * @throws JsonProcessingException on JSON errors.
	 */
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	/**
	 * Return a pretty print JSON representation of this object (slow).
	 */
	public String toString() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		return gson.toJson(this);
	}
}
