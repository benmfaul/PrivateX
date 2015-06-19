package com.xrtb.privatex;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

	int port = 9090;
	String instanceName;
	int logLevel = 2;
	int connections = 10;
	String redis = "localhost";
	int redisHost = 6379;
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
		/*setup();
		createPublisherStub();
		createSubscriberStub(); */
		
		setup("web-campaigns.json");
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
	public Database(String configFile) throws Exception {
		
		byte[] encoded = Files.readAllBytes(Paths.get(configFile));
		String str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
		
		Map<?, ?> m = gson.fromJson(str,Map.class);
		instanceName = (String)m.get("instance");
		
		m = (Map)m.get("app");
		Map verbosity = (Map)m.get("verbosity");
		if (verbosity != null) {
			logLevel = ((Double)verbosity.get("level")).intValue();
		}
		if (m.get("connections")!=null) {
			Double d = (Double)m.get("connections");
			connections = d.intValue();
		}
		
		cfg.useSingleServer()
    	.setAddress(redis + ":" + redisHost)
    	.setConnectionPoolSize(connections);
		setup();
		System.out.println("Subscribers:");
		printSubscribers();
		System.out.println("-------------\nPublishers:");
		printPublishers();
	}
	
	public void reset() {
		setup();
	}
	
	public void setup(String file) throws Exception {
		redisson = Redisson.create();
		publishers = redisson.getMap("publishers");	
		subscribers = redisson.getList("subscribers");	
		publishers.clear();
		subscribers.clear();
		
		byte[] encoded = Files.readAllBytes(Paths.get(file));
		String str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
		Map<String,?> map = gson.fromJson(str,Map.class);

		Map pubs = (Map)map.get("publishers");
		Set set = pubs.entrySet();
		Iterator it = set.iterator();
		while(it.hasNext() ){
			Entry e = (Entry)it.next();
			Publisher p = Publisher.instance((Map)e.getValue());
			publishers.put((String) e.getKey(), p);
		}
		
		List<Map> subs = (List)map.get("subscribers");
		for (Map x : subs) {
			Subscriber s = Subscriber.instance(x);
			subscribers.add(s);
		}
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
		System.out.println("Subscribers:");
		System.out.println(gson.toJson(subscribers));
	}
	
	/**
	 * Print the web content publishers
	 */
	public void printPublishers() {
		System.out.println("Publishers:");
		System.out.println(gson.toJson(publishers));
	}
	
	/**
	 * Stops redisson.
	 */
	public void shutdown() {
		redisson.shutdown();
	}
}
