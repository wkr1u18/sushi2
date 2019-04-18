package comp1206.sushi.common;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

import com.esotericsoftware.kryonet.Server;

import comp1206.sushi.common.Drone;
import comp1206.sushi.server.ServerInterface;

public class Drone extends Model implements Runnable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Number speed;
	private transient Number progress;
	
	private Number capacity;
	private Number battery;
	
	private transient Random generator;
	private transient String status;
	
	private transient Postcode source;
	private transient Postcode destination;

	private volatile boolean shutdown = false;
	
	
	private transient Thread threadInstance; 
	private transient StockManagement stockManagement;
	private transient ServerInterface server;
	
	public synchronized void setThreadInstance(Thread threadInstance) {
		this.threadInstance = threadInstance;
	}
	
	public synchronized Thread getThreadInstace() {
		return this.threadInstance;
	}
	
	public void setServer(ServerInterface server) {
		this.server = server;
	}
	
	public void setStockManagement(StockManagement stockManagement) {
		this.stockManagement = stockManagement;
	}
	
	public Drone(Number speed) {
		this.setSpeed(speed);
		this.setCapacity(1);
		this.setBattery(100);
		this.setStatus("Idle");
		generator = new Random();
	}

	public Number getSpeed() {
		return speed;
	}

	
	public Number getProgress() {
		return progress;
	}
	
	public synchronized void setProgress(Number progress) {
		this.progress = progress;
	}
	
	public synchronized void setSpeed(Number speed) {
		this.speed = speed;
	}
	
	@Override
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	public synchronized Postcode getSource() {
		return source;
	}

	public synchronized void setSource(Postcode source) {
		this.source = source;
	}

	public synchronized Postcode getDestination() {
		return destination;
	}

	public synchronized void setDestination(Postcode destination) {
		this.destination = destination;
	}

	public Number getCapacity() {
		return capacity;
	}

	public synchronized void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	public Number getBattery() {
		return battery;
	}

	public synchronized void setBattery(Number battery) {
		this.battery = battery;
	}

	public synchronized String getStatus() {
		return status;
	}

	public synchronized void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}

	public void shutdown() {
		shutdown=true;
	}
	
	public void fly(Postcode source, Postcode destination, Number route){
		this.setSource(source);
		this.setDestination(destination);
		this.setStatus("Flying");
		Integer soFar = 0;
		while(soFar<route.intValue()) {
			if(battery.intValue()==0) {
				flyFrom(destination, source, soFar);
				recharge();
				soFar=0;
				this.setSource(source);
				this.setDestination(destination);
			}
			try {
				Thread.sleep(100);
				
			} catch (InterruptedException ie) {
				
			}
			Integer newBattery= battery.intValue() - generator.nextInt(2);
			if(newBattery<0) {
				newBattery=0;
			}
			battery = newBattery;
			soFar += this.getSpeed().intValue();
			int progress = Math.round(soFar*100/route.intValue());
			if(progress>100) {
				progress=100;
			}
			this.setProgress(progress);
		}
		this.setStatus("Idle");
	}
	
	public void flyFrom(Postcode source, Postcode destination, Number route) {
		this.setSource(source);
		this.setDestination(destination);
		this.setStatus("Flying");
		Integer soFar = 0;
		while(soFar<route.intValue()) {
			try {
				Thread.sleep(1000);
				
			} catch (InterruptedException ie) {
				
			}
			Integer newBattery= battery.intValue() - generator.nextInt(2);
			if(newBattery<0) {
				newBattery=0;
			}
			battery = newBattery;
			soFar += this.getSpeed().intValue();
			int progress = Math.round(soFar*100/route.intValue());
			if(progress>100) {
				progress=100;
			}
			this.setProgress(progress);
		}
		this.setStatus("Idle");
	}
	
	public void deliverOrder(Order o) {
		Postcode source = server.getRestaurantPostcode();
		Postcode destination = o.getUser().getPostcode();
		Number distance = o.getDistance();

		fly(source, destination, distance);

		flyFrom(destination, source, distance);
	}
	
	
	public void deliverIngredient(Ingredient i) {
		Supplier supplier = i.getSupplier();
		Number distance = supplier.getDistance();
		Postcode source = server.getRestaurantPostcode();
		Postcode destination = supplier.getPostcode();
		fly(source, destination, distance);
		flyFrom(destination, source, distance);
	}
	
	
	@Override
	public void run() {
		while(!shutdown) {
			if(battery.intValue()==0) {
				recharge();
			}
			Ingredient nextIngredient = stockManagement.getNextIngredient();
			if(nextIngredient!=null) {
				stockManagement.notifyRestocking(nextIngredient);
				System.out.println("Restocking: " + nextIngredient);
				deliverIngredient(nextIngredient);
				stockManagement.restockIngredient(nextIngredient);
				stockManagement.notifyRestockingFinished(nextIngredient);
			}
			
			//If we can deliver order, do it
			Order nextOrder = stockManagement.getNextOrder();
			if(nextOrder!=null)
			{
				deliverOrder(nextOrder);
				nextOrder.setStatus("Complete");
				System.out.println("delivering order");
			}
			
			//Otherwise, do nothing and wait
			this.setStatus("Idle");

		}
		
	}
	private void readObject(ObjectInputStream in) throws Exception {
		in.defaultReadObject();
		generator = new Random();
		source = null;
		destination = null;
		status="Idle";
	}
	
	public void recharge() {
		status = "Charging";
		while(battery.intValue()<100) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				
			}
			battery=battery.intValue()+generator.nextInt(20);
		}
		if(battery.intValue()>100) {
			battery=100;
		}
		
		status="Idle";
	}
	
}