package comp1206.sushi.common;

public class MessageLogin extends Message {

	private String username;
	private String password;
	
	public MessageLogin() {
		super("LOGIN");
	}

	public MessageLogin(String username, String password) {
		super("LOGIN");
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}

}
