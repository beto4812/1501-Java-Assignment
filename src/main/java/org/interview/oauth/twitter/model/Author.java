package org.interview.oauth.twitter.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class Author implements Comparable<Author> {

	private String id;
	private long epoch;
	private String name;
	private String screen_name;
	private Calendar cal;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss ZZZZZ yyyy");
	private ArrayList<Twit> twits = new ArrayList<Twit>();
	

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	@Override
	public int compareTo(Author o) {
		// TODO Auto-generated method stub
		return Long.compare(epoch, o.epoch);
	}
	
	public void addTwit(Twit twit){
		twits.add(twit);
	}
	
	public void printTwits(){
		Collections.sort(twits);
		Iterator it = twits.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}		
	}

	@Override
	public String toString() {
		return "Author [epochText=" + dateFormatter.format(epoch) + ", epoch=" + epoch + ", name=" + name
				+ ", screen_name=" + screen_name + ", id=" + id + ", twits="+twits.size()+"]";
	}



}
