package comp1206.sushi.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import comp1206.sushi.common.Basket;
import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Message;
import comp1206.sushi.common.MessageBasket;
import comp1206.sushi.common.MessageLogin;
import comp1206.sushi.common.MessageRegisterUser;
import comp1206.sushi.common.MessageWithAttachement;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.UpdateEvent;
import comp1206.sushi.common.UpdateListener;
import comp1206.sushi.common.User;

public class Client implements ClientInterface {

    private static final Logger logger = LogManager.getLogger("Client");
	private CommsClient commsClient;
   
	private User user;
    private List<Postcode> postcodes;
    private List<Dish> dishes;
    private List<UpdateListener> listeners = new CopyOnWriteArrayList<UpdateListener>();
    private Basket userBasket;
    Postcode postcode1 = new Postcode("SO17 1AW");
    Restaurant restaurant;
    
    Dish myDish = new Dish("aa", "asdasd", 23, 2, 5);
	public Client() {
        logger.info("Starting up client...");
        commsClient = new CommsClient(this);
        Thread clientThread = new Thread(commsClient);
        clientThread.setName("Client");
        clientThread.setDaemon(true);
        clientThread.start();
        this.notifyUpdate();
	}
	
	@Override
	public Restaurant getRestaurant() {
		if(restaurant==null) {
			commsClient.sendMessage("GET-RESTAURANT");
			while(restaurant==null) {
				restaurant=commsClient.getRestaurant();
			}
		}
		return restaurant;
	}
	
	@Override
	public String getRestaurantName() {
		if(restaurant==null) {
			getRestaurant();
		}
		return restaurant.getName();
	}

	@Override
	public Postcode getRestaurantPostcode() {
		if(restaurant==null) {
			getRestaurant();
		}
		return restaurant.getLocation();
	}
	
	@Override
	public User register(String username, String password, String address, Postcode postcode) {
		MessageRegisterUser msg = new MessageRegisterUser(username, password, address, postcode);
		commsClient.sendMessage(msg);
		while(!commsClient.isUserReady()) {
		}
		user = commsClient.getUser();
		commsClient.resetUserReady();
		return user;
		
	}

	@Override
	public User login(String username, String password) {
		MessageLogin msg = new MessageLogin(username, password);
		commsClient.sendMessage(msg);
		
		while(!commsClient.isUserReady()) {
		}
		user = commsClient.getUser();
		commsClient.resetUserReady();
		return user;
	}

	@Override
	public synchronized List<Postcode> getPostcodes() {
		if(postcodes==null) {
			commsClient.sendMessage("GET-POSTCODES");
			while(postcodes==null) {
				postcodes = commsClient.getPostcodes();
			}
		} else {
			postcodes=commsClient.getPostcodes();
		}
		return postcodes;
	}

	@Override
	public List<Dish> getDishes() {
		if(dishes==null) {
			commsClient.sendMessage("GET-DISHES");
			while(dishes==null) {
				dishes = commsClient.getDishes();
			}
		} else {
			dishes=commsClient.getDishes();
		}
		return dishes;
	}

	@Override
	public String getDishDescription(Dish dish) {
		if(dishes==null) {
			getDishes();
		}
		return dish.getDescription();
	}

	@Override
	public Number getDishPrice(Dish dish) {
		if(dishes==null) {
			getDishes();
		}
		return dish.getPrice();
	}

	@Override
	public synchronized Map<Dish, Number> getBasket(User user) {
		if(userBasket==null) {
			commsClient.sendMessage("GET-BASKET");
			do {
				userBasket=commsClient.getBasket();	
			} while (userBasket==null);
		}
		return userBasket.getContents();
	}

	@Override
	public Number getBasketCost(User user) {
		getBasket(user);
		return userBasket.getBasketCost();
	}

	@Override
	public void addDishToBasket(User user, Dish dish, Number quantity) {
		if(userBasket==null) {
			getBasket(user);
		}
		userBasket.addDishToBasket(dish, quantity);
		Message m = new MessageBasket("ADD-DISH",dish.getName(), quantity);
		commsClient.sendMessage(m);
		//userBasket.addDishToBasket(dish, quantity);

	}

	@Override
	public void updateDishInBasket(User user, Dish dish, Number quantity) {
		if(userBasket==null) {
			getBasket(user);
		}
		userBasket.updateDishInBasket(dish, quantity);
		Message m = new MessageBasket("ADD-DISH",dish.getName(), quantity);
		commsClient.sendMessage(m);
	}

	@Override
	public Order checkoutBasket(User user) {
		Message m = new Message("CHECKOUT-BASKET");
		commsClient.sendMessage(m);
		return userBasket.checkoutBasket(user);
	}

	@Override
	public void clearBasket(User user) {
		if(userBasket==null) {
			getBasket(user);
		}
		Message m = new Message("CLEAR-BASKET");
		userBasket.clearBasket();
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
		this.listeners.add(listener);

	}

	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));

	}

}
