package comp1206.sushi.common;

public class MessageBasket extends Message {

	private String dish;
	private Number amount;
	
	
	public MessageBasket() {
		// TODO Auto-generated constructor stub
	}

	public MessageBasket(String command, String dish, Number amount) {
		super(command);
		this.dish = dish;
		this.amount = amount;
	}
	
	public void setDish(String dish) {
		this.dish = dish;
	}
	
	public String getDish() {
		return dish;
	}
	
	public void setAmount(Number amount) {
		this.amount = amount;
	}
	
	public Number getAmount() {
		return amount;
	}

}
