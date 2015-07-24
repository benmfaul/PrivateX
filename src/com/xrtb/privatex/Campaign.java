package com.xrtb.privatex;

import java.util.List;

/**
 * A Class that encapsulates a Campaign used by a web publisher.
 * @author Ben M. Faul
 *
 */

public class Campaign {
	/** The floor we are looking for on this campaign */
	Double price;
	/** The identifier of this campaign */
	String identifier;
	/** The number of served ads */
	long served;
	/** the number of requested ads */
	long requested;
	/** The http address of this page */
	String page;
	/** The javascript attributes used by this campaign to create an RTB bid */
	List<String> attributes;
	/** All the attributes concatenated together */
	transient String attributesAsString;

	/**
	 * Default empty constructor 
	 */
	public Campaign() {

	}

	/**
	 * Return all the attribute strings as one string of javascript.
	 * @return String. The conglomerated javascript of this campaign.
	 */
	public String getAttributesAsString()  {
		if (attributesAsString == null) {
			attributesAsString = "";
			for (String s : attributes) {
				attributesAsString += s + "\n";
			}
		}
		return attributesAsString;
	}

}
