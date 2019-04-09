//Contains code from tutorial: http://silversableprog.blogspot.com/2015/11/javakryonet-poaczenie-tcp-i-udp-serwer.html

package comp1206.sushi.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;

import comp1206.sushi.common.Message;
import comp1206.sushi.common.MessageWithAttachement;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Registration;

public class CommsClient implements Runnable {

	public final static String HOST = "127.0.0.1";
	public final static int TIMEOUT = 1000;
	public final static int WRITE_BUFFER = 256 * 1024;
	public final static int READ_BUFFER = 256 * 1024;
	public final static int PORT_TCP = 56555;
	public final static int PORT_UDP = 56777;

	private ClientInterface clientInterface;
	
	private List<Postcode>postcodes;
	
	private AtomicBoolean ready = new AtomicBoolean(false);
	
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
			System.out.println("connection established");
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	class ClientListener extends Listener {
		@Override
        public void received(Connection connection, Object object) {
			if(object instanceof MessageWithAttachement) {
				MessageWithAttachement msg = (MessageWithAttachement) object;
				if(msg.toString().equals("POSTCODES")) {
					postcodes = (List<Postcode>) msg.getAttachement();

				}
				
			}
		}
	}
	
	public List<Postcode> getPostcodes() {
		return postcodes;
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

}
