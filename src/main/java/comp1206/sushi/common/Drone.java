package comp1206.sushi.common;

import comp1206.sushi.common.Drone;

public class Drone extends Model {

	private Number speed;
	private Number progress;
	
	private Number capacity;
	private Number battery;
	
	private String status;
	
	private Postcode source;
	private Postcode destination;

	public Drone(Number speed) {
		this.setSpeed(speed);
		this.setCapacity(1);
		this.setBattery(100);
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

	public Postcode getSource() {
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
	
}
