package observerpattern;

import java.util.ArrayList;

/**
 * This class is used to call a certain callback method as defined in the 
 * observer interface whenever it's subclass calls the notifyObservers method.
 * Observers must implement the Observer interface, and define its callback
 * method. Useful to be extended by classes who don't want to have any direct
 * knowledge of other classes, but want to be notified by the publisher object
 * when a change occurs that it wants to publish.
 * @author Cathal
 *
 */
public class Publisher 
{
	private ArrayList<Observer> observers;
	
	/**
	 * Create a Publisher object with no initial observers.
	 */
	public Publisher()
	{
		this.observers = new ArrayList<>();
	}
	
	/**
	 * Add an observer to the observer list.
	 * @param o the class that implements the Observer interface
	 */
	public final void subscribe(Observer o)
	{
		this.observers.add(o);
	}
	
	/**
	 * Notify the observers of this class.
	 * Calls the onUpdate method of the observers so that they may be notified
	 * of a change or something.
	 */
	protected final void notifyObservers()
	{
		for (Observer o : this.observers)
		{
			o.onUpdate();
		}
	}
}
