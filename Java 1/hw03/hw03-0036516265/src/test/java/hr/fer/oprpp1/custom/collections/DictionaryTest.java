package hr.fer.oprpp1.custom.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DictionaryTest {
	
	@Test
	public void constructorAndGetterTest() {
	
		Dictionary<String, Integer> ocjeneStudenata = new Dictionary<>();
		ocjeneStudenata.put("Marko", 5);
		ocjeneStudenata.put("Iva", 4);
		ocjeneStudenata.put("Josip", 3);
		ocjeneStudenata.put("Marija", 5);
		
		//System.out.println(ocjeneStudenata.toString());
				
		Assertions.assertEquals(5, ocjeneStudenata.get("Marko"));
		Assertions.assertEquals(5, ocjeneStudenata.get("Marija"));
		Assertions.assertEquals(3, ocjeneStudenata.get("Josip"));
		Assertions.assertEquals(4, ocjeneStudenata.get("Iva"));
		
	}
	
	@Test
	public void setNewValueTest() {
		
		Dictionary<String, Integer> ocjeneStudenata = new Dictionary<>();
		ocjeneStudenata.put("Marko", 5);
		ocjeneStudenata.put("Iva", 4);
		ocjeneStudenata.put("Josip", 3);
		ocjeneStudenata.put("Marija", 5);
		
		ocjeneStudenata.put("Iva", 5);
		Assertions.assertEquals(5, ocjeneStudenata.get("Iva"));
		//System.out.println(ocjeneStudenata.toString());
	}
	
	@Test
	public void testRemove() {
		Dictionary<String, Integer> ocjeneStudenata = new Dictionary<>();
		ocjeneStudenata.put("Marko", 5);
		ocjeneStudenata.put("Iva", 4);
		ocjeneStudenata.put("Josip", 3);
		ocjeneStudenata.put("Marija", 5);
		
		ocjeneStudenata.remove("Josip");
		
		Assertions.assertEquals(5, ocjeneStudenata.get("Marko"));
		Assertions.assertEquals(5, ocjeneStudenata.get("Marija"));
		Assertions.assertEquals(4, ocjeneStudenata.get("Iva"));
		Assertions.assertNull(ocjeneStudenata.get("Josip"));
		
	}
	
	@Test
	public void testRemoveAll() {
		
		Dictionary<String, Integer> ocjeneStudenata = new Dictionary<>();
		ocjeneStudenata.put("Marko", 5);
		ocjeneStudenata.put("Iva", 4);
		ocjeneStudenata.put("Josip", 3);
		ocjeneStudenata.put("Marija", 5);
		
		ocjeneStudenata.clear();
		Assertions.assertEquals(0, ocjeneStudenata.size());
		Assertions.assertEquals(null, ocjeneStudenata.get("Marko"));
	}

}
