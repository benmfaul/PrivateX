package com.xrtb.privatex;

/**
 * A response record to win notification and bid requests.
 * @author Ben M. Faul
 *
 */
public class Response {
	public String from;
	public String id;
	public String html;
	public double price;
	
	public Response() {
		
	}
	
	public String toString() {
		String s = "Response, \n" +
					"\tFrom: " + from + "\n"+
					"\tid: " + id + "\n" +
					"\thtml: " + html + "\n" +
					"\tprice: " + price;
		return s;
	}
}
