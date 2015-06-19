package com.xrtb.privatex.br;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PvtBidRequest {
	public String id;
	public int at = 2;
	public List <Impression> imp = new ArrayList();
	public Site site = new Site();
	public Device device = new Device();
	public User user = new User();
	
	
	public static void main(String [] args) throws Exception {
		PvtBidRequest r = new PvtBidRequest();
		String s = r.toJson();
		System.out.println(s);
	}
	public PvtBidRequest() {
		
	}
    
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}
}
