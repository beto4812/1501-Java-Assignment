package org.interview.oauth.twitter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.interview.oauth.twitter.model.Author;
import org.interview.oauth.twitter.model.Twit;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.json.jackson.JacksonFactory;

/**
 * 
 * @author Alberto Vazquez
 *
 */
public class TwitterStream {

	private final String cosumerKey = "vp8qXAMoZzy6jowJdtouPLUUb";
	private final String consumerSecret = "IMx3eIRfXXbRimoIz7cNpZCl0dr9dYEdRuDVTr2C4LdResXjN7";
	private final String endpoint = "https://stream.twitter.com/1.1/statuses/filter.json";
	private final String filterWord = "bieber";
	private final int maxTimeoutMs = 30000; // 30 seconds
	private final int maxMessages = 100; // 100 messages

	private HashMap<String, Author> authors = new HashMap<String, Author>();
	private Author currentAuthor = new Author();
	private Twit currentTwit = new Twit();
	private TwitterAuthenticator authenticator;
	private int numberOfMessages = 0;
	private int messageType = 0; // 1 twit // 2 delete
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss ZZZZZ yyyy");

	public TwitterStream() {
		PrintStream stream = new PrintStream(System.out);
		authenticator = new TwitterAuthenticator(stream, cosumerKey,
				consumerSecret);
	}

	private InputStream getInputStream() {
		try {
			HttpRequestFactory httpRequestFactory = authenticator
					.getAuthorizedHttpRequestFactory();
			HttpRequest req = httpRequestFactory
					.buildGetRequest(new GenericUrl(endpoint + "?track="
							+ filterWord));
			HttpResponse response = req.execute();
			return response.getContent();
		} catch (TwitterAuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void parseStream() {
		InputStream inputStream = getInputStream();
		JsonFactory factory = new JacksonFactory();
		JsonParser parser = null;
		JsonToken token = null;
		try {
			parser = factory.createJsonParser(inputStream);
			long startTime = System.nanoTime();
			long duration = 0;
			while (numberOfMessages < maxMessages
					&& (duration / 1000000) < maxTimeoutMs) {
				token = parser.nextToken();
				if (token.equals(JsonToken.FIELD_NAME)) {
					parser.nextToken();
					if (parser.getCurrentName().equals("created_at")) {
						insertParsed();
					}
					parseCurrent(parser, token);
				}
				duration = (System.nanoTime() - startTime);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void parseCurrent(JsonParser parser, JsonToken token)
			throws IOException, ParseException {
		switch (parser.getCurrentName()) {
		case "created_at":
			messageType = 1;
			currentTwit.setEpoch(dateFormatter.parse(parser.getText())
					.getTime());
			break;
		case "text":
			if (messageType == 1 && currentTwit.getText() == null)
				currentTwit.setText(parser.getText());
			break;
		case "user":
			parseCurrentAuthor(parser, token);
			break;
		case "id":
			if (messageType == 1)
				currentTwit.setId(parser.getText());
			break;
		case "delete":
			System.out.println("new message...");
			numberOfMessages++;
			messageType = 2;
		}
	}

	private void parseCurrentAuthor(JsonParser parser, JsonToken token)
			throws IOException, ParseException {
		while (!token.equals(JsonToken.END_OBJECT)) {
			if (token.equals(JsonToken.FIELD_NAME)) {
				if (parser.getCurrentName().equals("id")) {
					parser.nextToken();
					currentTwit.setAuthor_id(parser.getText());
					currentAuthor.setId(parser.getText());
				} else if (parser.getCurrentName().equals("created_at")) {
					parser.nextToken();
					currentAuthor.setEpoch(dateFormatter
							.parse(parser.getText()).getTime());
				} else if (parser.getCurrentName().equals("name")) {
					parser.nextToken();
					currentAuthor.setName(parser.getText());
				} else if (parser.getCurrentName().equals("screen_name")) {
					parser.nextToken();
					currentAuthor.setScreen_name(parser.getText());
				}
			}
			token = parser.nextToken();
		}
	}

	private void insertParsed() {
		if (currentTwit.getAuthor_id() != null) {
			System.out.println("new message...");
			numberOfMessages++;
			if (authors.get(currentAuthor.getId()) == null) {
				currentAuthor.addTwit(currentTwit);
				authors.put(currentAuthor.getId(), currentAuthor);
			} else {
				authors.get(currentAuthor.getId()).addTwit(currentTwit);
			}
			currentTwit = new Twit();
			currentAuthor = new Author();
		}
	}

	private ArrayList<Author> getResults() {
		ArrayList<Author> authorValues = null;
		if (!authors.isEmpty()) {
			authorValues = new ArrayList<Author>(authors.values());
			Collections.sort(authorValues);

			Iterator it = authorValues.iterator();
			Author author;
			while (it.hasNext()) {
				author = (Author) it.next();
				System.out.println(author);
				author.printTwits();
			}
		} else {
			System.out.println("No results");
		}
		return authorValues;
	}

	private void start() {
		parseStream();
	}

	public static void main(String... args) {
		TwitterStream stream = new TwitterStream();
		stream.start();
		stream.getResults();
	}

}
