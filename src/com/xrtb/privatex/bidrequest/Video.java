package com.xrtb.privatex.bidrequest;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

/** 
 * AN RTB Video object of minimal video attributes. The getter/setters provide the required attributes. Other
 * attributes can be added using the put method.
 * @author Ben M. Faul
 *
 */
public class Video extends HashMap {
	
	/**
	 * Returns the height of the video object
	 * @return int. The height of the object.
	 */
    public int getHeight() {
		return (Integer)get("h");
	}

    /**
     * Set the height of the video object
     * @param h int. The height to set.
     */
	public void setHeight(int h) {
		put("h",h);
	}

	/**
	 * Return the width of the video object.
	 * @return int. The widdth of the object.
	 */
	public int getWidth() {
		return (Integer)get("w");
	}

	/**
	 * Set the width of the video object.
	 * @param w int. The width of the video object.
	 */
	public void setWidth(int w) {
		put("w",w);
	}

	/**
	 * Return the linearity of this video object.
	 * @return
	 */
	public int getLinearity() {
		return (Integer)get("linearity");
	}

	/**
	 * Set the linearity of this video.
	 * @param linearity int. The linearity value.
	 */
	public void setLinearity(int linearity) {
		put("linearity",linearity);
	}

	/**
	 * Get the minduration of the video object requested.
	 * @return int. The min duration in seconds.
	 */
	public int getMinduration() {
		return (Integer)get("minduration");
	}

	/**
	 * Set the min duration of the video we are looking for.
	 * @param minduration
	 */
	public void setMinduration(int minduration) {
		put("minduration",minduration);
	}

	/**
	 * Get the maximum duration of the 
	 * @return
	 */
	public int getMaxduration() {
		return (Integer)get("maxduration");
	}

	/**
	 * Set the maximum duration of the video we are looking for.
	 * @param maxduration int. The max duration in seconds.
	 */
	public void setMaxduration(int maxduration) {
		put("maxduration", maxduration);
	}

	/**
	 * Return the protocols we  want the ad to support.
	 * @return List. The list of protocols the ad should support.
	 */
	public List<Integer> getProtocols() {
		return (List<Integer>)get("protocols");
	}

	/**
	 * Set the protocols we are looking for the ad to support.
	 * @param protocols List. A list of integer designations.
	 */
	public void setProtocols(List<Integer> protocols) {
		put("protocols",protocols);
	}
	
	/**
	 * Set the scalar protocol we want supported.
	 * @param protocol int. The Protocol we want the ad to support.
	 */
	public void setProtocol(int protocol) {
		put("protocol",protocol);
	}
	
	/**
	 * Get the protocol we want supported in the ad.
	 * @return int. The protocol to support.
	 */
	public int getProtocol() {
		return (Integer)get("protocol");
	}

	/**
	 * Return the array of mimes we want supported.
	 * @return String[]. The array of mimes we will acdept for the ad.
	 */
	public String[] getMimes() {
		List<String> list = (List<String>)get("mimes");
		return (String[])list.toArray();
	}
	
	/**
	 * Add a mime type to the list of mimes we can support.
	 * @param item String. A mime type.
	 */
	public void addMime(String item) {
		List<String> list = (List<String>)get("momes");
		if (list == null) {
			list = new ArrayList();
			put("mimes",list);
		}
		list.add(item);
	}

	/**
	 * Return the position od where the ad will be placed.
	 * @return int. Returns the position of the ad on the page.
	 */
	public int getPos(){
		return (Integer)get("pos");
	}

	/**
	 * Set the position of where the ad will be placed on the page.
	 * @param pos int. The position of the ad.
	 */
	public void setPos(int pos) {
		put("pos",pos);
	}
	
	/**
	 * Default constructor, used by JSON
	 */
	public Video() {
		
	}
}
