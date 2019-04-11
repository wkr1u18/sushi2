package comp1206.sushi.common;

public class MessageRegisterUser extends Message {

	private String name;
	private String password;
	private String address;
	private Postcode postcode;
	
	public MessageRegisterUser() {
		super("REGISTER-USER");
	}

	public MessageRegisterUser(String name, String password, String address, Postcode postcode) {
		super("REGISTER-USER");
		this.name=name;
		this.password=password;
		this.address=address;
		this.postcode=postcode;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	public String getAddress() {
		return address;
	}
	public Postcode getPostcode() {
		return postcode;
	}

}
