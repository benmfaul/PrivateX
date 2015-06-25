package com.xrtb.privatex;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.MessageListener;
import org.redisson.core.RTopic;

import com.xrtb.commands.LogMessage;
import com.xrtb.commands.PixelClickConvertLog;

public class WatchLog {
	Config cfg = new Config();
	Redisson redisson;	
	int watch;
	
	public static void main(String args[]) throws Exception {
		WatchLog watch = new WatchLog("localhost:6379","xlog",5);
	}
	
	/**
	  * Instantiate a connection to localhost (Redisson)
	  * Also contains the listener for the pixels, clicks and conversions.
	  * @param redis String. The redis host:port string.
	  * @param channel String. The topic of what we are looking for.
	  * @param what int. The integer type of what we are looking for.
	  */
	 public WatchLog(String redis, String channel, int what) {
			cfg.useSingleServer()
	    	.setAddress(redis)
	    	.setConnectionPoolSize(10);
			redisson = Redisson.create(cfg);
	     
		 watch = what;
	     RTopic<LogMessage> responses = redisson.getTopic(channel);
	     responses.addListener(new MessageListener<LogMessage>() {
	         @Override
	         public void onMessage(LogMessage msg) {
	        	 if (watch == -1 || msg.sev <= watch)
	        		 System.out.format("%d - %s - %s - %s\n",msg.sev,msg.source,msg.field,msg.message);
	         }
	     });
	 }
}
