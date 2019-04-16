package comp1206.sushi.server;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import comp1206.sushi.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
public class Server implements ServerInterface, UpdateListener {

    private static final Logger logger = LogManager.getLogger("Server");
	
    private Configuration configuration;
    
	public Restaurant restaurant;
	private StockManagement stockManagement;

	public List<Dish> dishes = new CopyOnWriteArrayList<Dish>();
	public List<Drone> drones = new CopyOnWriteArrayList<Drone>();
	public List<Ingredient> ingredients = new CopyOnWriteArrayList<Ingredient>();
	public Map<User, Basket> baskets = new ConcurrentHashMap<User, Basket>();
	public List<Order> orders = new CopyOnWriteArrayList<Order>();
	public List<Staff> staff = new CopyOnWriteArrayList<Staff>();
	public List<Supplier> suppliers = new CopyOnWriteArrayList<Supplier>();
	public List<User> users = new CopyOnWriteArrayList<User>();
	public List<Postcode> postcodes = new CopyOnWriteArrayList<Postcode>();
	private List<UpdateListener> listeners = new CopyOnWriteArrayList<UpdateListener>();
	private Comms server;
	
	private void clear() {
		stockManagement.shutdown();
		stockManagement.getThreadInstace().stop();
		for(Staff s : staff) {
			s.shutdown();
			s.getThreadInstace().stop();
		}
		for(Drone d : drones) {
			d.shutdown();
			d.getThreadInstace().stop();
		}
		stockManagement = new StockManagement(this); 
        Thread stockManagementThread = new Thread(stockManagement);
        stockManagement.setThreadInstance(stockManagementThread);
        stockManagementThread.start();
		dishes.clear();
		drones.clear();
		ingredients.clear();
		orders.clear();
		staff.clear();
		suppliers.clear();		
		users.clear();
		postcodes.clear();
		listeners.clear();
		baskets.clear();

	}
	
	public Server() {
        logger.info("Starting up server...");
        
        configuration = new Configuration(this);
        stockManagement = new StockManagement(this);
        
        Thread stockManagementThread = new Thread(stockManagement);
        stockManagement.setThreadInstance(stockManagementThread);
        stockManagementThread.start();
        
        //Default configuration, if not initialised it can cause null pointer exception in ServerWindow (title set up) - but we are not allowed to edit this
		Postcode restaurantPostcode = new Postcode("SO17 1BJ");
		restaurant = new Restaurant("Sushi Restaurant",restaurantPostcode);
		
		server = new Comms(this);
		Thread serverThread = new Thread(server);
		serverThread.setName("Server");
		serverThread.run();
		
		while(!server.isReady()) {
			Thread.yield();
		}
	}
	

	
	@Override
	public List<Dish> getDishes() {
		return this.dishes;
	}

	@Override
	public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		Dish newDish = new Dish(name,description,price,restockThreshold,restockAmount);
		this.dishes.add(newDish);
		stockManagement.setStock(newDish, 0);
		stockManagement.setDishesBeingRestocked(newDish, 0);
		this.notifyUpdate();
		return newDish;
	}
	
	@Override
	public void removeDish(Dish dish) throws UnableToDeleteException {
		if(stockManagement.isDishBeingRestocked(dish)) {
			throw new UnableToDeleteException("Cannot delete dish which is currently being restocked");
		}
		this.dishes.remove(dish);
		stockManagement.remove(dish);
		this.notifyUpdate();
	}

	@Override
	public Map<Dish, Number> getDishStockLevels() {
		return stockManagement.getDishStockLevels();
	}
	
	@Override
	public void setRestockingIngredientsEnabled(boolean enabled) {
		stockManagement.setRestockingIngredientsEnable(enabled);
	}

	@Override
	public void setRestockingDishesEnabled(boolean enabled) {
		stockManagement.setRestockingDishesEnable(enabled);
	}
	
	@Override
	public void setStock(Dish dish, Number stock) {
		stockManagement.setStock(dish, stock);
		this.notifyUpdate();
	}

	@Override
	public void setStock(Ingredient ingredient, Number stock) {
		stockManagement.setStock(ingredient, stock);
		this.notifyUpdate();
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
		stockManagement.setStock(newIngredient, 0);
		stockManagement.setIngredientsBeingRestocked(newIngredient, 0);
		this.notifyUpdate();
		return newIngredient;
	}

	public User addUser(String username, String password, String address, Postcode postcode) {
		if(getUser(username)==null) {
			User newUser = new User(username, password, address, postcode);
			this.users.add(newUser);
			System.out.println(users);
			this.notifyUpdate();
			return newUser;
		} else {
			return null;
		}
	}
	
	@Override
	public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException{
		for(Dish d : dishes) {
			if(d.getRecipe().containsKey(ingredient)) {
				throw new UnableToDeleteException("Cannot delete ingredient used in a dish");
			}
		}
		this.ingredients.remove(ingredient);
		stockManagement.remove(ingredient);
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
		mock.setStockManagement(stockManagement);
		mock.setServer(this);
		Thread newWorker = new Thread(mock);
		mock.setThreadInstance(newWorker);
		newWorker.start();
		return mock;
	}

	@Override
	public void removeDrone(Drone drone) throws UnableToDeleteException{
		if(!drone.getStatus().equals("Idle")) {
			throw new UnableToDeleteException("You can't delete flying drone");
		}
		drone.shutdown();
		this.drones.remove(drone);
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
		mock.setStockManagement(stockManagement);
		Thread newWorker = new Thread(mock);
		mock.setThreadInstance(newWorker);
		newWorker.start();
		return mock;
	}

	@Override
	public void removeStaff(Staff staff) throws UnableToDeleteException {
		if(!staff.getStatus().equals("Idle")) {
			throw new UnableToDeleteException("Cannot delete staff who is currently working");
		}
		staff.shutdown();
		this.staff.remove(staff);
		this.notifyUpdate();
	}

	@Override
	public List<Order> getOrders() {
		return this.orders;
	}

	@Override
	public void removeOrder(Order order) throws UnableToDeleteException {
		if(order.getStatus()!=null) {
			if(!order.getStatus().equals("Delivering")) {
				if(order.getStatus().equals("Placed")||order.getStatus().equals("Collected")) {
					cancelOrder(order);
				}
				this.orders.remove(order);
				stockManagement.untrackOrder(order);
				this.notifyUpdate();
				return;
			}
		}
		throw new UnableToDeleteException("Cannot delete order while delivering it");
	}
	
	@Override
	public Number getOrderCost(Order order) {
		return order.getCost();
	}

	@Override
	public Map<Ingredient, Number> getIngredientStockLevels() {
		return stockManagement.getIngredientStockLevels();
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
		return order.getDistance();
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
		Postcode mock = new Postcode(code, restaurant);
		this.postcodes.add(mock);
		this.notifyUpdate();
		return mock;
	}
	
	public Order addOrder() {
		return null;
	}
	
	public Order addOrder(User buyer, Map<Dish, Number> orderDetails) {
		Order newOrder = new Order(buyer, orderDetails);
		this.orders.add(newOrder);
		stockManagement.trackOrder(newOrder);
		this.notifyUpdate();
		newOrder.addUpdateListener(this);
		return newOrder;
	}
	

	@Override
	public void removePostcode(Postcode postcode) throws UnableToDeleteException {
		for(Supplier s : suppliers) {
			if(s.getPostcode().equals(postcode)) {
				throw new UnableToDeleteException("Postcode used by supplier");
			}
		}
		for(User u : users) {
			if(u.getPostcode().equals(postcode)) {
				throw new UnableToDeleteException("Postcode used by user");
			}
		}
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
		this.clear();
		System.out.println("Loaded configuration: " + filename);
		configuration.loadConfiguration(filename);
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
		if(order.getStatus().equals("Complete")) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String getOrderStatus(Order order) {
		return order.getStatus();
	}
	
	@Override
	public String getDroneStatus(Drone drone) {
		return drone.getStatus();
	}
	
	@Override
	public String getStaffStatus(Staff staff) {
		return staff.getStatus();
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
		try {
			this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
		} catch (NullPointerException npe) {
			
		}
		server.update();
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
		return restaurant;
	}
	
	
	public Basket getBasket(User user) {
		return baskets.get(user);
	}
	
	public void createBasket(User user) {
		baskets.put(user, new Basket());
	}
	
	public void removeBasket(User user) {
		baskets.remove(user);
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
		for(Postcode p : postcodes) {
			if(p.getName().equals(postcode)) {
				return p;
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
		for(Supplier s : suppliers) {
			if(s.getName().equals(supplier)) {
				return s;
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

		for(Ingredient i : ingredients) {
			if(i.getName().equals(ingredient)) {
				return i;
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

		for(Dish d : dishes) {
			if(d.getName().equals(dish)) {
				return d;
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
		System.out.println("Searching for user named: " + user);
		for(User u : users) {
			System.out.println("checking user: " + u.getName());
			if(u.getName().equals(user)) {
				return u;
			}
		}
		System.out.println("returning null");
		return null;
	}
	
	public User getUser(int connectionId) {
		for(User u : users) {
			if(u.getConnectionId()!=null) {
				if(u.getConnectionId().equals(connectionId)) {
					return u;
				}
			}
		}
		return null;
	}
	
	public Order getOrder(String username, String orderName) {
		for(Order o : orders) {
			if(o.getName().equals(orderName)&&o.getUser().getName().equals(username)) {
				return o;
			}
		}
		return null;
	}

	@Override
	public void updated(UpdateEvent updateEvent) {
		this.notifyUpdate();
		
	}
	
	public void cancelOrder(Order o) {
		stockManagement.cancelOrder(o);
		
	}

}
