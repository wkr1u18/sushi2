package comp1206.sushi.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Basket {

	private Map<Dish, Number> basket;
	public Basket() {
		basket = new ConcurrentHashMap<Dish, Number>();
	}
	
	
	public Map<Dish, Number> getBasket() {
		return basket;
	}
	
	public void clearBasket() {
		basket.clear();
	}
	
	public void addDishToBasket(Dish dish, Number quantity) {
		if(basket.get(dish)!=null) {
			Number newQuantity = basket.get(dish).intValue()+quantity.intValue();
			//basket.remove(dish);
			basket.put(dish, newQuantity);
		}
		else {
			basket.put(dish, quantity);
		}
	}
	
	public void updateDishInBasket(Dish dish, Number newQuantity) {
		if(newQuantity.intValue()==0) {
			basket.remove(dish);
		}
		else {
			basket.put(dish, newQuantity);
		}
	}
	
	public Number getBasketCost() {
		Double result = 0.0;
		for(Map.Entry<Dish, Number> entry : basket.entrySet()) {
			int amount =  entry.getValue().intValue();
			result += entry.getKey().getPrice().doubleValue() * amount;
		}
		return result;
	}
	
	public synchronized Map<Dish, Number> getContents() {
		return basket;
	}
	
	public Order checkoutBasket(User owner) {
		return new Order(owner, getContents());
	}

}
