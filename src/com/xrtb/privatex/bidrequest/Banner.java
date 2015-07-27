package com.xrtb.privatex.bidrequest;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * RTB Banner ad
 * @author Ben M. Faul
 *
 */
public class Banner extends HashMap  {

	/** 
	 * Default constructor 
	 */
	public Banner() {
		setApi(0);
		setPos(0);
	}
	
	public void setWidth(int w) {
		put("w",w);
	}
	
	public void setHeight(int h) {
		put("h",h);
	}
	
	public void setPos(int pos) {
		put("pos",pos);
	}
	
	public void setApi(int api) {
		put("api",api);
	}
	
	public int getWidth() {
		return (Integer)get("w");
	}
	
	public int getHeight() {
		return (Integer)get("h");
	}
	public int getPos() {
		return (Integer)get("pos");
	}
	public int getApi() {
		return (Integer)get("api");
	}
}
