package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Class modeling a simple hash table
 * @author dorian
 *
 * @param <K> key type for the entry
 * @param <V> value type for the entry
 */
public class SimpleHashtable<K,V> implements Iterable<SimpleHashtable.TableEntry<K,V>>{
	
	public static class TableEntry<K,V>{
		private K key;
		private V value;
		private TableEntry<K,V> next; //pokazivac na sljedeci clan u njegovom slotu
		
		public TableEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		public K getKey() {
			return this.key;
		}
		
		public void setValue(V value) {
			this.value = value;
		}
		
		public V getValue() {
			return this.value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof TableEntry))
				return false;
			TableEntry<K, V> other = (TableEntry<K,V>) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		
		
	}
	
	private TableEntry<K, V>[] table;
	private int size;
	private int modificationCount;

	
	/**
	 * Default constructor. Allocates a new hash table with 16 slots.
	 */
	@SuppressWarnings("unchecked")
	public SimpleHashtable(){
		this.table = (TableEntry<K,V>[])new TableEntry[16];
		this.size = 0;
		this.modificationCount = 0;
	}
	
	/**
	 * Constructors, takes in a value for the capacity of the table and allocates a new hash table with the number of slot equal to the nearest power of two.
	 *  i.e for a given capacity 30, it allocates a 32 slot table
	 * @param capacity
	 */
	public SimpleHashtable(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Capacity has to be at least 1!");
		}
		
		int trueCapacity = (int) Math.pow(2, Math.ceil(Math.log(capacity)/Math.log(2))); //npr. ln(30)/ln(2) = 4.9
		table = (TableEntry<K,V>[])new TableEntry[trueCapacity];
		//System.out.println("Radim novo polje sa " + trueCapacity + " mjesta");
		this.size = 0;
	}
	
	/**
	 * Adds a new entry to the hash table. 
	 * @param key Key of the new entry. Must not be null
	 * @param value Value of the new entry. Can be null
	 */
	public void put(K key, V value) {
		if (key == null) {
			throw new NullPointerException("The key must not be null!");
		}
		
		//System.out.println("Broj slotova je " + this.table.length);
		int slot = Math.abs((key.hashCode() % this.table.length));
		//dohvati prvi element odgovarajućeg slota iz polja slotova
		//te idi po toj ulančanoj listi čiji je prvi element prvi član i dodaj novi entry na kraj
		TableEntry<K, V> tmp = this.table[slot];
		boolean alreadyAdded = false;
		double noOfElementsInSlot = 1;
		
		if (tmp == null) { //ako dosad nije bilo ništa zapisano na tom slotu, onda sigurno nema ni prepunjenja tog slota
			this.table[slot] = new TableEntry<K,V>(key, value);
			this.size++;
		} else {
			while (tmp.next != null) {
				if (tmp.getKey().equals(key)) { //ako u mapi već postoji vrijednost, onda isto nema prepunjenja jer samo mijenjamo entry, ne dodajemo novi
					tmp.setValue(value);
					alreadyAdded = true;
					break;
				}
				noOfElementsInSlot++;
				tmp = tmp.next;
			}
			if (!alreadyAdded) {
				this.modificationCount++;
				double tmpVar = (noOfElementsInSlot + 1) / (this.size + 1);
				if (tmpVar >= 0.75) {
					this.doubleCapacity();
					slot = Math.abs((key.hashCode() % this.table.length));
					tmp = this.table[slot];
					if (tmp == null) {
						this.table[slot] = new TableEntry<K, V>(key, value);
						this.size++;
					} else {
						while (tmp.next != null) {
							tmp = tmp.next;
						}
						tmp.next = new TableEntry<K, V>(key, value);
						this.size++;
					}
										
					//tmp.next = new TableEntry<K, V>(key, value);
					//this.put(key, value); //nakon sto povecas kapacitet polja, ponovo pozovi metodu add, jer sad možda element pripada nekom drugom slotu
					//this.size++;
				} else {
					tmp.next = new TableEntry<K, V>(key, value); //dodajemo novi entry na kraj ulančane liste, moramo provjeriti da nije došlo do prepunjenja
					this.size++; 		// we increase the size only if we add an entry with a new key, not if we modify an exsisting one
				}
}
		}
	}

	/**
	 * Gets the value corresponding to the given key.
	 * @param key
	 * @return the requested value if it exists in the table, null otherwise
	 */
	public V get(K key) {
		int slot = Math.abs((key.hashCode() % this.table.length));
		V res = null;
		TableEntry<K, V> tmp = this.table[slot];
		if (tmp == null) {
			return res;
		} else {
			while (tmp != null) {
				if (tmp.getKey().equals(key)) { //ako u mapi već postoji vrijednost
					res = tmp.getValue();
					break;
				}
				tmp = tmp.next;
			}
			
			return res;
		}
	}
	
	/**
	 * Returns the number of entries in the table
	 * @return
	 */
	public int size() {
		return this.size;
	}
	
	/**
	 * Returns true if the table contains an entry with the given key
	 * @return
	 */
	public boolean containsKey(K key) {
		int slot = Math.abs((key.hashCode() % this.table.length));
		TableEntry<K, V> tmp = this.table[slot];
		if (tmp == null) {
			return false;
		} else {
			while (tmp != null) {
				if (tmp.getKey().equals(key)) { //ako u mapi već postoji vrijednost
					return true;
				}
				tmp = tmp.next;
			}
			
			return false;
		}
	}
	
	/**
	 * Returns true if there is at least one entry in the table containing a given value, false otherwise. Null is a legal argument
	 * @param value
	 * @return
	 */
	public boolean containsValue(V value) {
		for (int i = 0; i < this.table.length; i++) {
			TableEntry<K, V> tmp = this.table[i];
			while (tmp != null) {
				if (tmp.getValue().equals(value)) {
					return true;
				}
				tmp = tmp.next;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes an entry containing the given key, and returns the entry's value. If there is no such entry, returns null
	 * @param key
	 * @return
	 */
	public V remove(K key) {
		this.modificationCount++;
		int slot = Math.abs((key.hashCode() % this.table.length));
		TableEntry<K, V> tmp = this.table[slot];
		TableEntry<K, V> prev = null;
		if (tmp == null) {
			return null;
		} else {
			while (tmp != null) {
				if (tmp.getKey().equals(key)) { //ako u mapi već postoji vrijednost
					if (prev != null) { 
						prev.next = tmp.next;
					} else {
						this.table[slot] = tmp.next; //ako je entry koji zelimo maknuti prvi u polju
					}
					this.size--;
					return tmp.value;
				}
				prev = tmp;
				tmp = tmp.next;
			}
			return null;
		}
	}
	
	/**
	 * Returns true if the table is empty
	 * @return
	 */
	public boolean isEmpty() {
		if (this.size == 0) return true;
		return false;	
	}
	
	@Override
	public String toString() {
		String res = "[";
		
		for (int i = 0; i < this.table.length; i++) {
			TableEntry<K, V> tmp = this.table[i];
			while (tmp != null) {
				res += tmp.getKey().toString() + "=" + tmp.getValue() +", ";
				tmp = tmp.next;
			}
		}
		return res + "]";
	}
	
	/**
	 * Returns a new TableEntry array filled with entries from the table
	 * @return
	 */
	public TableEntry<K,V>[] toArray(){
		@SuppressWarnings("unchecked")
		TableEntry<K,V>[] resultArray = (TableEntry<K,V>[])new TableEntry[this.size];
		int counter = 0;
		for (int i = 0; i < this.table.length; i++) {
			TableEntry<K, V> tmp = this.table[i];
			while (tmp != null) {
				resultArray[counter] = tmp;
				counter++;
				tmp = tmp.next;
			}
		}
		
		return resultArray;
	}
	
	/**
	 * Removes all entries from the table
	 */
	public void clear() {
		for (int i = 0; i < this.table.length; i++) {
			this.table[i] = null;
			this.size = 0;
		}
	}
	
	/**
	 * Private method used to double the number of slots.
	 */
	@SuppressWarnings("unchecked")
	private void doubleCapacity() {
		TableEntry<K, V>[] tmp = this.toArray();
		int newLength = this.table.length * 2;
		this.table = (TableEntry<K,V>[])new TableEntry[newLength];
		//System.out.println("Calling doubleCapacity, new capacity is " + Integer.toString(newLength));

		this.size = 0;
		
		for (int i = 0; i < tmp.length; i++) {
			this.put(tmp[i].getKey(), tmp[i].getValue());
		}
		
	}

	@Override
	public Iterator<TableEntry<K, V>> iterator() {
		return new IteratorImpl();
	}
	
	private class IteratorImpl implements Iterator<TableEntry<K,V>>{
		int remainingNumbers;
		int index;
		int currentModificationCount;
		SimpleHashtable.TableEntry<K, V> currentEntry;
		SimpleHashtable.TableEntry<K, V> lastEntry;

		
		public IteratorImpl() {
			remainingNumbers = size;
			index = 0;
			currentEntry = table[index++];
			currentModificationCount = modificationCount;
			while (currentEntry == null) {
				currentEntry = table[index++];
			}
			lastEntry = null;
		}
		
		@Override
		public boolean hasNext() {
			return remainingNumbers > 0;
		}

		@Override
		public SimpleHashtable.TableEntry<K, V> next() {
			if (currentModificationCount != modificationCount) {
				throw new ConcurrentModificationException("Illegal action - the table has been modified!");
			}
			SimpleHashtable.TableEntry<K, V> res = currentEntry;
			lastEntry = res;


			if (remainingNumbers < 1) {
				throw new NoSuchElementException("No more entries in the map avalible!");
			}
			
			if (remainingNumbers > 1 && currentEntry.next == null) { //ako dođemo do kraja polja nekog slota
				currentEntry = table[index++];
				while (currentEntry == null) {
					currentEntry = table[index++];
				}
			} else {
				currentEntry = currentEntry.next;
			}
			remainingNumbers--;
			return res;
		};
		
		@Override
		public void remove() {
			if (lastEntry == null) {
				throw new IllegalStateException("You aren't allowed to call this method!");
			}
			if (currentModificationCount != modificationCount) {
				throw new ConcurrentModificationException("Illegal action - the table has been modified!");
			}
			currentModificationCount++;
			SimpleHashtable.this.remove(lastEntry.getKey());
			lastEntry = null;
			
		}
	}


	

}
