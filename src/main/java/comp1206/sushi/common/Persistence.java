package comp1206.sushi.common;

import java.io.Serializable;
import java.util.List;

import comp1206.sushi.server.ServerInterface;

public class Persistence implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient ServerInterface server;
	
	private Restaurant restaurant;
	private List<Postcode> postcodes;
	private List<Supplier> suppliers;
	private List<Ingredient> ingredients;
	private List<Dish> dishes;
	private List<User> users;
	private StockManagement stockManagement;
	private List<Order> orders;
	private List<Drone> drones;
	private List<Staff> staff;
	
	public Persistence(ServerInterface server) {
		this.server = server;
		restaurant = server.getRestaurant();
		postcodes = server.getPostcodes();
		suppliers = server.getSuppliers();
		ingredients = server.getIngredients();
		dishes = server.getDishes();
		users = server.getUsers();
		orders = server.getOrders();
		drones = server.getDrones();
		staff = server.getStaff();
	}
	
	public void setStockManagement(StockManagement stockManagement) {
		this.stockManagement = stockManagement;
	}
	
	public Restaurant getRestaurant() {
		return this.restaurant;
	}
	
	public List<Postcode> getPostcodes() {
		return postcodes;
	}
	public List<Supplier> getSuppliers() {
		return suppliers;
	}
	
	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	
	public List<Dish> getDishes() {
		return dishes;
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public StockManagement getStockManagement() {
		return stockManagement;
	}
	
	public List<Order> getOrders() {
		return orders;
	}
	
	public List<Drone> getDrones() {
		return drones;
	}
	
	public List<Staff> getStaff() {
		return staff;
	}

}
