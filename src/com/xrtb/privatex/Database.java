package com.xrtb.privatex;

import java.util.concurrent.ConcurrentMap;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xrtb.common.Node;
import com.xrtb.privatex.br.Banner;
import com.xrtb.privatex.br.Impression;

/**
 * A class to provide Redisson entry points, and to create initial databases.
 * @author Ben M. Faul
 *
 */
public class Database {
	static ConcurrentMap<String, Publisher> publishers;
	static RList<Subscriber> subscribers;
	public static Redisson redisson;
	transient Config cfg = new Config();
	transient Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Used to create an initial database in Redisson.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Database d = new Database();
		d.redo();
		d.shutdown();
	}
	
	public void redo() throws Exception {
		setup();
		createPublisherStub();
		createSubscriberStub();
		printPublishers(); 
		printSubscribers();  
	}

	/**
	 * Empty constructor
	 */
	public Database() {
		
	}
	
	/**
	 * Open the database using the supplied redis configuration host:port
	 * @param redis String. host:port of the Redis server.
	 */
	public Database(String redis) {
		cfg.useSingleServer()
    	.setAddress(redis)
    	.setConnectionPoolSize(10);
		setup();
		System.out.println("Subscribers:");
		printSubscribers();
		System.out.println("-------------\nPublishers:");
		printPublishers();
	}
	
	public void reset() {
		setup();
	}
	
	/**
	 * Sets up the root redisson object, loads and sets up the initial publishers and subscribers.
	 */
	public void setup() {
		redisson = Redisson.create();
		
		/**
		 * Load the publishers
		 */
		publishers = redisson.getMap("publishers");	
		
		/**
		 * Start the subscribers
		 */
		subscribers = redisson.getList("subscribers");
		for (Subscriber s : subscribers) {
			s.setup();
		}
	}
	
	/**
	 * Print the RTB exchange subscribers
	 */
	public void printSubscribers() {
		System.out.println(gson.toJson(subscribers));
	}
	
	/**
	 * Print the web content publishers
	 */
	public void printPublishers() {
		System.out.println(gson.toJson(publishers));
	}
	
	/**
	 * Create a stub subscriber
	 */
	public void createSubscriberStub() {
		subscribers.clear();
		Subscriber s = new Subscriber();
		
		s.url = "myurlhere";
		s.accountNumber = "666-666-666";
	    s.name = "Test Subscriber";
	    s.address = "123 Anystreet, Anytown USA 12345";
	    s.telephoneNumber = "555-1212";
	    s.maxConnections = 10;
	    s.maxRate = 100;
	    subscribers.add(s);
		
	}
	
	/**
	 * Create a stub publisher
	 * @throws Exception
	 */
	public void createPublisherStub() throws Exception {
		publishers.clear();
		Publisher p = new Publisher();
		p.name = "Pubname";
		p.address = "PubAddress"; 
		p.telephoneNumber = "PubTel";
		
		Campaign c = new Campaign();
		c.price = 5.0;
		c.domain = "yourdomainhere.com";
		c.keywords = "football,mixed martial arts";
		c.ref =  "http://www.iab.net";
		c.page = "yourdomainhere.com/football-and-mma.html";

		
	    c.identifier = "campaign identifier here";
	    c.impression.id = "35c22289-06e2-48e9-a0cd-94aeb79fab43-1";
	    c.impression.instl = 0;
	    c.impression.banner.w = 320;
	    c.impression.banner.h = 50;    
	    c.cat.add("IAB1");
	    c.cat.add("IAB2");
	    
		
		Node n = new Node("nodename", "a.b","EQUALS","ben");
		c.attributes.add(n);
		p.campaigns.put("campname",c);
		publishers.put("pubacct", p);
	}
	
	/**
	 * Stops redisson.
	 */
	public void shutdown() {
		redisson.shutdown();
	}
}
