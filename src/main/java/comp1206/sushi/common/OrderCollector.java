package comp1206.sushi.common;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderCollector implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Order order;
	private Map<Dish, Number> soFar;
	private volatile boolean isComplete = false;
	
	public boolean isComplete() {
		return this.isComplete;
	}
	
	public OrderCollector(Order order) {
		this.order = order;
		this.soFar = new ConcurrentHashMap<Dish, Number>();
		for(Map.Entry<Dish, Number> entry : order.getOrderDetails().entrySet()) {
			soFar.put(entry.getKey(), 0);
			System.out.println(entry.getKey() + ": " + soFar.get(entry.getKey()));
		}
	}
	
	public Map<Dish, Number> getCollectedSoFar() {
		return soFar;
	}
	
	public Order getOrder() {
		return this.order;
	}
	
	public void notifyCollected() {
		isComplete = true;
	}

}
