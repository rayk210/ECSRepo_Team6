/**
 * Subject.java
 * This interface defines the contract specifying the methods that
 * the Subject role (Transaction) must implement in the Observer design pattern.
 * The Subject maintains a list of observers and provides methods to register,
 * remove, and notify observers of a change in the state of transactions.
 */

package ecsapplication;

public interface Subject {
	
	// Add observer to the list
	void registerObserver(Observer observer);
	
	// Remove observers from the list
	void removeObserver(Observer observer);
	
	// Notify registered observers (Reminder) of a change in state
	void notifyObservers();
}
