package comp1206.sushi.common;

import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.User;

public class User extends Model {
	
	private String name;
	private String password;
	private String address;
	private Postcode postcode;
	
	private Integer connectionId;
	
	public User() {
		
	}

	public User(String username, String password, String address, Postcode postcode) {
		this.name = username;
		this.password = password;
		this.address = address;
		this.postcode = postcode;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized Number getDistance() {
		return postcode.getDistance();
	}

	public synchronized Postcode getPostcode() {
		return this.postcode;
	}
	
	public synchronized void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}
	
	public synchronized boolean verify(String password) {
		if(password.equals(this.password)) {
			return true;
		}
		return false;
	}
	
	public synchronized void setConnectionId(Integer id) {
		this.connectionId = id;
	}
	
	public synchronized Integer getConnectionId() {
		return connectionId;
	}

}
