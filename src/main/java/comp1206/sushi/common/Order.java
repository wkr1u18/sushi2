package comp1206.sushi.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import comp1206.sushi.common.Order;

public class Order extends Model {

	private String status;
	private User buyer;
	private Map<Dish, Number> orderDetails = new HashMap<Dish, Number>();
	public Order() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		this.name = dtf.format(now);
	}
	
	public Order(User buyer, Map<Dish,Number> orderDetails) {
		this();
		this.setUser(buyer);
		this.setOrderDetails(orderDetails);
	}
	
	public void setUser(User buyer) {
		this.buyer = buyer;
	}
	
	public void setOrderDetails(Map<Dish, Number> orderDetails) {
		this.orderDetails=orderDetails;
	}

	public void addDish(Dish dish, Number amount) {
		orderDetails.put(dish, amount);
	}
	
	public Number getDistance() {
		return 1;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}

}
