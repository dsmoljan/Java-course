package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import javax.lang.model.util.Elements;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//tests include test for ElementsGetter
public class ArrayIndexedCollectionTest {
	
	
	@Test
	public void testConstructors() {
		ArrayIndexedCollection<Integer> array = new ArrayIndexedCollection<>();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);
		array.add(5);
		
		if (array.size() != 5) {
			Assertions.fail("Wrong number of elements in the array created using the 1st constructor!");
		}
		
		ArrayIndexedCollection<Integer> array2 = new ArrayIndexedCollection<>(5);
		
		array2.add(1);
		array2.add(2);
		array2.add(3);
		array2.add(4);
		array2.add(5);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> new ArrayIndexedCollection<Integer>(-1)); //lambda izraz, kraća implementacija anonimne klase
		Assertions.assertThrows(NullPointerException.class, () -> new ArrayIndexedCollection<Integer>(null));
		
		if (array2.size() != 5) {
			Assertions.fail("Wrong number of elements in the array created using the 2nd constructor!");
		}
		
		ArrayIndexedCollection<Integer> array3 = new ArrayIndexedCollection<>(array);
		Assertions.assertTrue(array3.size() == array.size());
		
		ArrayIndexedCollection<Integer> array4 = new ArrayIndexedCollection<>(10, array);
		
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
		ArrayIndexedCollection<Integer> array = new ArrayIndexedCollection<>(2);
		array.add(1);
		array.add(2);
		array.add(3);
	}
	
	@Test
	public void testGet() {
		ArrayIndexedCollection<Integer> array = new ArrayIndexedCollection<Integer>(2);
		array.add(1);
		
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
		
		array.add(2);
		
		@SuppressWarnings("unused")
		Object a = array.get(1);
	}
	
	@Test
	public void testClear() {
		ArrayIndexedCollection<Integer> array = new ArrayIndexedCollection<>();
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
		ArrayIndexedCollection<Integer> array = new ArrayIndexedCollection<>();
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
		ArrayIndexedCollection<Integer> array = new ArrayIndexedCollection<>();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(3);
		
		Assertions.assertTrue(array.indexOf(3) == 2);
		Assertions.assertTrue(array.indexOf(4) == -1);
	}
	
	@Test
	public void testRemove() {
		ArrayIndexedCollection<Integer> array = new ArrayIndexedCollection<>();
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
	
	@Test
	public void testElementsGetter() {
		Collection<String> col = new ArrayIndexedCollection<>();
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
		
		Collection<String> col1 = new ArrayIndexedCollection<>();
		Collection<String> col2= new ArrayIndexedCollection<>();
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
		col = new ArrayIndexedCollection<>();
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
		Collection<String> col1 = new ArrayIndexedCollection<>();
		Collection<String> col2 = new ArrayIndexedCollection<>();

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
	
	//TODO: dodaj one testove sa stringovima koji su u tekstu zadaće
}
