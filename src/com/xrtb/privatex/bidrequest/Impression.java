package com.xrtb.privatex.bidrequest;

import java.util.UUID;

public class Impression {
   public  String id = UUID.randomUUID().toString(); 
   public int  instl;
   public Banner banner;
   public Video video;
   public Ext ext = new Ext();
   
   public Impression() {
	   
   }
   
   public Impression doBanner() {
	   video = null;
	   banner = new Banner();
	   return this;
   }
   
   public Impression doVideo() {
	   banner = null;
	   video = new Video();
	   return this;
   }
}
