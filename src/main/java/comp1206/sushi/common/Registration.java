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
		kryo.register(java.util.concurrent.ConcurrentHashMap.class);
		kryo.register(Postcode.class);
		kryo.register(Message.class);
		kryo.register(MessageWithAttachement.class);
	}

}
