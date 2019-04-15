package comp1206.sushi.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import comp1206.sushi.server.ServerInterface;

public class StockManagement {

	private ServerInterface server;
	
	private Map<Dish, Number> dishStock;
	private Map<Ingredient,Number> ingredientStock;
	private Map<Dish, Integer> dishesBeingRestocked;
	private Map<Ingredient, Integer> ingredientsBeingRestocked;
	
	private volatile boolean dishRestockingEnabled;
	private volatile boolean ingredientsRestockingEnabled;
	
	public StockManagement(ServerInterface server) {
		
		dishStock = Collections.synchronizedMap(new HashMap<Dish, Number>());
		ingredientStock = Collections.synchronizedMap(new HashMap<Ingredient, Number>());
		dishesBeingRestocked = Collections.synchronizedMap(new HashMap<Dish, Integer>());
		ingredientsBeingRestocked = Collections.synchronizedMap(new HashMap<Ingredient, Integer>());
		dishRestockingEnabled = true;
		ingredientsRestockingEnabled = true;
		this.server = server;
	}
	
	public boolean isDishRestockingEnabled() {
		return dishRestockingEnabled;
	}
	
	public boolean isIngredientRestockingEnabled() {
		return ingredientsRestockingEnabled;
	}
	
	public void setRestockingIngredientsEnable(boolean enabled) {
		ingredientsRestockingEnabled=enabled;
	}
	
	public void setRestockingDishesEnable(boolean enabled) {
		dishRestockingEnabled=enabled;
	}
	
	public void setStock(Dish dish, Number stock) {
		synchronized(dishStock) {
			dishStock.put(dish, stock);
		}
	}
	
	public void setStock(Ingredient ingredient, Number stock) {
		synchronized(ingredientStock) {
			ingredientStock.put(ingredient, stock);
		}
	}
	
	public synchronized Map<Dish,Number> getDishStockLevels() {
		return dishStock;
	}
	
	public synchronized Map<Ingredient, Number> getIngredientStockLevels() {
		return ingredientStock;
	}
	
	public void remove(Dish d) {
		synchronized(dishStock) {
			dishStock.remove(d);
		}
	}
	
	public void remove(Ingredient i) {
		synchronized(ingredientStock) {
			ingredientStock.remove(i);
		}
	}
	
	public void setDishesBeingRestocked(Dish d, Integer n) {
		synchronized(dishesBeingRestocked) {
			dishesBeingRestocked.put(d, n);
		}
	}
	
	public boolean isDishBeingRestocked(Dish d) {
		synchronized(dishesBeingRestocked) {
			if(dishesBeingRestocked.get(d).intValue()>0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public void setIngredientsBeingRestocked(Ingredient i, Integer n) {
		synchronized(ingredientsBeingRestocked) {
			ingredientsBeingRestocked.put(i, n);
		}
	}
	
	public void notifyRestocking(Dish d) {
		synchronized(dishesBeingRestocked) {
			dishesBeingRestocked.put(d, dishesBeingRestocked.get(d)+1);
		}
	}
	
	public void notifyRestockingFinished(Dish d) {
		synchronized(dishesBeingRestocked) {
			dishesBeingRestocked.put(d, dishesBeingRestocked.get(d)-1);
		}
	}
	
	public void notifyRestocking(Ingredient i) {
		synchronized(ingredientsBeingRestocked) {
			ingredientsBeingRestocked.put(i, ingredientsBeingRestocked.get(i)+1);
		}
	}
	
	public void notifyRestockingFinished(Ingredient i) {
		synchronized(ingredientsBeingRestocked) {
			ingredientsBeingRestocked.put(i, ingredientsBeingRestocked.get(i)-1);
		}
	}
	
	public synchronized Dish getNextDish() {
		if(!dishRestockingEnabled) {
			return null;
		}
		List<Dish> dishes = server.getDishes();
		for(Dish d : dishes) {
			Integer prognosedAmount = 0;
			Integer currentStock = 0;
			Integer amount = 0;
			synchronized(dishStock) {
				currentStock = (Integer) dishStock.get(d);
			}
			synchronized(dishesBeingRestocked) {
				amount = dishesBeingRestocked.get(d);
			}
			Integer restockAmount = (Integer) d.getRestockAmount();
			Integer futureStock = amount*restockAmount;
			prognosedAmount = currentStock + futureStock;
			
			if(prognosedAmount<(Integer) d.getRestockThreshold()) {
				Map<Ingredient, Number> recipe = d.getRecipe();
				//Iterate through recipe to check whether we have enough elements 
				for(Map.Entry<Ingredient, Number> entry : recipe.entrySet()) {
					synchronized(ingredientStock) {
						Integer weNeed = (Integer) entry.getValue() * (Integer) d.getRestockAmount();
						Integer weHave = (Integer) ingredientStock.get(entry.getKey());
						if(weNeed > weHave) {
							//Not enough ingredients
							return null;
						}
					}
				}
				
				for(Map.Entry<Ingredient, Number> entry : recipe.entrySet()) { 
					Integer currentAmount;
					synchronized(ingredientStock) {
						currentAmount = (Integer) ingredientStock.get(entry.getKey());
					}
			
					Integer weNeed = (Integer) recipe.get(entry.getKey()) * (Integer) d.getRestockAmount();
					Integer newValue = currentAmount - weNeed;
					synchronized(ingredientStock) {
						ingredientStock.put(entry.getKey(), newValue);
					}
					
				}
				//Otherwise return the dish
				return d;
			}
		}
		return null;
	}
	
	public void makeDish(Dish d) {
		synchronized(dishStock) {
			dishStock.put(d, (Integer) dishStock.get(d) + (Integer) d.getRestockAmount());
		}
	}
	
	public synchronized Ingredient getNextIngredient() {
		return null;
	}
	
	public synchronized Order getNextOrder() {
		return null;
	}

}
