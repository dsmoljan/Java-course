package hr.fer.zemris.java.custom.scripting.exec;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectMultistack {
	
	private Map<String, MultistackEntry> map = new LinkedHashMap<>();
	
	/**
	 * Node of a single linked list
	 * @author Dorian
	 *
	 */
	private static class MultistackEntry{
		
		private MultistackEntry next;
		private ValueWrapper value;
		
		private MultistackEntry(ValueWrapper value, MultistackEntry next) {
			this.value = value;
			this.next = next;
		}

		public MultistackEntry getNext() {
			return next;
		}

		public void setNext(MultistackEntry next) {
			this.next = next;
		}

		public ValueWrapper getValue() {
			return value;
		}

		public void setValue(ValueWrapper value) {
			this.value = value;
		}
		
		
		
	}
	
	public void push(String keyName, ValueWrapper valueWrapper) {
		if (map.get(keyName) == null) {
			map.put(keyName, new MultistackEntry(valueWrapper, null));
		}else {
			MultistackEntry next = map.get(keyName);
			MultistackEntry newEntry = new MultistackEntry(valueWrapper, next);
			map.put(keyName, newEntry);
		}
		
	}
	public ValueWrapper pop(String keyName) {
		MultistackEntry current = map.get(keyName);
		MultistackEntry next = current.next;
		
		if (next != null) {
			map.put(keyName, next);
		}else {
			map.remove(keyName);
		}
		
		return current.getValue();
		
	}
	public ValueWrapper peek(String keyName) {
		return map.get(keyName).getValue();
		
	}
	public boolean isEmpty(String keyName) {
		return map.isEmpty();
	}


}
