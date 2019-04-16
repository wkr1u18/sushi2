package comp1206.sushi.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import comp1206.sushi.common.Order;

public class Order extends Model {

	private String status;
	private User buyer;
	private Map<Dish, Number> orderDetails = new ConcurrentHashMap<Dish, Number>();
	private Number cost;
	
	public Order() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		this.name = dtf.format(now);
		this.status="Placed";
	}
	
	public Order(User buyer, Map<Dish,Number> orderDetails) {
		this();
		this.setUser(buyer);
		this.setOrderDetails(orderDetails);
		getCost();
	}
	
	public synchronized void setUser(User buyer) {
		this.buyer = buyer;
	}
	
	public void setOrderDetails(Map<Dish, Number> orderDetails) {
		this.orderDetails.clear();
		this.orderDetails.putAll(orderDetails);
	}

	public void addDish(Dish dish, Number amount) {
		orderDetails.put(dish, amount);
	}
	
	public synchronized Number getDistance() {
		return this.buyer.getDistance();
	}

	@Override
	public synchronized String getName() {
		return this.name;
	}

	public synchronized String getStatus() {
		return status;
	}

	public synchronized void setStatus(String status) {
		this.status = status;
		notifyUpdate();
	}
	
	public Number getCost() {
		if(cost==null) {
			Double result = 0.0;
			for(Map.Entry<Dish, Number> entry : orderDetails.entrySet()) {
				int amount =  entry.getValue().intValue();
				result += entry.getKey().getPrice().doubleValue() * amount;
			}
			cost = result;
		}
		return cost;
	}
	
	public User getUser() {
		return buyer;
	}
	
	public void cancelOrder() {
		System.out.println("Cancelling order");
	}
	
	public Map<Dish, Number> getOrderDetails() {
			return orderDetails;
	}
	

}
