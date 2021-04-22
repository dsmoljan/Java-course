package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;

public interface ElementsGetter<T> {
	
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
	T getNextElement();
	
	/**
	 * Processes all remaining elements in the collection using the given processor
	 * @param <T>
	 * @param
	 */
	default void processRemaining(Processor<? super T> processor) {
		while (hasNextElement()) {
			processor.process(getNextElement());
		}
	}

}
