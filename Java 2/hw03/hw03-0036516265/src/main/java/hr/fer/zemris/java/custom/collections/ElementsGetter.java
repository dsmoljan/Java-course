package hr.fer.zemris.java.custom.collections;

import java.util.ConcurrentModificationException;

public interface ElementsGetter {
	
	/**
	 * Checks if the collection has more elements
	 * @throws ConcurrentModificationException if the collection has been modified since the last access
	 * @return
	 */
	boolean hasNextElement();
	
	/**
	 * Gets the next element from the collection, if it exists
	 * @throws ConcurrentModificationException if the collection has been modified since the last access
	 * @return
	 */
	Object getNextElement();
	
	/**
	 * Processes all remaining elements in the collection using the given processor
	 * @param
	 */
	default void processRemaining(Processor p) {
		while (hasNextElement()) {
			p.process(getNextElement());
		}
	}

}
