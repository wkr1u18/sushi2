package comp1206.sushi.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.User;

public class Basket {

	private User owner;
	private Map<Dish, Number> basket;
	
	public Basket() {
	}
	
	public Basket(User owner) {
		this.owner = owner;
		basket = new ConcurrentHashMap<Dish, Number>();
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public User getOwner() {
		return this.owner;
	}
	
	public Map<Dish, Number> getBasket() {
		return basket;
	}
	
	public void clearBasket() {
		basket.clear();
	}
	
	public void addDishToBasket(Dish dish, Number quantity) {
		basket.put(dish, quantity);
	}
	
	public void updateDishInBasket(Dish dish, Number newQuantity) {
		addDishToBasket(dish, newQuantity);
	}
	
	public Number getBasketCost() {
		Double result = 0.0;
		for(Map.Entry<Dish, Number> entry : basket.entrySet()) {
			int amount =  entry.getValue().intValue();
			result += entry.getKey().getPrice().doubleValue() * amount;
		}
		return result;
	}

}
