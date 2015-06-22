package com.xrtb.privatex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.redisson.core.MessageListener;
import org.redisson.core.RBucket;
import org.redisson.core.RCountDownLatch;
import org.redisson.core.RList;
import org.redisson.core.RTopic;

import com.google.gson.Gson;
import com.xrtb.common.HttpPostGet;
import com.xrtb.pojo.BidRequest;
import com.xrtb.privatex.bidrequest.PvtBidRequest;
import com.xrtb.privatex.bidresponse.Bid;
import com.xrtb.privatex.bidresponse.Body;

/**
 * A class that handles RTB bid requests to subscribing RTB DSPs.
 * @author Ben M. Faul
 *
 */
public class Subscriber implements Runnable {
	transient RTopic bidRequests;
	transient RTopic winResponses;
	transient Thread me;
	transient HttpPostGet connection;
	transient ObjectMapper mapper = new ObjectMapper();
	
	String url;

	String accountNumber;
    String name;
    String address;
    String telephoneNumber;
    int maxConnections;
    int maxRate = 0;
    int sentOk = 0;
    int errored = 0;
    int bid = 0;
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

	public void run() {
		bidRequests.addListener(new MessageListener<Request>() {

			/**
			 * Request to send bids
			 */
			@Override
			public void onMessage(Request r) {
				requests.add(r);			
			}
		});
		
		winResponses.addListener(new MessageListener<Response>() {

			/**
			 * Request to send bids
			 */
			@Override
			public void onMessage(Response r) {
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
		System.out.println("--------------->"+s);
		
		try {
			returns = connection.sendPost(url,r.br.toJson()); 
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (connection.getResponseCode() == 200) {     /** LET'S BID */
			System.out.println("GOOD BID RECEIVED FROM RTB!");
			Response response = new Response();
			response.html = returns;
			response.from = accountNumber;
			response.id = r.id;
			list.add(response);
			
			// store the adm record
			
		} else {
			System.err.println(returns);
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
			//String s[] = nurl.split("http");
			//nurl = "http" + s[1];
			adm = connection.sendGet(nurl);
			System.out.println("ADM: " + adm);
		} catch(Exception error) {
			
		}
	
		bucket.set(adm);
		
		RCountDownLatch latch = Database.redisson.getCountDownLatch("latch:"+r.id);
		latch.countDown();
	}
}
