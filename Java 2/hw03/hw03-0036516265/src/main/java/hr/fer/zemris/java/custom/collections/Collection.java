package hr.fer.zemris.java.custom.collections;
/**
 * Interface which models an apstract collection
 * @author dorian
 *
 */
public interface Collection {
	
	/** 
	 * @return true if collection contains no objects and false otherwise
	 */
	default boolean isEmpty() {
		return this.size() == 0;
	}
	
	/**
	 * @return number of currently stored objects in this collection
	 */
	int size();
	
	/**
	 * Adds the given object to collection
	 * @param value object we want to add to the collection
	 */
	void add(Object value);
	
	/** Checks if an object is in the collection, comparision done using <code>equals</code> method
	 * @param value
	 * @return true if the collection contains given value
	 */
	boolean contains(Object value);
	
	
	/**
	 * Removes one occurence of the given value, comparision done using <code>equals</code> method
	 * @param value
	 * @return true if the removal is successful
	 */
	boolean remove(Object value);
	
	/**
	 * Allocates a new array with size equal to the size of this collection, fills it with collection content and returns the array
	 * @return never returns null
	 */
	Object[] toArray();
	
	/**
	 * Method calls <code>processor.process()</code> for each element of this collection. The order in which elements will be sent is undefined in this class
	 * @param processor
	 */
	default void forEach(Processor processor) {
		ElementsGetter getter = this.createElementsGetter();
		getter.processRemaining(processor);
	}
	
	/**
	 * Adds all the elements of the given collection to the current collection. The other collection remains unchanged
	 * @param other
	 */
	default void addAll(Collection other) {
		class LocalProcessor implements Processor{ //local class
			
			@Override
			public void process(Object obj) {
				add(obj);
			}
			
		}
		other.forEach(new LocalProcessor());
	}
	
	/**
	 * Removes all objects in this collection
	 */
	public void clear();
	
	/**
	 * Creates a new ElementsGetter for this collection
	 * @return
	 */
	public ElementsGetter createElementsGetter();
	
	default void addAllSatisfying(Collection col, Tester tester) {
		col.forEach(new Processor() {

			@Override
			public void process(Object obj) {
				if (tester.test(obj)) {
					add(obj);
				}
			}
		});
		
		//alterantivni način na koji smo mogli napisati ovo gore
		/*col.forEach(p -> { //p je parametar koji se proslijeđuje metodi process iz foreach
			if (tester.test(p)) {
				add(p);
			}
		}); */
	}

}
