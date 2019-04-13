package comp1206.sushi.common;

import com.esotericsoftware.kryo.Kryo;

public class Registration {

	public static void register(Kryo kryo) {
		kryo.register(java.lang.String.class);
		kryo.register(java.lang.Number.class);
		kryo.register(java.lang.Integer.class);
		kryo.register(java.lang.Double.class);
		kryo.register(java.util.List.class);
		kryo.register(java.util.concurrent.CopyOnWriteArrayList.class);
		kryo.register(java.util.ArrayList.class);
		kryo.register(java.util.concurrent.ConcurrentHashMap.class);
		kryo.register(Postcode.class);
		kryo.register(Message.class);
		kryo.register(MessageWithAttachement.class);
		kryo.register(User.class);
		kryo.register(MessageRegisterUser.class);
		kryo.register(MessageLogin.class);
		kryo.register(Restaurant.class);
		kryo.register(Ingredient.class);
		kryo.register(Supplier.class);
		kryo.register(Dish.class);
		kryo.register(Basket.class);
		kryo.register(MessageBasket.class);
		kryo.register(Order.class);
		kryo.register(MessageOrder.class);
	}

}
