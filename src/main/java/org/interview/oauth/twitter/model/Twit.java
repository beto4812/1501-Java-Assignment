package org.interview.oauth.twitter.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Twit implements Comparable<Twit>{

	private String id;
	private long epoch;
	private String text;
	private String author_id;
	private Calendar cal;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss ZZZZZ yyyy");
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getEpoch() {
		return epoch;
	}

	public void setEpoch(long epoch) {
		this.epoch = epoch;
		cal = new GregorianCalendar();
		cal.setTimeInMillis(epoch);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor_id() {
		return author_id;
	}

	public void setAuthor_id(String author_id) {
		this.author_id = author_id;
	}

	@Override
	public int compareTo(Twit o) {
		// TODO Auto-generated method stub
		return Long.compare(epoch, o.epoch);
	}

	@Override
	public String toString() {
		return "Twit [epochText=" + dateFormatter.format(epoch) + ", epoch=" + epoch + ", text=" + text
				+ ", author_id=" + author_id + ", id=" + id + "]";
	}


	
	
}
