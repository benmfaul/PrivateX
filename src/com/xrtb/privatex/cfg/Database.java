package com.xrtb.privatex.cfg;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xrtb.bidder.LogPublisher;
import com.xrtb.commands.LogMessage;
import com.xrtb.common.Node;
import com.xrtb.privatex.Publisher;
import com.xrtb.privatex.Response;
import com.xrtb.privatex.Subscriber;
import com.xrtb.privatex.bidrequest.Banner;
import com.xrtb.privatex.bidrequest.Impression;

/**
 * A class to provide Redisson entry points, and to create initial databases.
 * @author Ben M. Faul
 *
 */
public class Database {
	public static ConcurrentMap<String, Publisher> publishers;
	static RList<Subscriber> subscribers;
	public static ConcurrentMap<String, Response> candidates;
	public static Redisson redisson;
	transient Config cfg = new Config();
	transient Gson gson = new GsonBuilder().setPrettyPrinting().create();
	static LogPublisher loggerQueue;

	public int port;
	public static String instanceName;
	public static int logLevel;
	int connections;
	String redis;
	int redisPort;
	String logName;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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
		
		ObjectMapper mapper = new ObjectMapper();
		com.xrtb.privatex.cfg.Config myConfig = mapper.readValue(str, com.xrtb.privatex.cfg.Config.class);
		instanceName = myConfig.instance;
		port = myConfig.port;
		
		App app = myConfig.app;
		Verbosity verbosity = app.verbosity;
		Redis redisInfo = app.redis;
		logLevel = verbosity.level;
		connections = app.connections;
		redis = redisInfo.host;
		redisPort = redisInfo.port;
		logName = redisInfo.logger;
		
		
		
		cfg.useSingleServer()
    	.setAddress(redis + ":" + redisPort)
    	.setConnectionPoolSize(connections);
		setup();
		
		loggerQueue = new LogPublisher(redisson,logName);
		
		System.out.println("Subscribers:");
		printSubscribers();
		System.out.println("-------------\nPublishers:");
		printPublishers();
	}
	
	public static void log(int level, String field, String message) {
		if (logLevel >  0 && (level >= logLevel))
			return;
	    
		if (loggerQueue == null)
			return;
		
		LogMessage msg = new LogMessage(level,instanceName,field,message);
		if (logLevel < 0) {
			if (Math.abs(logLevel) >= logLevel)
				System.out.format("[%s] - %d - %s - %s - %s\n",sdf.format(new Date()),msg.sev,msg.source,msg.field,msg.message);
		} else {
			loggerQueue.add(msg);
		}
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
