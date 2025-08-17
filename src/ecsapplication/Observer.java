/**
 * Observer.java
 * This interface defines how observers should react when the Transaction subject changes state.
 * It also defines a contract for the Observer role in the Observer design pattern.
 * In the ECS system, the Observer is represented by Reminder objects, which subscribe
 * to the Transaction (Subject) updates.
 */

package ecsapplication;

public interface Observer {

	// Method that’s called by the Subject to notify observers of a change
	void update(Transaction transaction);
}
