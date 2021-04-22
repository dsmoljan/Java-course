package hr.fer.oprpp1.custom.collections;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LinkedListIndexedCollectionTest {
	
	@Test
	public void testAdd() {
		LinkedListIndexedCollection list = new LinkedListIndexedCollection();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		
		int[] testArray = {1,2,3,4};
		
		for (int i = 0; i < 4; i++) {
			//System.out.println(Integer.toString((int)list.get(i)));
			if((int)list.get(i) != testArray[i]) {
				Assertions.fail("Arrays don't match!");
			}
		}
	}
	
	@Test
	public void testClear() {
		LinkedListIndexedCollection list = new LinkedListIndexedCollection();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		
		list.clear();
		
		Assertions.assertThrows(NullPointerException.class, () -> list.get(0));
		Assertions.assertThrows(NullPointerException.class, () -> list.get(2));
		Assertions.assertThrows(NullPointerException.class, () -> list.get(3));
		Assertions.assertTrue(list.size() == 0);
	}

	@Test
	public void testInsert() {
		LinkedListIndexedCollection list = new LinkedListIndexedCollection();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(5);
		
		list.insert(4, 3);
		
		int[] testArray = {1,2,3,4,5};
		
		for (int i = 0; i < 5; i++) {
			//System.out.println(Integer.toString((int)list.get(i)));
			if ((int)list.get(i) != testArray[i]) {
				Assertions.fail("Arrays don't match!");
			}
		}
	}
	
	@Test
	public void testIndexOf() {
		LinkedListIndexedCollection list = new LinkedListIndexedCollection();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		
		Assertions.assertTrue(list.indexOf(1) == 0);
		Assertions.assertTrue(list.indexOf(2) == 1);
		Assertions.assertTrue(list.indexOf(3) == 2);
		Assertions.assertTrue(list.indexOf(4) == 3);
	}
	
	@Test
	public void testRemove() {
		LinkedListIndexedCollection list = new LinkedListIndexedCollection();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		
		list.remove(0);
		list.remove(2);
		list.remove(2);

		
		int[] testArray = {2,3};
		
		Assertions.assertTrue(((int)list.get(0) == 2 && (int)list.get(1) == 3));
		
	}
	
	@Test
	public void testAddAll() {
		LinkedListIndexedCollection list = new LinkedListIndexedCollection();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		
		ArrayIndexedCollection array = new ArrayIndexedCollection();
		array.add(6);
		array.add(7);
		array.add(8);
		array.add(9);
		array.add(10);
		
		list.addAll(array);
		
		int[] testArray = {1,2,3,4,5,6,7,8,9,10};
		
		for (int i = 0; i < 10; i++) {
			//System.out.println(Integer.toString((int)list.get(i)));
			if ((int)list.get(i) != testArray[i]) {
				Assertions.fail("Arrays don't match!");
			}
		}	
	}
	
	//@Test
	public void commonTest() {
		ArrayIndexedCollection col = new ArrayIndexedCollection(2);
		col.add(Integer.valueOf(20));
		col.add("New York");
		col.add("San Francisco");  // here the internal array is reallocated to 4
		System.out.println(col.contains("New York")); // writes: true
		col.remove(1); // removes "New York"; shifts "San Francisco" to position 1
		System.out.println(col.get(1)); // writes: "San Francisco"
		System.out.println(col.size()); // writes: 2
		col.add("Los Angeles");
		
		LinkedListIndexedCollection col2 = new LinkedListIndexedCollection(col);
		
		// This is local class representing a Processor which writes objects to System.out
		class P extends Processor {
			  public void process(Object o) {
				    System.out.println(o);
			  }
		};
		
		System.out.println("col elements:");
		col.forEach(new P());
		
		System.out.println("col elements again:");
		System.out.println(Arrays.toString(col.toArray()));
		
		System.out.println("col2 elements:");
		col2.forEach(new P());
		
		System.out.println("col2 elements again:");
		System.out.println(Arrays.toString(col2.toArray()));
		
		System.out.println(col.contains(col2.get(1))); // true
		System.out.println(col2.contains(col.get(1))); // true
		
		col.remove(Integer.valueOf(20)); // removes 20 from collection (at position 0).
	}
}
