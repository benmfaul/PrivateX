package com.xrtb.privatex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.redisson.core.MessageListener;
import org.redisson.core.RBucket;
import org.redisson.core.RCountDownLatch;
import org.redisson.core.RList;
import org.redisson.core.RTopic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.xrtb.common.HttpPostGet;
import com.xrtb.privatex.bidresponse.Bid;
import com.xrtb.privatex.bidresponse.Body;
import com.xrtb.privatex.cfg.Database;

/**
 * A class that handles RTB bid requests to subscribing RTB DSPs.
 * @author Ben M. Faul
 *
 */
public class Subscriber implements Runnable {
	/** Redisson topic for bid requests */
	transient RTopic bidRequests;
	/** Redisson topic for win responses */
	transient RTopic winResponses;
	/** My thread */
	transient Thread me;
	/** The connection object to the RTB bidder */
	transient HttpPostGet connection;
	/** The JSON mapping object  for this subscriber */
	transient ObjectMapper mapper = new ObjectMapper();
	
	/** The URL of this subscriber's RTB */
	String url;

	/** String the account number of this RTB subscriber */
	String accountNumber;
	/** The name of this subscriber */
    String name;
    /** The address of this RTB subscriber */
    String address;
    /** The telephone number of this RTB subscriber */
    String telephoneNumber;
    /** The max connextions the RTB subscriber wants */
    int maxConnections;
    /** The max rate in bid requests per second */
    int maxRate = 0;
    /** The number of requests successfully sent */
    int sentOk = 0;
    /** The number of errored requests */
    int errored = 0;
    /** The number of times the RTB bidder bid */
    int bid = 0;
    /** The number of thimes the RTB bidder did not bid */
    int noBid = 0;
    
    /** handles win notifications, returns ADM */
    List<Response> winners = new ArrayList<Response>();           
    /** handles bid requests */
    List<Request> requests = new ArrayList<Request>();		

    /**
     * Empty constructor for use by JSON
     */
	public Subscriber() {
		
	}
	
	/**
	 * Return a subscriber from a Map representing the subscriber.
	 * @param m Map. A map object that can be converted to a subscriber.
	 * @return Subscriber. A subscriber object constructed from the map.
	 */
	public static Subscriber instance(Map m) {
		Gson gson = new Gson();
		String s = gson.toJson(m);
		Subscriber sub = gson.fromJson(s, Subscriber.class);
		return sub;
	}
	
	/**
	 * After loading in from Redisson, initialize the real-time parts.
	 */
	public void setup() {
		bidRequests = Database.redisson.getTopic("bidrequests");
		winResponses = Database.redisson.getTopic(accountNumber);
		connection = new HttpPostGet();
		me = new Thread(this);
		me.start();
	}

	/**
	 * This is the code that actually sends bid requests to the RTB subscriber 
	 */
	public void run() {
		bidRequests.addListener(new MessageListener<Request>() {

			/**
			 * Request to send bids
			 */
			@Override
			public void onMessage(String channel, Request r) {
				requests.add(r);			
			}
		});
		
		winResponses.addListener(new MessageListener<Response>() {

			/**
			 * Request to send bids
			 */
			@Override
			public void onMessage(String channel, Response r) {
				winners.add(r);				
			}
		});
		
		while(true) {
			try {
				Thread.sleep(1);
				if (winners.size() > 0) {
					Response r = winners.remove(0);
				    try {
						doAdm(r);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (requests.size() > 0) {
					Request r = requests.remove(0);
					doRequest(r);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	/**
	 * Process a Bid Request
	 * @param r Request. The parameters of the bid request.
	 */
	private void doRequest(Request r) {
		String requestId = r.uuid;
		RList list = (RList)Database.redisson.getList(requestId);
	
		
		String returns = "hello world";
		String s = r.br.toString();
		Database.log(5,"Subscriber:"+name+":doRequest:sending",url+" ### " + s);
		
		try {
			returns = connection.sendPost(url,r.br.toJson()); 
			sentOk++;
		} catch (Exception e) {
			Database.log(2,"Subscriber:"+name+":doRequest:response","Error receiving response, error is: " +e.toString());
			returns = null;
			this.errored++;
			return;
		}

		if (connection.getResponseCode() == 200) {     /** LET'S BID */
			Database.log(5,"Subscriber:"+name+":doRequest:response","Good bid received from Subscriber:" + name);
			Response response = new Response();
			response.html = returns;
			response.from = accountNumber;
			response.id = r.id;
			list.add(response);
			
			// store the adm record
			
		} else {
			returns = connection.getHeader("X-REASON");
			Database.log(3,"Subscriber:"+name+"doRequest:response","Error received from Subscriber:" + name + ", error:"+returns);
		}
	}
	
	/**
	 * Handles the win notification by retrieving the ADM from the winning bid.
	 * @param id String. The winning bid id.
	 */
	private void doAdm(Response r) throws Exception {
		RBucket<String> bucket = Database.redisson.getBucket(r.id);
		// Notify bidder of the win, retrieve the ADM, do the accounting
		
		Body body = mapper.readValue(r.html,Body.class);
		Bid bid = body.seatbid[0].bid[0];
		
		String adm = null;
		try {
			String nurl = bid.nurl;
			Database.log(5,"Subscriber:"+name+":doAdm","Notify the winning RTB: " + nurl);
			adm = connection.sendGet(nurl);
			Database.log(5,"Subscriber/doAdm:adm",adm);
		} catch(Exception error) {
			
		}
	
		bucket.set(adm);
		
		RCountDownLatch latch = Database.redisson.getCountDownLatch("latch:"+r.id);
		latch.countDown();
	}
}
