package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Class which models an indexed array. Duplicate elements are allowed, null references are not allowed
 */
public class ArrayIndexedCollection<T> implements List<T>{
	
	private int size;
	private T[] elements;
	private long modificationCount = 0;
	
	/**
	 * Default constructor. Array capacity is 16 elements
	 */
	@SuppressWarnings("unchecked")
	public ArrayIndexedCollection() {  //default constructor
		super();
		elements = (T[])new Object[16];
	};
	
	@SuppressWarnings("unchecked")
	public ArrayIndexedCollection(int initialCapacity) {
		super();
		if (initialCapacity < 1) {
			throw new IllegalArgumentException("Initial capacity must be >= 1!");
		}
		elements = (T[]) new Object[initialCapacity];
	}
	
	public ArrayIndexedCollection(Collection<? extends T> other) {
		//super();
		this();
		if (other == null) {
			throw new NullPointerException("Other collection must not be null!");
		}
		this.addAll(other);
	}
	
	public ArrayIndexedCollection(int initialCapacity, Collection<T> other) {
		this(Math.max(initialCapacity, other.size())); //to honour the limit that if the initialCapacity is smaller than the size of the other collection, the size of the given collection should should be used for the size of the new collection
		this.addAll(other);											   //there is no check to see if other collection is null, because the call of the simpler constructor has to be first, but calling .size over a null object would still throw a NullPointerException
		
	}

	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}

	@Override
	public int size() {
		return this.size;
	}
	
	/**
	 * Adds the given object to the array. Does not accept null values
	 */
	@Override
	public void add(T value) { //average complexity if O(1) if array is not full
		//System.out.println("Adding " + Integer.toString((int)value));
		if (value == null) {
			throw new NullPointerException("New element must not be null!");
		}
		if (this.elements.length == this.size) { //complexity of array.length is O(1)
			this.doubleArray();
		}
		this.elements[size] = value;
		this.size++;
	}

	@Override
	public boolean contains(Object value) {
		for (T obj : this.elements) {
			if (obj.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean remove(Object value) { //remove which removes an object with the same value
		for (int i = 0; i < this.size - 1; i++) {
			if (this.elements[i].equals(value)) {
				this.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes an element at specified index from collection. Shifts all elements after it by one position to the left, so the array remains interrupted
	 * @param index accepted values [0, size>
	 * @return
	 */
	public void remove(int index) { 
		if (index < 0 || index > this.size - 1) {
			throw new IndexOutOfBoundsException("Index must be in [0,size>");
		}
		if (index != this.size - 1) { //if we remove the last element, nothing has to shift
			this.modificationCount++;
		}
		for (int i = index; i < this.size - 1; i++) {
			this.elements[index] = this.elements[index + 1];
		}
		this.elements[this.size] = null;
		this.size--;
	}

	@Override
	public T[] toArray() {
		@SuppressWarnings("unchecked")
		T[] tmp = (T[]) new Object[this.size];
		for (int i = 0; i < this.size; i++) {
			tmp[i] = this.elements[i];
		}
		return tmp;
	}


	@Override
	public void addAll(Collection<? extends T> other) { //čak možda ne treba ni neka posebna implementacija jer će se pozvati procesor koji poziva metodu add koja se brine za slučaj prepunjenja polja
		// TODO Auto-generated method stub
		List.super.addAll(other); 
	}

	@Override
	public void clear() {
		this.modificationCount++;
		for (T obj: this.elements) {
			obj = null;
		}
		this.size = 0;
		
	}
	
	public T get(int index) { //average complexity is O(1)
		if (index < 0 || index > this.size - 1) {
			throw new IndexOutOfBoundsException("Invalid index! Index must be between 0 and " + Integer.toString(this.size));
		}
		return this.elements[index];
	}
	/**
	 * Inserts the given value at the given position in array. Does not overwrite, elements are shifted to make space for the new element. 
	 * @param value
	 * @param position valid positions are [0,size]
	 */
	public void insert(T value, int position) {
		if (position < 0 || position > size) {
			throw new IndexOutOfBoundsException("Position is invalid! It needs to be in [0,size]");
		}
		if (this.elements.length == this.size) { //complexity of array.length is O(1)
			this.doubleArray();
		}
		//Object[] tmp = ;
		T obj = null;
		obj = this.elements[position];
		T prev = value;
		if (position != size) { //if we insert at the last position, nothing has to shift
			this.modificationCount++;
		}
		for (int i = position; i <= this.size + 1; i++) { //nadam se da ovo radi, ako igdje ima greska ovdje je
			obj = this.elements[i];
			this.elements[i] = prev;
			prev = obj;
		}
			
		size++;
	}
	
	/**
	 * Searches the collection and returns the index of the first occurrence of the given value
	 * @param value can be null - then the result is -1
	 * @return index of the first given value or -1 if the value is not found
	 */
	public int indexOf(Object value) { //average complexity is O(n), not parameterized because why wouldn't it be legal to check if some other object, not of type T, isn't a part of this array
		if (value == null) {
			return -1; 		//to save time
		}
		for (int i = 0; i < this.size; i++) {
			if (this.elements[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}
	
	
	/**
	 * Assisting method
	 */
	@SuppressWarnings("unchecked")
	private void doubleArray() {
		T[] tmp = this.elements;
		this.elements = (T[]) new Object[this.size * 2];
		for (int i = 0; i < tmp.length; i++) {
			this.elements[i] = tmp[i];
		}
		this.modificationCount++; //as this is an operation which reallocates the array, we increase the modification count
		
	}
	
	private static class ArrayElementsGetter<T> implements ElementsGetter{
		
		private int index;
		private int size;
		private ArrayIndexedCollection<T> array;
		private long savedModificationCount;
		
		private ArrayElementsGetter(ArrayIndexedCollection<T> array) {
			this.array = array;
			this.index = 0;
			this.savedModificationCount = array.modificationCount;
		}

		@Override
		public boolean hasNextElement() {
			if (this.savedModificationCount != this.array.modificationCount) {
				throw new ConcurrentModificationException("The array has been modified!");
			}
			return this.index <= this.array.size - 1;
		}

		@Override
		public T getNextElement() {
			if (this.savedModificationCount != this.array.modificationCount) {
				throw new ConcurrentModificationException("The array has been modified!");
			}
			if (this.hasNextElement() == false){
				throw new NoSuchElementException();
			}
			return array.get(this.index++);
		}
		
	}

	@Override
	public ElementsGetter<T> createElementsGetter() {
		return new ArrayElementsGetter<T>(this);
	}

	
	

}
