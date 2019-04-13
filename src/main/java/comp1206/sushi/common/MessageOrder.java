package comp1206.sushi.common;

public class MessageOrder extends Message {

	private String username;
	private String orderName;
	
	public MessageOrder() {
		// TODO Auto-generated constructor stub
	}

	public MessageOrder(String contents, String username, String orderName) {
		super(contents);
		this.username = username;
		this.orderName = orderName;
	}
	
	public MessageOrder(String contents, Order order) {
		this(contents, order.getUser().getName(), order.getName());
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getOrderName() {
		return orderName;
	}

}
