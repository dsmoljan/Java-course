package hr.fer.oprpp1.custom.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayIndexedCollectionTest {
	
	
	@Test
	public void testConstructors() {
		ArrayIndexedCollection array = new ArrayIndexedCollection();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);
		array.add(5);
		
		if (array.size() != 5) {
			Assertions.fail("Wrong number of elements in the array created using the 1st constructor!");
		}
		
		ArrayIndexedCollection array2 = new ArrayIndexedCollection(5);
		
		array2.add(1);
		array2.add(2);
		array2.add(3);
		array2.add(4);
		array2.add(5);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> new ArrayIndexedCollection(-1)); //lambda izraz, kraća implementacija anonimne klase
		Assertions.assertThrows(NullPointerException.class, () -> new ArrayIndexedCollection(null));
		
		if (array2.size() != 5) {
			Assertions.fail("Wrong number of elements in the array created using the 2nd constructor!");
		}
		
		ArrayIndexedCollection array3 = new ArrayIndexedCollection(array);
		Assertions.assertTrue(array3.size() == array.size());
		
		ArrayIndexedCollection array4 = new ArrayIndexedCollection(10, array);
		
		int[] tmp = {1,2,3,4,5};
		
		for (int i = 0; i < 5; i++) {
			if ((int)array.get(i) != tmp[i]) {
				Assertions.fail("Something is wrong with the 1st constructor!");
			}
			if((int)array2.get(i) != tmp[i]) {
				Assertions.fail("Something is wrong with the 2nd constructor!");
			}
			if ((int)array3.get(i) != tmp[i]) {
				Assertions.fail("Something is wrong with the 3rd constructor!");
			}
			if ((int)array4.get(i) != tmp[i]) {
				Assertions.fail("Something is wrong with the 4th constructor!");
			}
		}
	}
	
	@Test
	public void testAdd() {
		ArrayIndexedCollection array = new ArrayIndexedCollection(2);
		array.add(1);
		array.add(2);
		array.add(3);
	}
	
	@Test
	public void testGet() {
		ArrayIndexedCollection array = new ArrayIndexedCollection(2);
		array.add(1);
		
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
		
		array.add(2);
		
		@SuppressWarnings("unused")
		Object a = array.get(1);
	}
	
	@Test
	public void testClear() {
		ArrayIndexedCollection array = new ArrayIndexedCollection();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);
		array.add(5);
		
		array.clear();
		Assertions.assertTrue(array.size() == 0);
		Assertions.assertThrows(IndexOutOfBoundsException.class,() -> array.get(0));
	}
	
	@Test
	public void testInsert() {
		ArrayIndexedCollection array = new ArrayIndexedCollection();
		array.add(1);
		array.add(2);
		array.add(4);
		array.insert(3, 2);
		
		int[] tmp = {1,2,3,4};
		
		//insertion at the next to last position
		for (int i = 0; i < 4; i++) {
			if ((int)array.get(i) != tmp[i]) {
				System.out.println("Test array is ");
				System.out.println(Integer.toString((int)array.get(0)));
				System.out.println(Integer.toString((int)array.get(1)));
				System.out.println(Integer.toString((int)array.get(2)));
				System.out.println(Integer.toString((int)array.get(3)));
				System.out.println("when it should be 1 2 3 4");
				Assertions.fail("Arrays don't match!");
			}
		}
		
		//insertion at the last position
		int[] tmp2 = {1,2,3,4,5};
		
		array.insert(5, 4);
		
		for (int i = 0; i < 5; i++) {
			if ((int)array.get(i) != tmp2[i]) {
				System.out.println("Test array is ");
				System.out.println(Integer.toString((int)array.get(0)));
				System.out.println(Integer.toString((int)array.get(1)));
				System.out.println(Integer.toString((int)array.get(2)));
				System.out.println(Integer.toString((int)array.get(3)));
				System.out.println(Integer.toString((int)array.get(4)));
				System.out.println("when it should be 1 2 3 4 5");
				Assertions.fail("Arrays don't match!");
			}
		}
	}
	
	@Test
	public void testIndexOf(){
		ArrayIndexedCollection array = new ArrayIndexedCollection();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(3);
		
		Assertions.assertTrue(array.indexOf(3) == 2);
		Assertions.assertTrue(array.indexOf(4) == -1);
	}
	
	@Test
	public void testRemove() {
		ArrayIndexedCollection array = new ArrayIndexedCollection();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);
		
		array.remove(2);
		
		int[] tmp = {1,2,4};
		
		for (int i = 0; i < 3; i++) {
			if ((int)array.get(i) != tmp[i]) {
				Assertions.fail("Arrays are not equal");
			}
		}
		
	}
	
	//TODO: dodaj one testove sa stringovima koji su u tekstu zadaće
}
