package com.xrtb.privatex;

/**
 * A simple class for holding a lat/long object.
 * @author Ben M. Faul
 *
 */
public class LatLong {

	double lat = 0;
	double lon = 0;
	
	/**
	 * Default constructor
	 */
	public LatLong() {
		
	}
	
	/**
	 * Create a lat/long object.
	 * @param lat Double. The latitude,
	 * @param lon Double. The longitude.
	 */
	public LatLong(Double lat, Double lon) {
		this.lat = lat;
		this.lon = lon;
	}
}

