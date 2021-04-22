package hr.fer.oprpp1.custom.collections;

/**
 * Class which models a linked list. Duplicate elements are allowed. Null elements are not allowed
 * @author dorian
 *
 */
public class LinkedListIndexedCollection extends Collection{
	private static class ListNode{
		ListNode prev;
		ListNode next;
		Object value;
		
		private ListNode() {
			
		}
		
		private ListNode(Object value) {
			this.value = value;
		}
		
	}
	
	private int size;
	private ListNode first; 	//reference to the first node of the linked list
	private ListNode last; 		//reference to the last node of the linked list
	
	/**
	 * Default constructor, creates an empty LinkedList
	 */
	public LinkedListIndexedCollection() {
		this.size = 0;
		this.first = null;
		this.last = null;
	}
	
	/**
	 * Constructor which creates a new LinkedList and inserts all elements from the given collection
	 * @param other
	 */
	public LinkedListIndexedCollection(Collection other) {
		this();
		if (other == null) {
			throw new NullPointerException("Other collection must not be null!");
		}
		this.addAll(other);
	}

	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public void add(Object value) {
		if (value == null) {
			throw new NullPointerException("New element must not be null!");
		}
		if (this.first == null) {
			this.first = new ListNode();
			this.first.value = value;
			this.first.prev = null;
			this.first.next = null;
			this.last = this.first;
		} else {
			ListNode newNode = new ListNode();
			newNode.value = value;
			this.last.next = newNode;
			newNode.prev = this.last;
			this.last = newNode;
		}
		size++;
		
	}

	@Override
	public boolean contains(Object value) {
		ListNode tmp = this.first;
		
		while (tmp != null) {
			if (tmp.value == value) return true;
			tmp = tmp.next;
		}
		
		return false;
	}


	@Override
	public boolean remove(Object value) {
		ListNode tmp = this.first;
		
		if (this.first.value == value) {
			this.first = this.first.next;
			this.first.prev = null;
			return true;
		}
		
		while(tmp.value != value && tmp != null) {
			tmp = tmp.next;
		}
		
		if (tmp != null) {
			tmp.prev.next = tmp.next;
			tmp.next.prev = tmp.prev;
			return true;
		}
		return false;
	}
	
	/**
	 * Removes an element at specified index from collection. Shifts all elements after it by one position to the left, so the array remains interrupted
	 * @param index accepted values [0, size>
	 */
	public void remove(int index) {
		
		if (index < 0 || index >= size) {
			throw new IllegalArgumentException("Index must be between 0 and size - 1");
		}
		
		if (index == 0) {
			this.first = this.first.next;
			this.first.prev = null;
		} else {
			ListNode tmp = this.first;;
			int pos = 0;
			
			while (pos != index && tmp != null) {
				tmp = tmp.next;
				pos++;
			}
			tmp.prev.next = tmp.next;			
		}
		size--;
	}
	
	@Override
	public Object[] toArray() {
		Object[] array = new Object[this.size];  //method get was not used because it would slow the program down, because each call of the get method is of complexity O(n)
		ListNode tmp = this.first;

		for (int i = 0; i < this.size; i++) {
			array[i] = tmp.value;
			tmp = tmp.next;
		}
		
		return array;
	}

	@Override
	public void forEach(Processor processor) {
		ListNode tmp = this.first;
		
		while (tmp != null) {
			processor.process(tmp.value);
			tmp = tmp.next;
		}
	}

	@Override
	public void addAll(Collection other) {
		// TODO Auto-generated method stub
		super.addAll(other);
	}

	@Override
	public void clear() {
		ListNode current = this.first;
		while(current != null) {
			current.prev = null;
			current = current.next;
		}
		this.first = null;
		this.last = null;
		this.size = 0;
		
		
	}
	
	/**
	 * @param index valid entries are [0,size - 1]
	 * @return The object stored at the position index
	 */
	public Object get(int index) {  
		ListNode tmp = null;
		if (index <= this.size/2) {    //the requested element is in the first part of the list
			int pos = 0;
			tmp = this.first;
			while (pos != index) {
				tmp = tmp.next;
				pos++;
			}
		} else {				//the requested element is in the second part of the list
			int pos = this.size - 1;
			tmp = this.last;
			while (pos != index) {
				tmp = tmp.prev;
				pos--;
			}
		}
		return tmp.value;
	}
	
	/**
	 * Inserts the given value at the given position in array. Does not overwrite, elements are shifted to make space for the new element. 
	 * @param value
	 * @param position valid positions are [0,size]
	 */
	public void insert(Object value, int position) {
		if (position < 0 || position > size) {
			throw new IndexOutOfBoundsException("Position is invalid! It needs to be in [0,size]");
		}
		ListNode tmp = this.first;
		ListNode newNode = new ListNode(value);
		
		for (int i = 0; i < position; i++) {
			tmp = tmp.next;
		}
		if (position == 0) {
			newNode.next = this.first;
			newNode.prev = null;
			this.first = newNode;
		} else if (position == this.size) {
			this.last.next = newNode;
			newNode.prev = this.last;
			this.last = newNode;
		}
		else {
			newNode.next = tmp;
			newNode.prev = tmp.prev;
			newNode.prev.next = newNode;
			tmp.prev = newNode;
		}
		
		this.size++;
	}
	
	
	/**
	 * Searches the collection and returns the index of the first occurrence of the given value
	 * @param value can be null - then the result is -1
	 * @return index of the first given value or -1 if the value is not found
	 */
	public int indexOf(Object value) {
		ListNode tmp = this.first;
		
		if (value == null) return -1;
		
		int pos = 0;
		
		while (tmp != null) {
			if (tmp.value == value) return pos;
			pos++;
			tmp = tmp.next;
		}
		
		return -1;
	}
	
	
	
}
