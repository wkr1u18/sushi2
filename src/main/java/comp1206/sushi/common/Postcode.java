package comp1206.sushi.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import comp1206.sushi.common.Postcode;

public class Postcode extends Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Map<String,Double> latLong;
	private Number distance;

	private static final String GET_URL = "http://api.postcodes.io/postcodes/";
	
	public Postcode() {
		this.name = "";
		this.distance = Integer.valueOf(0);
	}
	
	public Postcode(String code) {
		this.name = code;
		calculateLatLong();
		this.distance = Integer.valueOf(0);
	}
	
	public Postcode(String code, Restaurant restaurant) {
		this.name = code;
		calculateLatLong();
		calculateDistance(restaurant);
	}
	
	@Override
	public synchronized String getName() {
		return this.name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}
	
	public synchronized Number getDistance() {
		return this.distance;
	}

	public Map<String,Double> getLatLong() {
		return this.latLong;
	}
	
	protected void calculateDistance(Restaurant restaurant) {
		//This function needs implementing
		Postcode destination = restaurant.getLocation();
		this.distance = new Integer((int) Math.round(Geography.distance(this, destination)*1000.0));

	}
	
	protected void calculateLatLong() {
		this.latLong = new ConcurrentHashMap<String,Double>();
		try {
			//Connect with online Postcode API
			URL obj = new URL(GET_URL+this.name);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			//Use HTTP GET method
			con.setRequestMethod("GET");
			//fetch responseCode - if 200 then proceed
			int responseCode = con.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK) {
				//Fetch the input to String
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while((inputLine = in.readLine())!= null) {
					response.append(inputLine);
				}
				in.close();
				//Parse the json
				String json = response.toString();
				Pattern pattern = Pattern.compile("\"longitude\":[+-]?[0-9]+\\.[0-9]*");
				Matcher matcher = pattern.matcher(json);
				
				String longitude;
				String latitude;
				
				if(matcher.find()) {
					longitude = matcher.group(0);
					longitude = longitude.substring(12, longitude.length());
					
				}
				else {
					throw new IOException("JSON parsing error");
				}
				pattern = Pattern.compile("\"latitude\":[+-]?[0-9]+\\.[0-9]*");
				matcher = pattern.matcher(json);
				if(matcher.find()) {
					latitude = matcher.group(0);
					latitude = latitude.substring(11, latitude.length());
					
				}
				else {
					throw new IOException("JSON parsing error");
				}
				
				latLong.put("lat", Double.parseDouble(latitude));
				latLong.put("lon", Double.parseDouble(longitude));
				
			} else {
				throw new IOException("Connection error");
			}
			
		}
		catch(IOException ioe) {
			latLong.put("lat", 0d);
			latLong.put("lon", 0d);
		}
		this.distance = new Integer(0);
	}
	
}
