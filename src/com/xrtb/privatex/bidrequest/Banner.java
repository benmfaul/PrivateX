package com.xrtb.privatex.bidrequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Banner {
	/** Height of the impression in pixels */
	public int h;
	/** Width of the impression in pixels */
	public int w;
	/** Position of impression. Refer to Table 6.5 in OpenRTB API Spec v2.1 */
	public int pos;
	/** If API = 3 support for MRAID v1; If API = 5 support for MRAID v2 */
	int api;

	public Banner() {
		
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
}
