package hr.fer.oprpp1.custom.collections;

/**
 * Class which models a stack. An adapter to the adaptee - ArrayIndexedCollection
 * @author dorian
 *
 */
public class ObjectStack<T> {
	
	private ArrayIndexedCollection<T> array;
	
	public ObjectStack() {
		array = new ArrayIndexedCollection<>();
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
	public void push(T value) {
		array.add(value);
	}
	
	
	/**
	 * Removes and returns the last value pushed on stack.
	 * @return the last value pushed on stack
	 * @throws EmptyStackExceptionn if the stack is empty
	 */
	public T pop() {
		if (this.size() == 0) {
			throw new EmptyStackException("Stack is empty!");
		}
		T res = array.get(array.size() - 1);
		array.remove(array.size() - 1);
		return res;
	}
	
	/**
	 * Returns the last value pushed on stack, but does not remove it from stack
	 * @return the last value pushed on stack
	 * @throws EmptyStackExceptionn if the stack is empty
	 */
	public T peek() {
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
