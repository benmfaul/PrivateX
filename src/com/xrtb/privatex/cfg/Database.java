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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xrtb.bidder.LogPublisher;
import com.xrtb.commands.LogMessage;
import com.xrtb.privatex.Campaign;
import com.xrtb.privatex.API;
import com.xrtb.privatex.Publisher;
import com.xrtb.privatex.Response;
import com.xrtb.privatex.Subscriber;
import com.xrtb.privatex.bidrequest.PvtBidRequest;

/**
 * A class to provide Redisson entry points, and to create initial databases.
 * 
 * @author Ben M. Faul
 *
 */
public class Database {
	/** The map of publishers */
	public static ConcurrentMap<String, Publisher> publishers;
	/** The list of subscriber */
	public static RList<Subscriber> subscribers;
	/** Candidate bid requests */
	public static ConcurrentMap<String, Response> candidates;
	/** The global redisson object */
	public static Redisson redisson;
	/** The configuration object */
	transient Config cfg = new Config();
	/** A gson object for pretty printing */
	public transient static Gson gson = new GsonBuilder().disableHtmlEscaping()
			.setPrettyPrinting().create();
	/** The log publishing queue */
	static LogPublisher loggerQueue;

	/** This http port */
	public int port;
	/** This systems instance name */
	public static String instanceName;
	/** The current log level */
	public static int logLevel;
	/** The number of connections to support */
	int connections;
	/** The redis host string */
	String redis;
	/** The redis port */
	int redisPort;
	/** The logger topic name */
	String logName;
	/** A format string for logging */
	static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * Used to create an initial database in Redisson.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Database d = new Database();
		d.redo();
		d.shutdown();
	}

	public void redo() throws Exception {
		/*
		 * setup(); createPublisherStub(); createSubscriberStub();
		 */

		setup("web-campaigns.json");
		printPublishers();
		printSubscribers();
		compile();
	}
	
	public void compile() {
		API cmd = new API();
		Iterator it = publishers.entrySet().iterator();
		for (Map.Entry<String, Publisher> ent : publishers.entrySet()) {
			Publisher p = ent.getValue();
			String key = ent.getKey();
			System.out.println("Compiling Nashorn for " + p.name);
			Map<String, Campaign> c = p.campaigns;
			Iterator iterator = p.campaigns.entrySet().iterator();
			boolean error = false;
			for (Map.Entry<String, Campaign> entry : c.entrySet()) {
				System.out.print("... " + entry.getKey() + " ... ");

				Campaign camp = entry.getValue();
				PvtBidRequest request = new PvtBidRequest();
				ScriptEngine engine = new ScriptEngineManager()
						.getEngineByName("nashorn");
				engine.put("cmd", cmd);
				engine.put("request", request);
				String str = camp.getAttributesAsString();
				try {
					engine.eval(str);
				} catch (Exception err) {
					System.out.println("ERROR");
					System.out.println(err.toString());
					error = true;
				}
				if (!error)
					System.out.println("done.");

			}
			System.out.println("Compilation complete");
		}
	}

	/**
	 * Empty constructor
	 */
	public Database() {

	}

	/**
	 * Open the database using the supplied redis configuration host:port
	 * 
	 * @param redis
	 *            String. host:port of the Redis server.
	 */
	public Database(String configFile) throws Exception {

		byte[] encoded = Files.readAllBytes(Paths.get(configFile));
		String str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded))
				.toString();

		ObjectMapper mapper = new ObjectMapper();
		com.xrtb.privatex.cfg.Config myConfig = mapper.readValue(str,
				com.xrtb.privatex.cfg.Config.class);
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

		cfg.useSingleServer().setAddress(redis + ":" + redisPort)
				.setConnectionPoolSize(connections);
		setup();

		loggerQueue = new LogPublisher(redisson, logName);

		System.out.println("Subscribers:");
		printSubscribers();
		System.out.println("-------------\nPublishers:");
		printPublishers();

		compile();
	}

	public static void log(int level, String field, String message) {
		if (logLevel > 0 && (level > logLevel))
			return;

		if (loggerQueue == null)
			return;

		LogMessage msg = new LogMessage(level, instanceName, field, message);
		if (logLevel < 0) {
			if (Math.abs(logLevel) >= logLevel)
				System.out.format("[%s] - %d - %s - %s - %s\n",
						sdf.format(new Date()), msg.sev, msg.source, msg.field,
						msg.message);
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
		String str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded))
				.toString();
		Map<String, ?> map = gson.fromJson(str, Map.class);

		Map pubs = (Map) map.get("publishers");
		Set set = pubs.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Entry e = (Entry) it.next();
			Publisher p = Publisher.instance((Map) e.getValue());
			publishers.put((String) e.getKey(), p);
		}

		List<Map> subs = (List) map.get("subscribers");
		for (Map x : subs) {
			Subscriber s = Subscriber.instance(x);
			subscribers.add(s);
		}
	}

	/**
	 * Sets up the root redisson object, loads and sets up the initial
	 * publishers and subscribers.
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
