package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import hr.fer.oprpp1.custom.collections.SimpleHashtable.TableEntry;

public class SimpleHashtableTest {
	
	@Test
	public void testConstructorsAndPuts() {
		SimpleHashtable<String, Integer> examMarks = new SimpleHashtable<String, Integer>(2);
		examMarks.put("Ivana", 2);
		examMarks.put("Ante", 2);
		examMarks.put("Jasna", 2);
		examMarks.put("Kristina", 5);
		examMarks.put("Ivana", 5);
		examMarks.put("Josip", null);
		
		Assertions.assertThrows(NullPointerException.class, () -> examMarks.put(null,2));
		Assertions.assertEquals(5, examMarks.get("Ivana"));
		Assertions.assertEquals(2, examMarks.get("Ante"));
		Assertions.assertEquals(2, examMarks.get("Jasna"));
		Assertions.assertEquals(5, examMarks.get("Kristina"));
		Assertions.assertEquals(5, examMarks.size());
		
		//System.out.println("Constructor test: " + examMarks);
	}
	
	@Test
	public void testContains() {
		
		SimpleHashtable<String, Integer> examMarks = new SimpleHashtable<String, Integer>(2);
		examMarks.put("Ivana", 1);
		examMarks.put("Ante", 2);
		examMarks.put("Jasna", 2);
		examMarks.put("Kristina", 5);
		examMarks.put("Ivana", 5);
		
		Assertions.assertTrue(examMarks.containsKey("Ante"));
		Assertions.assertTrue(examMarks.containsValue(2));
		Assertions.assertFalse(examMarks.containsValue(1));
		
	}
	
	@Test
	public void testRemoveAndIsEmpty() {
		SimpleHashtable<String, Integer> examMarks = new SimpleHashtable<String, Integer>(2);
		examMarks.put("Ivana", 1);
		examMarks.put("Ante", 2);
		examMarks.put("Jasna", 2);
		examMarks.put("Kristina", 5);
		examMarks.put("Ivana", 5);
		
		examMarks.remove("Ivana");
		examMarks.remove("Kristina");
		examMarks.remove("Jasna");
		examMarks.remove("Ante");
		
		Assertions.assertEquals(0, examMarks.size());
		Assertions.assertTrue(examMarks.isEmpty());
		//System.out.println("Remove test: " + examMarks);

	}
	
	@Test
	public void testToArray() {
		SimpleHashtable<String, Integer> examMarks = new SimpleHashtable<String, Integer>(2);
		examMarks.put("Ivana", 1);
		examMarks.put("Ante", 2);
		examMarks.put("Jasna", 2);
		examMarks.put("Kristina", 5);
		examMarks.put("Ivana", 5);
		
		SimpleHashtable.TableEntry<String, Integer>[] testArray = (TableEntry<String,Integer>[])new TableEntry[4];
		testArray[0] = new SimpleHashtable.TableEntry<String, Integer>("Ante", 2);
		testArray[1] = new SimpleHashtable.TableEntry<String, Integer>("Ivana", 5);
		testArray[2] = new SimpleHashtable.TableEntry<String, Integer>("Jasna", 2);
		testArray[3] = new SimpleHashtable.TableEntry<String, Integer>("Kristina", 5);
		
		//System.out.println(examMarks);
		TableEntry<String,Integer>[] tmpArray = examMarks.toArray();

		for (int i = 0; i < 4; i++) {
			if (!(tmpArray[i].equals(testArray[i]))){
				Assertions.fail("The arrays are not equal!");
			}
		}
	}
	
	@Test
	public void testIterator() {
		SimpleHashtable<String, Integer> examMarks = new SimpleHashtable<String, Integer>(2);
		examMarks.put("Ivana", 1);
		examMarks.put("Ante", 2);
		examMarks.put("Jasna", 2);
		examMarks.put("Kristina", 5);
		examMarks.put("Ivana", 5);
		
		/*for (SimpleHashtable.TableEntry<String, Integer> entry : examMarks) {
			System.out.println(entry.getKey() + "=>" + entry.getValue());
		}*/
		
		/*for (SimpleHashtable.TableEntry<String, Integer> pair1 : examMarks) {
			for (SimpleHashtable.TableEntry<String, Integer> pair2 : examMarks) {
				System.out.printf(
						"(%s => %d - (%s => %d)%n",
						pair1.getKey(), pair1.getValue(),
						pair2.getKey(), pair2.getValue());
			}
		}*/
		
		Assertions.assertThrows(IllegalStateException.class, () -> {
			Iterator<SimpleHashtable.TableEntry<String,Integer>> iter = examMarks.iterator();
			while (iter.hasNext()) {
				SimpleHashtable.TableEntry<String, Integer> pair = iter.next();
				if (pair.getKey().equals("Ivana")) {
					iter.remove();
					iter.remove();
				}
			}
		});
		
		Assertions.assertThrows(ConcurrentModificationException.class, () -> {
			Iterator<SimpleHashtable.TableEntry<String,Integer>> iter = examMarks.iterator();
			while(iter.hasNext()) {
				SimpleHashtable.TableEntry<String, Integer> pair = iter.next();
				if (pair.getKey().equals("Ante")) {
					examMarks.remove("Ante");
				}
			}
		});
		
		//print all remaining pairs and leave the collection empty
		Iterator<SimpleHashtable.TableEntry<String,Integer>> iter = examMarks.iterator();
		while(iter.hasNext()) {
			SimpleHashtable.TableEntry<String, Integer> pair = iter.next();
			System.out.printf("%s => %d%n", pair.getKey(), pair.getValue());
			iter.remove();
		}
		
		Assertions.assertEquals(0, examMarks.size());
	}

}
