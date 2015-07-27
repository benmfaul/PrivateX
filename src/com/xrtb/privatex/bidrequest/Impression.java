package com.xrtb.privatex.bidrequest;

import java.util.UUID;

/**
 * The RTB Impression object
 * @author Ben M. Faul
 *
 */

public class Impression {
   /** The impression id */
   public  String id = UUID.randomUUID().toString(); 
   /** Interstitial */
   public int  instl;
   /** The banner object */
   public Banner banner;
   /** The video object. */
   public Video video;
   /** An extension object */
   public Ext ext = new Ext();
   
   /** 
    * Default constructor 
    */
   public Impression() {
	   
   }
   
   /**
    * Make this a banner impression. Nulls the video, if present.
    * @return Impression. A reference to itself.
    */
   public Impression doBanner() {
	   video = null;
	   banner = new Banner();
	   return this;
   }
   
   /**
    * Make this a video impression. Nulls the banner, if present.
    * @return A reference to itself.
    */
   public Impression doVideo() {
	   banner = null;
	   video = new Video();
	   return this;
   }
}
