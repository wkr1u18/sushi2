//Contains code from tutorial: http://silversableprog.blogspot.com/2015/11/javakryonet-poaczenie-tcp-i-udp-serwer.html

package comp1206.sushi.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;

import comp1206.sushi.common.Basket;
import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Message;
import comp1206.sushi.common.MessageWithAttachement;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Registration;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.User;

public class CommsClient implements Runnable {

	public final static String HOST = "127.0.0.1";
	public final static int TIMEOUT = 1000;
	public final static int WRITE_BUFFER = 256 * 1024;
	public final static int READ_BUFFER = 256 * 1024;
	public final static int PORT_TCP = 56555;
	public final static int PORT_UDP = 56777;

	private ClientInterface clientInterface;
	private List<Dish>dishes;
	private List<Postcode>postcodes;
	private List<Order>orders = new ArrayList<Order>();
	private User user;
	private Basket basket;
	private Restaurant restaurant;
	private AtomicBoolean isUserReady = new AtomicBoolean(false);
	private AtomicBoolean ready = new AtomicBoolean(false);
	private AtomicBoolean isBasketReady = new AtomicBoolean(true);
	private Client client;
	private Listener listener;
	
	public CommsClient(ClientInterface clientInterface) {
		this.clientInterface = clientInterface;
	}

	@Override
	public void run() {
		
		listener = new ClientListener();
		
		client = new Client(WRITE_BUFFER, READ_BUFFER, new KryoSerialization());
		Registration.register(client.getKryo());
		client.start();
		client.addListener(listener);
		
		try {
			client.connect(TIMEOUT, HOST, PORT_TCP, PORT_UDP);
			ready.set(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	class ClientListener extends Listener {
		@Override
        public void received(Connection connection, Object object) {
			if(object instanceof MessageWithAttachement) {
				MessageWithAttachement msg = (MessageWithAttachement) object;
				switch(msg.toString()) {
				case "BASKET":
					if(basket!=null) {
						basket = (Basket) msg.getAttachement();
						clientInterface.notifyUpdate();
					} else {
						basket = (Basket) msg.getAttachement();
						
					}
					break;
				case "DISHES":
					dishes  = (List<Dish>) msg.getAttachement();
					break;
				case "ORDERS":
					if(orders!=null) {
						orders = (List<Order>) msg.getAttachement();
						System.out.println("i got here");
						clientInterface.notifyUpdate();
					}
					else {
						orders = (List<Order>) msg.getAttachement();
					}
					break;
				case "POSTCODES":
					postcodes = (List<Postcode>) msg.getAttachement();
					break;
				case "USER":
					user = (User) msg.getAttachement();
					isUserReady.set(true);
					break;
				case "RESTAURANT":
					restaurant = (Restaurant) msg.getAttachement();
					if(restaurant==null) {
						restaurant = new Restaurant("SUSHI RESTAURANT", new Postcode("SO17 1BJ"));
					}
					break;
				}
			}
		}
	}
	
	public List<Order> getOrders() {
		return orders;
	}
	
	public Basket getBasket() {
		return basket;
	}
	
	public List<Dish> getDishes() {
		return dishes;
	}
	
	public List<Postcode> getPostcodes() {
		return postcodes;
	}
	
	public Restaurant getRestaurant() {
		return restaurant;
	}
	
	public User getUser() {
		return user;
	}
	
	public synchronized void sendMessage(Message m) {
		client.sendTCP(m);
		
	}
	
	public synchronized void sendMessage(String s) {
		sendMessage(new Message(s));
	}
	
	public boolean isReady() {
		return ready.get();
	}
	
	public boolean isUserReady() {
		return isUserReady.get();
	}
	
	public void resetUserReady() {
		isUserReady.set(false);
	}
	
	public boolean isBasketReady() {
		return isBasketReady.get();
	}
	
	public void resetBasketReady() {
		isBasketReady.set(false);
	}
	
	


}
