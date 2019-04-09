//Contains code from tutorial from: http://silversableprog.blogspot.com/2015/11/javakryonet-poaczenie-tcp-i-udp-serwer.html

package comp1206.sushi.server;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import comp1206.sushi.common.Message;
import comp1206.sushi.common.MessageWithAttachement;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Registration;

public class Comms implements Runnable {

	public final static int WRITE_BUFER = 256 * 1024;
	public final static int READ_BUFFER = 256 * 1024;
	public final static int PORT_TCP = 56555;
	public final static int PORT_UDP = 56777;
	
	private ServerInterface serverInterface;
	
	private AtomicBoolean ready = new AtomicBoolean(false);
	private Server server;
	private Listener listener;
	
	public Comms(ServerInterface serverInterface) {
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
	
	public synchronized void sendMessage(Message m) {
		server.sendToAllTCP(m);
	}
	
	public synchronized void sendMessageTo(Message m, int connectionID) {
		server.sendToTCP(connectionID, m);
	}
	
	public boolean isReady( ) {
		return ready.get();
	}
	
	class ServerListener extends Listener {
		@Override
		public void received(Connection connection, Object object) {
			if(object instanceof Message) {
				Message m = (Message) object;
				String contents = m.toString();
				switch(contents) {
				case "GET-POSTCODES": 
					sendPostcodes(connection.getID());
					break;
				default:
					System.out.println("not recognized");
					break;
				}
			}
		}
	}

}
