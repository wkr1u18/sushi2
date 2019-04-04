package comp1206.sushi.common;

import comp1206.sushi.common.Supplier;

public class Supplier extends Model {

	private String name;
	private Postcode postcode;
	private Number distance;

	public Supplier(String name, Postcode postcode) {
		this.name = name;
		this.postcode = postcode;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized Postcode getPostcode() {
		return this.postcode;
	}
	
	public synchronized void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}

	public synchronized Number getDistance() {
		return postcode.getDistance();
	}

}
