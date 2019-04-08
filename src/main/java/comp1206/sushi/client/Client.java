package comp1206.sushi.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.UpdateListener;
import comp1206.sushi.common.User;

public class Client implements ClientInterface {

    private static final Logger logger = LogManager.getLogger("Client");
	
    Postcode postcode1 = new Postcode("SO17 1AW");
    Restaurant restaurant = new Restaurant("sushi", postcode1);
    Dish myDish = new Dish("aa", "asdasd", 23, 2, 5);
	public Client() {
        logger.info("Starting up client...");
	}
	
	@Override
	public Restaurant getRestaurant() {
		//Call server getRestaurant
		return restaurant;
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
	public User register(String username, String password, String address, Postcode postcode) {
		return new User(username, password, address, postcode);
	}

	@Override
	public User login(String username, String password) {
		
		return new User(username, password, "random address", postcode1);
	}

	@Override
	public List<Postcode> getPostcodes() {
		List<Postcode> postcodes = new ArrayList<Postcode>();
		postcodes.add(postcode1);
		return postcodes;
	}

	@Override
	public List<Dish> getDishes() {
		List<Dish>dishes = new ArrayList<Dish>();
		dishes.add(myDish);
		return dishes;
	}

	@Override
	public String getDishDescription(Dish dish) {
		return myDish.getDescription();
	}

	@Override
	public Number getDishPrice(Dish dish) {
		// TODO Auto-generated method stub
		return myDish.getPrice();
	}

	@Override
	public Map<Dish, Number> getBasket(User user) {
		Map<Dish,Number>basket = new HashMap<Dish, Number>();
		basket.put(myDish, 7);
		return basket;
	}

	@Override
	public Number getBasketCost(User user) {
		
		return 7;
	}

	@Override
	public void addDishToBasket(User user, Dish dish, Number quantity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDishInBasket(User user, Dish dish, Number quantity) {
		// TODO Auto-generated method stub

	}

	@Override
	public Order checkoutBasket(User user) {
		
		return new Order();
	}

	@Override
	public void clearBasket(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Order> getOrders(User user) {
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order());
		return orders;
	}

	@Override
	public boolean isOrderComplete(Order order) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getOrderStatus(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number getOrderCost(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelOrder(Order order) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyUpdate() {
		// TODO Auto-generated method stub

	}

}
