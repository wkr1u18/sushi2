package comp1206.sushi.common;

import java.util.List;
import java.util.Map;
import java.util.Random;

import comp1206.sushi.common.Staff;
import comp1206.sushi.server.ServerInterface;

public class Staff extends Model implements Runnable{

	private String name;
	private String status;
	private Number fatigue;
	private volatile boolean shutdown = false;
	private StockManagement stockManagement;
	private Random generator;
	private Thread threadInstance; 
	
	public Staff(String name) {
		this.setName(name);
		this.setFatigue(0);
		generator = new Random();
	}
	
	public synchronized void setThreadInstance(Thread threadInstance) {
		this.threadInstance = threadInstance;
	}
	
	public synchronized Thread getThreadInstace() {
		return this.threadInstance;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized Number getFatigue() {
		return fatigue;
	}

	public synchronized void setFatigue(Number fatigue) {
		this.fatigue = fatigue;
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
	
	public void setStockManagement(StockManagement stockManagement) {
		this.stockManagement = stockManagement;
	}
	
	
	@Override
	public void run() {
		while(!shutdown) {
			Dish nextDish = stockManagement.getNextDish();
			
				if(nextDish!=null) {
					stockManagement.notifyRestocking(nextDish);
					this.setStatus("Restocking " + nextDish.getName());
					System.out.println(this.getName() + " is restocking " + nextDish.getName());
					try {
						Thread.sleep((generator.nextInt(40)+20)*1000);
					} catch (InterruptedException ie) {
						
					}
					stockManagement.notifyRestockingFinished(nextDish);
					stockManagement.makeDish(nextDish);
					
				}
				else {
					this.setStatus("Idle");
				}

		}
		
		
	}

}
