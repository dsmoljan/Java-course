package hr.fer.oprpp1.custom.collections;
/**
 * Class which models an apstract collection
 * @author dorian
 *
 */
public class Collection {
	
	/**
	 * default constructor
	 */
	protected Collection() {
		
	}
	
	/** 
	 * @return true if collection contains no objects and false otherwise
	 */
	public boolean isEmpty() {
		return this.size() == 0;
	}
	
	/**
	 * @return number of currently stored objects in this collection
	 */
	public int size() {
		return 0;
	}
	
	/**
	 * Adds the given object to collection
	 * @param value object we want to add to the collection
	 */
	public void add(Object value) {
		
	}
	
	/** Checks if an object is in the collection, comparision done using <code>equals</code> method
	 * @param value
	 * @return true if the collection contains given value
	 */
	public boolean contains(Object value) {
		return false;
	}
	
	
	/**
	 * Removes one occurence of the given value, comparision done using <code>equals</code> method
	 * @param value
	 * @return true if the removal is successful
	 */
	public boolean remove(Object value) {
		return false;
	}
	
	/**
	 * Allocates a new array with size equal to the size of this collection, fills it with collection content and returns the array
	 * @return never returns null
	 */
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Method calls <code>processor.process()</code> for each element of this collection. The order in which elements will be sent is undefined in this class
	 * @param processor
	 */
	public void forEach(Processor processor) {
	}
	
	/**
	 * Adds all the elements of the given collection to the current collection. The other collection remains unchanged
	 * @param other
	 */
	public void addAll(Collection other) {
		class LocalProcessor extends Processor{ //local class
			
			@Override
			public void process(Object obj) {
				add(obj);
			}
			
		}
		
		other.forEach(new LocalProcessor());
	}
	
	public void clear() {
		
	}

}
