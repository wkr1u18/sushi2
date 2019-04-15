package comp1206.sushi.common;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Ingredient;

public class Dish extends Model {

	private String name;
	private String description;
	private Number price;
	private Map <Ingredient,Number> recipe;
	private Number restockThreshold;
	private Number restockAmount;

	public Dish() {
		
	}

	public Dish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.restockThreshold = restockThreshold;
		this.restockAmount = restockAmount;
		this.recipe = new ConcurrentHashMap<Ingredient,Number>();
		
	}
	
	
	public synchronized void setStock(Number newStock) {
		this.restockAmount = newStock;
	}
	
	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized String getDescription() {
		return description;
	}

	public synchronized void setDescription(String description) {
		this.description = description;
	}

	public synchronized Number getPrice() {
		return price;
	}

	public synchronized void setPrice(Number price) {
		this.price = price;
	}

	public Map <Ingredient,Number> getRecipe() {
		return recipe;
	}

	public synchronized void setRecipe(Map <Ingredient,Number> recipe) {
		this.recipe.clear();
		this.recipe.putAll(recipe);
	}

	public synchronized void setRestockThreshold(Number restockThreshold) {
		this.restockThreshold = restockThreshold;
	}
	
	public synchronized void setRestockAmount(Number restockAmount) {
		this.restockAmount = restockAmount;
	}

	public synchronized Number getRestockThreshold() {
		return this.restockThreshold;
	}

	public synchronized Number getRestockAmount() {
		return this.restockAmount;
	}

}
