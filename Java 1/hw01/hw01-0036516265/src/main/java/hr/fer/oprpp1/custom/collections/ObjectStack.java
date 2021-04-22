package hr.fer.oprpp1.custom.collections;

/**
 * Class which models a stack. An adapter to the adaptee - ArrayIndexedCollection
 * @author dorian
 *
 */
public class ObjectStack {
	
	private ArrayIndexedCollection array;
	
	public ObjectStack() {
		array = new ArrayIndexedCollection();
	}
	
	/**
	 * Returns true if stack is empty.
	 * @return
	 */
	public boolean isEmpty() {
		return array.isEmpty();
	}
	
	/**
	 * Returns the number of objects in the stack.
	 * @return
	 */
	public int size() {
		return array.size();
	}
	
	/**
	 * Pushes the given value on the stack. Does not accept null values.
	 * @param value
	 */
	public void push(Object value) {
		array.add(value);
	}
	
	
	/**
	 * Removes and returns the last value pushed on stack.
	 * @return the last value pushed on stack
	 * @throws EmptyStackExceptionn if the stack is empty
	 */
	public Object pop() {
		if (this.size() == 0) {
			throw new EmptyStackException("Stack is empty!");
		}
		Object res = array.get(array.size() - 1);
		array.remove(array.size() - 1);
		return res;
	}
	
	/**
	 * Returns the last value pushed on stack, but does not remove it from stack
	 * @return the last value pushed on stack
	 * @throws EmptyStackExceptionn if the stack is empty
	 */
	public Object peek() {
		if (this.size() == 0) {
			throw new EmptyStackException("Stack is empty!");
		}
		return array.get(array.size() - 1);
	}
	
	/**
	 * Removes all elements from stack
	 */
	public void clear() {
		array.clear();
	}

}
