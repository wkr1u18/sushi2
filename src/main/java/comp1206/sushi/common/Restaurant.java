package comp1206.sushi.common;

import java.io.Serializable;

public class Restaurant implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Postcode location;

	public Restaurant() {
		
	}
	
	public Restaurant(String name, Postcode location) {
		this.name = name;
		this.location = location;
	}
	
	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized Postcode getLocation() {
		return location;
	}

	public synchronized void setLocation(Postcode location) {
		this.location = location;
	}
	
}
