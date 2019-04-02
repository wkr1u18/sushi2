package comp1206.sushi.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import comp1206.sushi.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
public class Server implements ServerInterface {

    private static final Logger logger = LogManager.getLogger("Server");
	
    private Configuration configuration;
    
	public Restaurant restaurant;
	public ArrayList<Dish> dishes = new ArrayList<Dish>();
	public HashMap<Dish, Number> dishStockLevels = new HashMap<Dish, Number>();
	public ArrayList<Drone> drones = new ArrayList<Drone>();
	public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public HashMap<Ingredient, Number> ingredientStockLevels = new HashMap<Ingredient, Number>();
	public ArrayList<Order> orders = new ArrayList<Order>();
	public ArrayList<Staff> staff = new ArrayList<Staff>();
	public ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	
	public Server() {
        logger.info("Starting up server...");
        
        configuration = new Configuration(this);
        
        //Default configuration, if not initialised it can cause null pointer exception in ServerWindo (title set up) - but we are not allowed to edit this
		Postcode restaurantPostcode = new Postcode("SO17 1BJ");
		restaurant = new Restaurant("Sushi Restaurant",restaurantPostcode);
	}
	
	@Override
	public List<Dish> getDishes() {
		return this.dishes;
	}

	@Override
	public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		Dish newDish = new Dish(name,description,price,restockThreshold,restockAmount);
		this.dishes.add(newDish);
		this.dishStockLevels.put(newDish, 0);
		this.notifyUpdate();
		return newDish;
	}
	
	@Override
	public void removeDish(Dish dish) {
		this.dishes.remove(dish);
		this.dishStockLevels.remove(dish);
		this.notifyUpdate();
	}

	@Override
	public Map<Dish, Number> getDishStockLevels() {
		return dishStockLevels;
	}
	
	@Override
	public void setRestockingIngredientsEnabled(boolean enabled) {
		
	}

	@Override
	public void setRestockingDishesEnabled(boolean enabled) {
		
	}
	
	@Override
	public void setStock(Dish dish, Number stock) {
		this.dishStockLevels.put(dish, stock);
	}

	@Override
	public void setStock(Ingredient ingredient, Number stock) {
		this.ingredientStockLevels.put(ingredient, stock);
	}

	@Override
	public List<Ingredient> getIngredients() {
		return this.ingredients;
	}

	@Override
	public Ingredient addIngredient(String name, String unit, Supplier supplier,
			Number restockThreshold, Number restockAmount, Number weight) {
		Ingredient newIngredient = new Ingredient(name,unit,supplier,restockThreshold,restockAmount,weight);
		this.ingredients.add(newIngredient);
		this.ingredientStockLevels.put(newIngredient, 0);
		this.notifyUpdate();
		return newIngredient;
	}

	public User addUser(String username, String password, String address, Postcode postcode) {
		User newUser = new User(username, password, address, postcode);
		this.users.add(newUser);
		this.notifyUpdate();
		return newUser;
	}
	
	@Override
	public void removeIngredient(Ingredient ingredient) {
		int index = this.ingredients.indexOf(ingredient);
		this.ingredients.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Supplier> getSuppliers() {
		return this.suppliers;
	}

	@Override
	public Supplier addSupplier(String name, Postcode postcode) {
		Supplier mock = new Supplier(name,postcode);
		this.suppliers.add(mock);
		return mock;
	}


	@Override
	public void removeSupplier(Supplier supplier) {
		int index = this.suppliers.indexOf(supplier);
		this.suppliers.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Drone> getDrones() {
		return this.drones;
	}

	@Override
	public Drone addDrone(Number speed) {
		Drone mock = new Drone(speed);
		this.drones.add(mock);
		return mock;
	}

	@Override
	public void removeDrone(Drone drone) {
		int index = this.drones.indexOf(drone);
		this.drones.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Staff> getStaff() {
		return this.staff;
	}

	@Override
	public Staff addStaff(String name) {
		Staff mock = new Staff(name);
		this.staff.add(mock);
		return mock;
	}

	@Override
	public void removeStaff(Staff staff) {
		this.staff.remove(staff);
		this.notifyUpdate();
	}

	@Override
	public List<Order> getOrders() {
		return this.orders;
	}

	@Override
	public void removeOrder(Order order) {
		int index = this.orders.indexOf(order);
		this.orders.remove(index);
		this.notifyUpdate();
	}
	
	@Override
	public Number getOrderCost(Order order) {
		Random random = new Random();
		return random.nextInt(100);
	}

	@Override
	public Map<Ingredient, Number> getIngredientStockLevels() {
		return ingredientStockLevels;
	}

	@Override
	public Number getSupplierDistance(Supplier supplier) {
		return supplier.getDistance();
	}

	@Override
	public Number getDroneSpeed(Drone drone) {
		return drone.getSpeed();
	}

	@Override
	public Number getOrderDistance(Order order) {
		Order mock = (Order)order;
		return mock.getDistance();
	}

	@Override
	public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
		if(quantity == Integer.valueOf(0)) {
			removeIngredientFromDish(dish,ingredient);
		} else {
			dish.getRecipe().put(ingredient,quantity);
		}
	}

	@Override
	public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
		dish.getRecipe().remove(ingredient);
		this.notifyUpdate();
	}

	@Override
	public Map<Ingredient, Number> getRecipe(Dish dish) {
		return dish.getRecipe();
	}

	@Override
	public List<Postcode> getPostcodes() {
		return this.postcodes;
	}

	@Override
	public Postcode addPostcode(String code) {
		Postcode mock = new Postcode(code);
		this.postcodes.add(mock);
		this.notifyUpdate();
		return mock;
	}
	
	public Order addOrder() {
		Order newOrder = new Order();
		return null;
	}
	
	public Order addOrder(User buyer, Map<Dish, Number> orderDetails) {
		Order newOrder = new Order(buyer, orderDetails);
		this.orders.add(newOrder);
		this.notifyUpdate();
		return newOrder;
	}
	

	@Override
	public void removePostcode(Postcode postcode) throws UnableToDeleteException {
		this.postcodes.remove(postcode);
		this.notifyUpdate();
	}

	@Override
	public List<User> getUsers() {
		return this.users;
	}
	
	@Override
	public void removeUser(User user) {
		this.users.remove(user);
		this.notifyUpdate();
	}

	@Override
	public void loadConfiguration(String filename) {
		System.out.println("Loaded configuration: " + filename);
		configuration.loadConfiguration(filename);
		restaurant = new Restaurant("Joes restaurant",new Postcode("SO17 1AW"));
		this.notifyUpdate();
	}

	@Override
	public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
		for(Entry<Ingredient, Number> recipeItem : recipe.entrySet()) {
			addIngredientToDish(dish,recipeItem.getKey(),recipeItem.getValue());
		}
		this.notifyUpdate();
	}

	@Override
	public boolean isOrderComplete(Order order) {
		return true;
	}

	@Override
	public String getOrderStatus(Order order) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Complete";
		} else {
			return "Pending";
		}
	}
	
	@Override
	public String getDroneStatus(Drone drone) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Idle";
		} else {
			return "Flying";
		}
	}
	
	@Override
	public String getStaffStatus(Staff staff) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Idle";
		} else {
			return "Working";
		}
	}

	@Override
	public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
		dish.setRestockThreshold(restockThreshold);
		dish.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
		ingredient.setRestockThreshold(restockThreshold);
		ingredient.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public Number getRestockThreshold(Dish dish) {
		return dish.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Dish dish) {
		return dish.getRestockAmount();
	}

	@Override
	public Number getRestockThreshold(Ingredient ingredient) {
		return ingredient.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Ingredient ingredient) {
		return ingredient.getRestockAmount();
	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}

	@Override
	public Postcode getDroneSource(Drone drone) {
		return drone.getSource();
	}

	@Override
	public Postcode getDroneDestination(Drone drone) {
		return drone.getDestination();
	}

	@Override
	public Number getDroneProgress(Drone drone) {
		return drone.getProgress();
	}

	@Override
	public String getRestaurantName() {
		return restaurant.getName();
	}

	@Override
	public Postcode getRestaurantPostcode() {
		return restaurant.getLocation();
	}
	
	@Override
	public Restaurant getRestaurant() {
		synchronized(restaurant) {
			return restaurant;
		}
	}
	
	/**
	 * Gives a reference to {@link Logger object}
	 * @return Logger object used by the server
	 */
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Returns a reference to a {@link Postcode} object for given String
	 * @param postcode String to be searched
	 * @return reference to {@link Postcode} object with a given name, null when not found
	 */
	public Postcode getPostcode(String postcode) {
		List<Postcode> safePostcodes = Collections.synchronizedList(postcodes);
		synchronized(safePostcodes) {
			for(Postcode p : safePostcodes) {
				if(p.getName().equals(postcode)) {
					return p;
				}
			}
		}
		return null;
		
	}

	/**
	 * Returns a reference to a {@link Supplier} object for given String
	 * @param supplier String to be searched
	 * @return reference to {@link Supplier} object with a given name, null when not found
	 */
	public Supplier getSupplier(String supplier) {
		List<Supplier> safeSuppliers = Collections.synchronizedList(suppliers);
		synchronized(safeSuppliers) {
			for(Supplier s : safeSuppliers) {
				if(s.getName().equals(supplier)) {
					return s;
				}
			}	
		}
		return null;
	}
	
	/**
	 * Returns a reference to a {@link Ingredient} object for given String
	 * @param ingredient String to be searched
	 * @return reference to {@link Ingredient} object with a given name, null when not found
	 */
	public Ingredient getIngredient(String ingredient) {
		List<Ingredient> safeIngredients = Collections.synchronizedList(ingredients);
		synchronized(safeIngredients) {
			for(Ingredient i : safeIngredients) {
				if(i.getName().equals(ingredient)) {
					return i;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a reference to a {@link Dish} object for a given String
	 * @param dish String to be searched
	 * @return reference to {@link Dish} object with a given name, null when not found
	 */
	public Dish getDish(String dish) {
		List<Dish> safeDishes = Collections.synchronizedList(dishes);
		synchronized(safeDishes) {
			for(Dish d : safeDishes) {
				if(d.getName().equals(dish)) {
					return d;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a reference to a {@link User} object for a given String
	 * @param user String to be searched
	 * @return reference to {@link Dish} object with a given name, null when not found
	 */
	public User getUser(String user) {
		List<User> safeUsers = Collections.synchronizedList(users);
		synchronized(safeUsers) {
			for(User u : safeUsers) {
				if(u.getName().equals(user)) {
					return u;
				}
			}
		}
		return null;
	}
}
