package com.xrtb.privatex.bidrequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * AN RTB Video object of minimal video attributes. The getter/setters provide the reuired attributes. Other
 * attributes can be added using the put method.
 * @author Ben M. Faul
 *
 */
public class Video extends HashMap {
	
    public int getHeight() {
		return (Integer)get("h");
	}

	public void setHeight(int h) {
		put("h",h);
	}

	public int getWidth() {
		return (Integer)get("w");
	}

	public void setWidth(int w) {
		put("w",w);
	}

	public int getLinearity() {
		return (Integer)get("linearity");
	}

	public void setLinearity(int linearity) {
		put("linearity",linearity);
	}

	public int getMinduration() {
		return (Integer)get("minduration");
	}

	public void setMinduration(int minduration) {
		put("minduration",minduration);
	}

	public int getMaxduration() {
		return (Integer)get("maxduration");
	}

	public void setMaxduration(int maxduration) {
		put("maxduration", maxduration);
	}

	public List<Integer> getProtocols() {
		return (List<Integer>)get("protocols");
	}

	public void setProtocols(List<Integer> protocols) {
		put("protocols",protocols);
	}

	public List<String> getMimes() {
		return (List<String>)get("mimes");
	}

	public void setMimes(List<String> mimes) {
		put("mimes",mimes);
	}

	public int getPos() {
		return (Integer)get("pos");
	}

	public void setPos(int pos) {
		put("pos",pos);
	}
	
	public Video() {
		
	}
}
