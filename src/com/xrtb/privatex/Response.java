package com.xrtb.privatex;

/**
 * A response record to win notification and bid requests.
 * @author Ben M. Faul
 *
 */
public class Response {
	/** Id of who this response is from */
	public String from;
	/** The id of who this is going to */
	public String id;
	/** html assocated with this response */
	public String html;
	/** price associated with this response */
	public double price;
	
	/**
	 * Default empty constructor
	 */
	public Response() {
		
	}
	
	/**
	 * A pretty print version of this response
	 * @return String. A string representation of this responae.
	 */
	public String toString() {
		String s = "Response, \n" +
					"\tFrom: " + from + "\n"+
					"\tid: " + id + "\n" +
					"\thtml: " + html + "\n" +
					"\tprice: " + price;
		return s;
	}
}
