package org.interview.oauth.twitter.model;

import java.util.List;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class TwitsFeed {
	
	
	@Key("twits")
	private List<Twit> twits;

	public List<Twit> getTwits() {
	    return twits;
	}
	
	static class Twit extends GenericJson {
		@Key("created_at")
		private String createdAt;
		
		
	}

}


