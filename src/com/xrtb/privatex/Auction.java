package com.xrtb.privatex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.redisson.core.RBucket;
import org.redisson.core.RCountDownLatch;
import org.redisson.core.RList;
import org.redisson.core.RTopic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xrtb.privatex.bidrequest.PvtBidRequest;
import com.xrtb.privatex.bidresponse.Body;
import com.xrtb.privatex.cfg.Database;

/**
 * A class that implements best price auction.
 * @author Ben M. Faul
 *
 */
public class Auction implements Runnable {
	/** Command object from the web page auction request */
	Command cmd;
	/** The campaign to use on the request */
	Campaign campaign;
	/** The user agent of the web user */
	String ua;
	/** The location record of the user */
	LatLong loc;
	/** The list of bid responses from bidder */
	List<Response> bids = new ArrayList();
	/** The ADM field that will be returned on successful bid
	String html;
	/** My base thread */
	Thread me;
	/** The bid request ID */
	String uuid = UUID.randomUUID().toString();
	/** Indicates thread is still running */
	volatile boolean running = true;
	/** The ADM html */
	String html;
	String ipAddr;
	transient private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	PvtBidRequest pvt;
	

	/**
	 * Creates and executes an auction.
	 * @param campaign Campaign. The campaign to use for the RTB exchange/
	 * @param ua String. The web user agent.
	 * @param loc LatLong. The lat/long of the user.
	 * @param ipAddr String. The IP address of the web user.
	 */
	public Auction(Command cmd, Publisher publisher, Campaign campaign, String ipAddr) throws Exception {
		this.cmd = cmd;
		this.campaign = campaign;
		this.ipAddr = ipAddr;
		
		pvt = new PvtBidRequest();
		pvt.id = UUID.randomUUID().toString();
		pvt.site.publisher.id = publisher.id;
		pvt.site.publisher.name = publisher.name;
		pvt.site.id = publisher.id;
		pvt.site.name = publisher.name;
		pvt.site.domain = publisher.domain;
		
		engine.put("cmd", cmd);
		engine.put("request", pvt);
		try {
			engine.eval(campaign.getAttributesAsString());
		} catch (Exception error) {
			System.out.println(error.toString());
			System.out.println(campaign.getAttributesAsString());
			Database.log(3,"Auction/evalJS","Error: " + error.toString());
			throw error;
		}

		me = new Thread(this);
		me.start();
	}

	/**
	 * Kill the auction
	 */
	public void cancel() {
		me.interrupt();
	}

	/**
	 * Is the auction done?
	 * @return boolean. Returns true of the auction concluded.
	 */
	public boolean isDone() {
		return !running;
	}

	/**
	 * Runs the auction, request bids, sort responses by price, get best price, notify winner, and form the html ADM.
	 */
	public void run() {

		try {
			requestBids();
			if (bids.size() == 0) {
				running = false;
				return;
			}
			Collections.sort(bids, new CustomComparator());
			Response winner = bids.get(0);
			notifyWinner(winner);

		} catch (Exception e) {
			e.printStackTrace();
		}
		running = false;
	}

	/**
	 * Request bids from the subscribing RTB exchanges.
	 */
	private void requestBids() {
		RList r = (RList) Database.redisson.getList(uuid);
		r.expire(300, TimeUnit.SECONDS);
		
		RTopic<Request> bidrequests = Database.redisson.getTopic("bidrequests");
		
		Request request = new Request(uuid, pvt);
		bidrequests.publish(request);

		Database.log(5,"Auction/requestBids:waiting","Waiting for response from subscribers");
		try {
			Thread.yield();
			Thread.sleep(500);
		} catch (Exception error) {

		}
		r = Database.redisson.getList(uuid);
		for (int i = 0; i < r.size(); i++) {
			Response b = (Response) r.get(i);
			bids.add(b);
		}
		Database.log(5,"Auction/requestBids:complete","Number of bids: " + bids.size());
		r.deleteAsync();
	}

	/**
	 * Notify the winner of an auction, and retrieve the HTML for ad.
	 * @param winner Response. The winner's response to the request.
	 * @throws Exception 
	 */
	public void notifyWinner(Response winner)  {
		RCountDownLatch latch = Database.redisson.getCountDownLatch("latch:"+uuid);
	//	System.out.println("HTML: " + winner.toString());
		 Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map x = gson.fromJson(winner.html, Map.class);
		System.out.println("MAP: " + gson.toJson(x));
		
		latch.trySetCount(1);
		
		winner.id = uuid;
				
		RTopic topic = Database.redisson.getTopic(winner.from); /** Send the notification */
		topic.publish(winner);                 // was req
		try {
			latch.await(1000,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Database.log(5,"Auction/notifyWinner","Winner did not respond!");
			return;                          // sorry, winner timed out, no ad will be served.
		}

		RBucket<String> bucket = Database.redisson.getBucket(uuid);
		html = bucket.get();
		latch.deleteAsync();
		bucket.deleteAsync();
	}

	/**
	 * Returns the html of the auction.
	 * @return
	 */
	public String process() {
		return html;
	}
}

/**
 * Comparator for sorting bid responses by price.
 * @author Ben M. Faul
 *
 */
class CustomComparator implements Comparator<Response> {
	@Override
	public int compare(Response o1, Response o2) {
		if (o1.price == o2.price)
			return 0;
		if (o1.price < o2.price)
			return -1;
		return 1;
	}
}

