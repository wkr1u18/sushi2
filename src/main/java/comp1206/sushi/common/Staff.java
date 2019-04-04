package comp1206.sushi.common;

import comp1206.sushi.common.Staff;

public class Staff extends Model {

	private String name;
	private String status;
	private Number fatigue;
	
	public Staff(String name) {
		this.setName(name);
		this.setFatigue(0);
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

}
