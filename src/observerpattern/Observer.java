package observerpattern;

/**
 * Defines the interface for an observer of a Publisher class.
 * Implementors must implement the onUpdate method with the statements they
 * want executed when the Publisher notifies of changes.
 * @author Cathal
 *
 */
public interface Observer 
{	
	/**
	 * Called when publisher notifies of changes.
	 */
	void onUpdate();
}
