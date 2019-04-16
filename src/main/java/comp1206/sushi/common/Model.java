package comp1206.sushi.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base model class for other models to extend. Provides key functionality for all data classes.
 * 
 * You are not permitted to modify this model, but you can extend it.
 */
public abstract class Model implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String name;
	private transient List<UpdateListener> updateListeners = new CopyOnWriteArrayList<UpdateListener>();
	

	@Override
	public String toString() {
		return this.getName();
	}
	
	/**
	 * Return a name which identifies an instance of a given model. Can be a direct name or generated. Used in the UI for display.
	 * @return the name which identifies this instance.
	 */
	public abstract String getName();
	
	/**
	 * Set the name of a model. This notifies any listeners that the name has been updated.
	 * @param name model name
	 */
	public synchronized void setName(String name) {
		notifyUpdate("name",this.name,name);
		this.name = name;
	}
	
	/**
	 * Add a new update listener to this model.
	 * @param listener UpdateListener to add
	 */
	public void addUpdateListener(UpdateListener listener) {
		updateListeners.add(listener);
	}
	
	/**
	 * Add a set of update listeners to this model.
	 * @param listeners collection of UpdateListeners to add
	 */
	public void addUpdateListeners(Collection<UpdateListener> listeners) {
		for(UpdateListener listener : listeners) {
			addUpdateListener(listener);
		}
	}
	
	/**
	 * Notify all update listeners that this model has been updated. 
	 * Use this when you are not able to use a more specific notification.
	 */
	public void notifyUpdate() {
		//Call the updated method on every update listener
		for(UpdateListener listener : updateListeners) {
			listener.updated(new UpdateEvent(this));
		}		
	}
	
	/**
	 * Notify all update listeners that a particular property has been updated from an old value to a new value.
	 * @param property property that has been modified
	 * @param oldValue old value
	 * @param newValue new value
	 */
	public void notifyUpdate(String property, Object oldValue, Object newValue) {
		//Call the updated method on every update listener
		for(UpdateListener listener : updateListeners) {
			listener.updated(new UpdateEvent(this,property,oldValue,newValue));
		}				
	}

	
}
