package com.xrtb.privatex.bidrequest;

/**
 * The RTB device object
 * @author Ben M. Faul
 *
 */
public class Device {
    public String didsha1;
    public String dpidsha1;
    public String ip;
    public String carrier;
    public String ua;
    public String make; 
    public String model; 
    public String osv;
    public int connectionType; 
    public int devicetype;
    public  Geo geo = new Geo();;
    
    public Device() {
    	
    }
}
