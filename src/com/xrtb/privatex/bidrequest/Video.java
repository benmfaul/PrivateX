package com.xrtb.privatex.bidrequest;

import java.util.ArrayList;
import java.util.List;

/** 
 * A clasa of minimal video attributes. These are all reauired attributes.
 * @author Ben M. Faul
 *
 */
public class Video {
	/** The height of the ad */
    int h;
    /** The width of the ad */
    int w;
    /** Whether it is linear or not */
    int linerarity;
    /** Min duration in seconds */
	int minduration;
	/** Max duration in seconds */
	int maxduration;
	/** The list of protocols supported */
	List<Integer> protocols;
	/** The mine types supported */
	List<String> mimes = new ArrayList();
	/** The position of the ad on the page */
	int pos;
}
