package hr.fer.oprpp1.custom.collections;

/**
 * Class modeling a dictionary (map)
 * @author dorian
 *
 * @param <K> data type of the entry key
 * @param <V> data type of the entry value
 */
public class Dictionary<K,V> {
	
	//kako bi se ovo moglo riješiti: napravi se nova privatna klasa koja modelira entry
	//i zatim se samo dodaje takav entry u array
	//i {
	//kad hoćemo dohvatiti entry, pretražujemo array
	
	private class Entry<K,V>{
		private K key;
		private V value;
		
		private Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		private K getKey() {
			return this.key;
		}
		
		private V getValue() {
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
			if (!(obj instanceof Entry))
				return false;
			@SuppressWarnings("unchecked")
			Entry<K,V> other = (Entry<K,V>) obj;
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
	
	private ArrayIndexedCollection<Entry<K,V>> array;
	
	public Dictionary(){
		this.array = new ArrayIndexedCollection<>();
	}
	
	/**
	 * Checks if the dictionary is empty
	 * @return true if the dictionary is empty
	 */
	public boolean isEmpty() {
		return this.array.size() == 0;
	}
	
	/**
	 * Returns the number of entries in the dictionary
	 * @return
	 */
	public int size() {
		return this.array.size();
	}
	
	/**
	 * Removes all entries from the dictionary
	 */
	public void clear() {
		this.array.clear();
	}
	
	/**
	 * Puts a new entry into the dictionary. If an entry with the same key already exists in the dictionary,
	 * it overwrites its value with the new one and returns the old value
	 * @param key key of the new entry
	 * @param value value of the new entry
	 * @return old value, if an entry with the same key was already in the dictionary, null if there was no such entry
	 */
	public V put(K key, V value) {
		Entry<K,V> tmp = new Entry<>(key, value);
		V oldValue = null;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).getKey().equals(tmp.getKey())) {
				oldValue = array.get(i).getValue();
				array.remove(i);
				array.insert(new Entry<K,V>(key,value), i);
				return oldValue;
			}
		}
		array.add(new Entry<K,V>(key,value));
		return null;
	}
	
	/**
	 * Returns the value of the entry containing the given key
	 * @param key
	 * @return value of the entry containing the given key, null if there is no such entry
	 */
	public V get(Object key) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).getKey().equals(key)) {
				return array.get(i).getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Removes an entry containing the given key, and returns its value
	 * @param key
	 * @return value of the removed entry, null if there was no entry with the given key
	 */
	public V remove(K key) {
		V tmp = null;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).getKey().equals(key)) {
				tmp = array.get(i).getValue();
				array.remove(i);
				return tmp;
			}
		}
		return null;
		
	}
	
	@Override
	public String toString() {
		String res = "[";
		for (int i = 0; i < array.size(); i++) {
			if (i < array.size() - 1) {
				res += "{" +  array.get(i).getKey().toString() + "," + array.get(i).getValue().toString() + "},";
			} else {
				res += "{" +  array.get(i).getKey().toString() + "," + array.get(i).getValue().toString() + "}";
			}
		}
		
		res += "]";
		return res;
	}
	
	
	


}
