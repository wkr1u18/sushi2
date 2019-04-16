//Contains code from tutorial from: http://silversableprog.blogspot.com/2015/11/javakryonet-poaczenie-tcp-i-udp-serwer.html

package comp1206.sushi.server;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import comp1206.sushi.common.Basket;
import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Message;
import comp1206.sushi.common.MessageBasket;
import comp1206.sushi.common.MessageLogin;
import comp1206.sushi.common.MessageOrder;
import comp1206.sushi.common.MessageRegisterUser;
import comp1206.sushi.common.MessageWithAttachement;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Registration;
import comp1206.sushi.common.UpdateEvent;
import comp1206.sushi.common.UpdateListener;
import comp1206.sushi.common.User;

public class Comms implements Runnable{

	public final static int WRITE_BUFER = 256 * 1024;
	public final static int READ_BUFFER = 256 * 1024;
	public final static int PORT_TCP = 56555;
	public final static int PORT_UDP = 56777;
	
	private comp1206.sushi.server.Server serverInterface;

	
	private AtomicBoolean ready = new AtomicBoolean(false);
	private Server server;
	private Listener listener;
	
	public Comms(comp1206.sushi.server.Server serverInterface) {
		this.serverInterface = serverInterface;
	}

	@Override
	public void run() {
		listener = new ServerListener();
		server = new Server(WRITE_BUFER, READ_BUFFER, new KryoSerialization());
		Registration.register(server.getKryo());
		
		server.addListener(listener);
		server.start();
		try {
			server.bind(PORT_TCP, PORT_UDP);
			ready.set(true);
			System.out.println("Server has started");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public synchronized void sendPostcodes(int connectionID) {
		List<Postcode> postcodes = serverInterface.getPostcodes();
		MessageWithAttachement msg = new MessageWithAttachement("POSTCODES", postcodes);
		sendMessageTo(msg, connectionID);
	}
	
	public MessageWithAttachement makeDishesMessage() {
		MessageWithAttachement msg = new MessageWithAttachement("DISHES", serverInterface.getDishes());
		return msg;
	}
	
	public synchronized void sendDishes(int connectionID) {
		MessageWithAttachement msg = makeDishesMessage();
		sendMessageTo(msg, connectionID);
	}
	
	public synchronized void broadcastDishes() {
		MessageWithAttachement msg = makeDishesMessage();
		sendMessage(msg);
	}
	
	public synchronized void sendUser(User user, int connectionID) {
		MessageWithAttachement msg = new MessageWithAttachement("USER", user);
		sendMessageTo(msg, connectionID);
	}
	
	public synchronized void sendRestaurant(int connectionID) {
		MessageWithAttachement msg = new MessageWithAttachement("RESTAURANT", serverInterface.getRestaurant());
		sendMessageTo(msg, connectionID);
	}
	
	public synchronized void sendMessage(Message m) {
		server.sendToAllTCP(m);
	}
	
	public synchronized void sendMessageTo(Message m, int connectionID) {
		server.sendToTCP(connectionID, m);
	}
	
	public boolean isReady( ) {
		return ready.get();
	}
	
	public synchronized void sendBasket(int userId) {
		User basketOwner = serverInterface.getUser(userId);
		Basket userBasket = serverInterface.getBasket(basketOwner);
		MessageWithAttachement basketReply = new MessageWithAttachement("BASKET", userBasket);
		sendMessageTo(basketReply, userId);
	}
	
	public synchronized void clearBasket(int userId) {
		User basketOwner = serverInterface.getUser(userId);
		Basket userBasket = serverInterface.getBasket(basketOwner);
		userBasket.clearBasket();
	}
	
	public synchronized void checkoutBasket(int userId) {
		User basketOwner = serverInterface.getUser(userId);
		Basket userBasket = serverInterface.getBasket(basketOwner);
		serverInterface.addOrder(basketOwner, userBasket.getContents());
		userBasket.clearBasket();
		//serverInterface.notifyUpdate();
	}
	
	public synchronized void addDishToBasket(Message m, int id) {
		MessageBasket messageBasket = (MessageBasket) m;
		User basketOwner = serverInterface.getUser(id);
		Basket usersBasket = serverInterface.getBasket(basketOwner);
		Dish dishToBeAdded = serverInterface.getDish(messageBasket.getDish());
		usersBasket.addDishToBasket(dishToBeAdded, messageBasket.getAmount());
	}
	
	public synchronized void updateDishInBasket(Message m, int id) {
		MessageBasket messageBasket = (MessageBasket) m;
		User basketOwner = serverInterface.getUser(id);
		Basket usersBasket = serverInterface.getBasket(basketOwner);
		Dish dishToBeAdded = serverInterface.getDish(messageBasket.getDish());
		usersBasket.updateDishInBasket(dishToBeAdded, messageBasket.getAmount());
	}
	
	public synchronized void cancelOrder(Message m) {
		MessageOrder messageOrder = (MessageOrder) m;
		Order order = serverInterface.getOrder(messageOrder.getUsername(), messageOrder.getOrderName());
		if(order!=null) {
			order.cancelOrder();
		}
		else {
			System.out.println("NO SUCH ORDER");
		}
	}
	
	public synchronized void sendOrders(int connectionId) {
		User user = serverInterface.getUser(connectionId);
		List<Order> ordersToBeSent = new ArrayList<Order>();
		for(Order o : serverInterface.getOrders()) {
			if(o.getUser().equals(user)) {
				ordersToBeSent.add(o);
			}
		}
		MessageWithAttachement msg = new MessageWithAttachement("ORDERS", ordersToBeSent);
		sendMessageTo(msg, connectionId);
	}
	
	
	class ServerListener extends Listener {
		@Override
		public void disconnected(Connection connection) {
			Integer id = connection.getID();
			List<User>users = serverInterface.getUsers();
			for(User u : users) {
				if(id.equals(u.getConnectionId())) {
					u.setConnectionId(null);
					serverInterface.removeBasket(u);
				}
			}
		}
		
		@Override
		public void received(Connection connection, Object object) {
			if(object instanceof Message) {
				Message m = (Message) object;
				String contents = m.toString();
				switch(contents) {
				case "CANCEL-ORDER":
					cancelOrder(m);
					break;
				case "CHECKOUT-BASKET":
					checkoutBasket(connection.getID());
					break;
				case "GET-ORDERS":
					sendOrders(connection.getID());
					break;
				case "CLEAR-BASKET":
					clearBasket(connection.getID());
					break;
				case "GET-BASKET":
					sendBasket(connection.getID());
					break;
				case "UPDATE-DISH":
					updateDishInBasket(m, connection.getID());
					break;
				case "ADD-DISH":
					addDishToBasket(m, connection.getID());
					break;
				case "GET-DISHES":
					sendDishes(connection.getID());
					break;
				case "GET-RESTAURANT":
					sendRestaurant(connection.getID());
					break;
				case "GET-POSTCODES": 
					sendPostcodes(connection.getID());
					break;
				case "REGISTER-USER":
					MessageRegisterUser mru = (MessageRegisterUser) m;
					User userObject = serverInterface.addUser(mru.getName(), mru.getPassword(), mru.getAddress(), mru.getPostcode());
					if(userObject!=null) {
						userObject.setConnectionId(connection.getID());
					}
					serverInterface.createBasket(userObject);
					sendUser(userObject, connection.getID());
					break;
				case "LOGIN":
					MessageLogin ml = (MessageLogin) m;
					User loginUser = serverInterface.getUser(ml.getUsername());
					User loginResponse = null;
					if(loginUser!=null) {
						if(loginUser.verify(ml.getPassword())) {
							if(loginUser.getConnectionId()==null) {
								loginUser.setConnectionId(connection.getID());
								loginResponse = loginUser;
							}
						}
					}
					serverInterface.createBasket(loginUser);
					sendUser(loginResponse, connection.getID());
					sendOrders(connection.getID());
					break;
				default:
					System.out.println("not recognized");
					break;
				}
			}
		}
	}

	public void update() {
		List<User> users = serverInterface.getUsers();
		for(User u : users) {
			Integer connectionId = u.getConnectionId();
			if(connectionId!=null) {
				sendOrders(connectionId);
				sendDishes(connectionId);
			}
		}
		
		
	}


}
