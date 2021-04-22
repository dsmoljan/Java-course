package hr.fer.oprpp1.custom.collections;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//tests include tests for ElementsGetter
public class LinkedListIndexedCollectionTest {
	
	@Test
	public void testAdd() {
		LinkedListIndexedCollection<Integer> list = new LinkedListIndexedCollection<>();
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
		LinkedListIndexedCollection<Integer> list = new LinkedListIndexedCollection<>();
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
		LinkedListIndexedCollection<Integer> list = new LinkedListIndexedCollection<>();
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
		LinkedListIndexedCollection<Integer> list = new LinkedListIndexedCollection<>();
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
		LinkedListIndexedCollection<Integer> list = new LinkedListIndexedCollection<>();
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
		LinkedListIndexedCollection<Integer> list = new LinkedListIndexedCollection<>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		
		ArrayIndexedCollection<Integer> array = new ArrayIndexedCollection<>();
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
	
	
	@Test
	public void testElementsGetter() {
		Collection<String> col = new LinkedListIndexedCollection<>();
		col.add("Ivo");
		col.add("Ana");
		col.add("Jasna");
		
		ElementsGetter<String> getter1 = col.createElementsGetter();
		ElementsGetter<String> getter2 = col.createElementsGetter();
		
		Assertions.assertTrue(getter1.hasNextElement());
		Assertions.assertTrue(getter1.hasNextElement());
		Assertions.assertTrue(getter1.hasNextElement());
		Assertions.assertTrue(getter1.hasNextElement());
		Assertions.assertTrue(getter1.hasNextElement());
		Assertions.assertEquals("Ivo", getter1.getNextElement());
		Assertions.assertTrue(getter1.hasNextElement());
		Assertions.assertEquals("Ana", getter1.getNextElement());
		Assertions.assertTrue(getter1.hasNextElement());
		Assertions.assertEquals("Jasna", getter1.getNextElement());
		Assertions.assertFalse(getter1.hasNextElement());
		Assertions.assertThrows(NoSuchElementException.class, () -> getter1.getNextElement());
		Assertions.assertFalse(getter1.hasNextElement());
		
		Assertions.assertTrue(getter2.hasNextElement());
		Assertions.assertTrue(getter2.hasNextElement());
		Assertions.assertTrue(getter2.hasNextElement());
		Assertions.assertTrue(getter2.hasNextElement());
		Assertions.assertTrue(getter2.hasNextElement());
		Assertions.assertEquals("Ivo", getter2.getNextElement());
		Assertions.assertTrue(getter2.hasNextElement());
		Assertions.assertEquals("Ana", getter2.getNextElement());
		Assertions.assertTrue(getter2.hasNextElement());
		Assertions.assertEquals("Jasna", getter2.getNextElement());
		Assertions.assertFalse(getter2.hasNextElement());
		Assertions.assertThrows(NoSuchElementException.class, () -> getter2.getNextElement());
		Assertions.assertFalse(getter2.hasNextElement());
		
		ElementsGetter<String> getter3 = col.createElementsGetter();
		ElementsGetter<String> getter4 = col.createElementsGetter();
		
		Assertions.assertEquals("Ivo", getter3.getNextElement());
		Assertions.assertEquals("Ana", getter3.getNextElement());
		Assertions.assertEquals("Ivo", getter4.getNextElement());
		Assertions.assertEquals("Jasna", getter3.getNextElement());
		Assertions.assertEquals("Ana", getter4.getNextElement());
		
		Collection<String> col1 = new LinkedListIndexedCollection<>();
		Collection<String> col2 = new LinkedListIndexedCollection<>();
		col1.add("Ivo");
		col1.add("Ana");
		col1.add("Jasna");
		col2.add("Jasmina");
		col2.add("Štefanija");
		col2.add("Karmela");
		
		ElementsGetter<String> getter5 = col1.createElementsGetter();
		ElementsGetter<String> getter6 = col1.createElementsGetter();
		ElementsGetter<String> getter7 = col2.createElementsGetter();
		
		Assertions.assertEquals("Ivo", getter5.getNextElement());
		Assertions.assertEquals("Ana", getter5.getNextElement());
		Assertions.assertEquals("Ivo", getter6.getNextElement());
		Assertions.assertEquals("Jasmina", getter7.getNextElement());
		Assertions.assertEquals("Štefanija", getter7.getNextElement());
		
		//testing illeagal modifications of the array
		col = new LinkedListIndexedCollection<>();
		col.add("Ivo");
		col.add("Ana");
		col.add("Jasna");

		ElementsGetter<String> getter8 = col.createElementsGetter();

		Assertions.assertEquals("Ivo", getter8.getNextElement());
		Assertions.assertEquals("Ana", getter8.getNextElement());

		col.clear();

		Assertions.assertThrows(ConcurrentModificationException.class, () -> getter8.getNextElement());
	}
	
	@Test
	public void testProcessRemaining() {
		Collection<String> col1 = new LinkedListIndexedCollection<>();
		Collection<String> col2 = new LinkedListIndexedCollection<>();

		col1.add("Ivo");
		col1.add("Ana");
		col1.add("Jasna");
		
		ElementsGetter<String> getter = col1.createElementsGetter();
		getter.getNextElement();
		
		
		getter.processRemaining(p -> col2.add(p)); //p je parametar metode process objekta klase procesor
		
		getter = col2.createElementsGetter();

		
		Assertions.assertEquals("Ana", getter.getNextElement());
		Assertions.assertEquals("Jasna", getter.getNextElement());
	}
}
