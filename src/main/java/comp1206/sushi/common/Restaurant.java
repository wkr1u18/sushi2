package comp1206.sushi.common;

public class Restaurant {

	private String name;
	private Postcode location;

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
