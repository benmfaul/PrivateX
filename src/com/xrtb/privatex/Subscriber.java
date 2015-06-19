package com.xrtb.privatex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.redisson.core.MessageListener;
import org.redisson.core.RBucket;
import org.redisson.core.RCountDownLatch;
import org.redisson.core.RList;
import org.redisson.core.RTopic;

import com.google.gson.Gson;
import com.xrtb.common.HttpPostGet;
import com.xrtb.pojo.BidRequest;
import com.xrtb.privatex.br.PvtBidRequest;

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
    List<String> uuids = new ArrayList<String>();           
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
		
		winResponses.addListener(new MessageListener<Request>() {

			/**
			 * Request to send bids
			 */
			@Override
			public void onMessage(Request r) {
				String from = r.uuid;
				uuids.add(r.uuid);
				
			}
		});
		
		while(true) {
			try {
				Thread.sleep(1);
				if (uuids.size() > 0) {
					String id = uuids.remove(0);
				    doAdm(id);
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
			Response response = new Response();
			response.html = returns;
			response.from = accountNumber;
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
	private void doAdm(String id) {
		RBucket<String> bucket = Database.redisson.getBucket(id);
		// Notify bidder of the win, retrieve the ADM, do the accounting
		String adm  = "You are here";
		try {
		; //		returns = connection.sendPost(arg0, arg1);
		} catch(Exception error) {
			
		}
	
		bucket.set(adm);
		
		RCountDownLatch latch = Database.redisson.getCountDownLatch("latch:"+id);
		latch.countDown();
	}
}
