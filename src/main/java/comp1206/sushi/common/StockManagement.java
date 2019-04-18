package comp1206.sushi.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import comp1206.sushi.server.ServerInterface;

public class StockManagement implements Runnable, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Dish, Number> dishStock;
	private Map<Ingredient,Number> ingredientStock;
	private Map<Dish, Integer> dishesBeingRestocked;
	private transient Map<Ingredient, Integer> ingredientsBeingRestocked;
	private List<OrderCollector> orderCollectors;
	private transient Thread threadInstance;
	private volatile boolean shutdown = false;
	
	
	private transient ServerInterface server;
	private volatile boolean dishRestockingEnabled;
	private volatile boolean ingredientsRestockingEnabled;
	
	public StockManagement(ServerInterface server) {
		
		dishStock = Collections.synchronizedMap(new HashMap<Dish, Number>());
		ingredientStock = Collections.synchronizedMap(new HashMap<Ingredient, Number>());
		dishesBeingRestocked = Collections.synchronizedMap(new HashMap<Dish, Integer>());
		ingredientsBeingRestocked = Collections.synchronizedMap(new HashMap<Ingredient, Integer>());
		orderCollectors = Collections.synchronizedList(new ArrayList<OrderCollector>());
		dishRestockingEnabled = true;
		ingredientsRestockingEnabled = true;
		this.server = server;
	}
	
	public void initialise(ServerInterface server) {
		this.server = server;

//		dishStock = Collections.synchronizedMap(dishStock);
//		ingredientStock = Collections.synchronizedMap(ingredientStock);

		for(Order o : server.getOrders()) {
			if(o.getStatus().equals("Placed")) {
				trackOrder(o);
			}
		}
		
		synchronized(dishesBeingRestocked) {
			for(Map.Entry<Dish, Integer> entry : dishesBeingRestocked.entrySet()) {
				Integer amountRestocked = entry.getValue();
				if(amountRestocked>0) {
					Dish currentDish = entry.getKey();
					System.out.println(currentDish + " was being made while crash");
					Map<Ingredient, Number> recipe = currentDish.getRecipe();
					for(Map.Entry<Ingredient, Number>recipeEntry : recipe.entrySet()) {
						Integer amountToGiveBack = recipeEntry.getValue().intValue()*amountRestocked;
						Ingredient ingredientToGiveBack = recipeEntry.getKey();
						System.out.println("We need to give back: " + amountToGiveBack + " of: " + ingredientToGiveBack);
						synchronized(ingredientStock) {
							Integer soFar = ingredientStock.get(ingredientToGiveBack).intValue();
							Integer newValue = soFar + amountToGiveBack;
							ingredientStock.put(ingredientToGiveBack, newValue);
						}
					}
				}
			}
		}
		
		ingredientsBeingRestocked = Collections.synchronizedMap(new HashMap<Ingredient, Integer>());
		dishesBeingRestocked = Collections.synchronizedMap(new HashMap<Dish, Integer>());
		
		List<Ingredient> ingredients = server.getIngredients();
		for(Ingredient i : ingredients) {
			synchronized(ingredientsBeingRestocked) {
				ingredientsBeingRestocked.put(i, 0);
			}
		}
		
		List<Dish> dishes = server.getDishes();
		for(Dish d : dishes) {
			synchronized(dishesBeingRestocked) {
				dishesBeingRestocked.put(d, 0);
			}
		}
	}
	
	public synchronized void setThreadInstance(Thread threadInstance) {
		this.threadInstance = threadInstance;
	}
	
	public synchronized Thread getThreadInstace() {
		return this.threadInstance;
	}
	
	public void shutdown() {
		shutdown=true;
	}
	
	public void trackOrder(Order o) {
		synchronized(orderCollectors) {
			orderCollectors.add(new OrderCollector(o));
		}
	}
	
	public void untrackOrder(Order o ) {
		System.out.println("Untracking order");
		OrderCollector orderCollector = getOrderCollector(o);
		synchronized(orderCollectors) {
			orderCollectors.remove(orderCollector);
		}
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
	
	public Map<Dish,Number> getDishStockLevels() {
		synchronized(dishStock) {
			return dishStock;
		}
	}
	
	public Map<Ingredient, Number> getIngredientStockLevels() {
		synchronized(ingredientStock) {
			return ingredientStock;
		}
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
	
	public synchronized Dish checkDish(Dish d) {
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
		return null;
	}
	
	public synchronized Dish getNextDish() {
		if(!dishRestockingEnabled) {
			return null;
		}
		List<Dish> dishes = server.getDishes();
		for(Dish d : dishes) {
			Dish result = checkDish(d);
			if(result!=null) {
				return result;
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
		if(!ingredientsRestockingEnabled) {
			return null;
		}
		
		List<Ingredient> ingredients = server.getIngredients();
		for(Ingredient i : ingredients) {
			Integer prognosedAmount = 0;
			Integer currentStock = 0;
			Integer amount = 0;
			
			synchronized(ingredientStock) {
				currentStock = (Integer) ingredientStock.get(i).intValue();
			}
			
			synchronized(ingredientsBeingRestocked) {
				amount = (Integer) ingredientsBeingRestocked.get(i).intValue();
			}
			
			Integer restockAmount = (Integer) i.getRestockAmount().intValue();
			Integer futureStock = amount * restockAmount;
			prognosedAmount = currentStock + futureStock;
			if(prognosedAmount<i.getRestockThreshold().intValue()) {
				return i;
			}
		}
		
		return null;
	}
	
	public void restockIngredient(Ingredient i) {
		synchronized(ingredientStock) {
			ingredientStock.put(i, ingredientStock.get(i).intValue() + i.getRestockAmount().intValue());
		}
	}
	
	public synchronized Order getNextOrder() {
		List<Order>orders = server.getOrders();
		for(Order o : orders) {
			if(o.getStatus()!=null) {
				if(o.getStatus().equals("Collected")) {
					o.setStatus("Delivering");
					return o;
				}
			}
		}
		return null;
	}
	
	public OrderCollector getOrderCollector(Order o) {
		synchronized(orderCollectors) {
			for(OrderCollector oc : orderCollectors) {
				if(oc.getOrder()!=null) {
					if(oc.getOrder().equals(o)) {
						return oc;
					}
				}
			}
		}
		return null;
	}
	
	public synchronized void cancelOrder(Order o) {
		o.setStatus("Cancelling");
		OrderCollector orderInformation = getOrderCollector(o);
		untrackOrder(o);
		for(Map.Entry<Dish, Number> entry : orderInformation.getCollectedSoFar().entrySet()) {
			synchronized(dishStock) {
				Integer current = dishStock.get(entry.getKey()).intValue();
				Integer newValue = current + entry.getValue().intValue();
				dishStock.put(entry.getKey(), newValue);	
			}
		}
		o.setStatus("Cancelled");
	}

	@Override
	public void run() {
		
		while(!shutdown) {
			
			synchronized(orderCollectors) {
				for(OrderCollector oc : orderCollectors) {
					Order currentOrder = oc.getOrder();
					if(currentOrder.getStatus().equals("Placed")) {
						Map<Dish, Number> details = currentOrder.getOrderDetails();
						Map<Dish, Number> soFar = oc.getCollectedSoFar();
						for(Map.Entry<Dish, Number> entry : details.entrySet()) {
							Dish currentDish = entry.getKey();
							
							Integer weNeed = entry.getValue().intValue();
							Integer weHaveCollected = soFar.get(entry.getKey()).intValue();
							Integer weHaveToTake = weNeed - weHaveCollected;
							if(weHaveToTake>0) {
								Integer currentAmountOfDish;
								synchronized(dishStock) {
									currentAmountOfDish = dishStock.get(currentDish).intValue();
								}
								if(currentAmountOfDish>0) {
									if(currentAmountOfDish>=weHaveToTake) {
										System.out.println("We have enough - no need to put more of this dish");
										System.out.println("Taking " + weHaveToTake + " of " + currentDish);
										synchronized(dishStock) {
											dishStock.put(currentDish, dishStock.get(currentDish).intValue()-weHaveToTake);
										}
										soFar.put(currentDish, weNeed);
										server.notifyUpdate();
									}
									else {
										System.out.println("Too few too fill at once, taking only few");
										System.out.println("Taking " + currentAmountOfDish + " of " + currentDish);
										synchronized(dishStock) {
											dishStock.put(currentDish, dishStock.get(currentDish).intValue()-currentAmountOfDish);
										}
										soFar.put(currentDish, currentAmountOfDish);
									}
									//this was here
								}
							}
							if(weHaveToTake==0) {
								//Now it is here
								if(soFar.equals(details)) {
									System.out.println("Done, now setting status");
									currentOrder.setStatus("Collected");
								} else {
									System.out.println("Collected so far: " + soFar);
									System.out.println("We need: " + details);
								}
							}
						}
					}
				}
			}
		}
		
	}

}
