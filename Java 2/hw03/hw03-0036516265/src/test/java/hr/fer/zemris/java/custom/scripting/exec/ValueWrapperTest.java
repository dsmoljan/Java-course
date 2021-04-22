package hr.fer.zemris.java.custom.scripting.exec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValueWrapperTest {

	@Test
	public void test1() {
		ValueWrapper v1 = new ValueWrapper(null);
		ValueWrapper v2 = new ValueWrapper(null);
		v1.add(v2.getValue()); // v1 now stores Integer(0); v2 still stores null.
		
		Assertions.assertEquals(v1.getValue(), Integer.valueOf(0));
		
	}
	
	@Test
	public void test2(){
		ValueWrapper v3 = new ValueWrapper("1.2E1");
		ValueWrapper v4 = new ValueWrapper(Integer.valueOf(1));
		v3.add(v4.getValue()); // v3 now stores Double(13); v4 still stores Integer(1).
		
		Assertions.assertEquals(v3.getValue(), Double.valueOf(13));
	}
	
	@Test
	public void test3(){
		ValueWrapper v5 = new ValueWrapper("12");
		ValueWrapper v6 = new ValueWrapper(Integer.valueOf(1));
		v5.add(v6.getValue()); // v5 now stores Integer(13); v6 still stores Integer(1).
		
		Assertions.assertEquals(v5.getValue(), Integer.valueOf(13));
		
	}
	
	@Test
	public void test4(){
		ValueWrapper v7 = new ValueWrapper("Ankica");
		ValueWrapper v8 = new ValueWrapper(Integer.valueOf(1));
		
		Assertions.assertThrows(RuntimeException.class, () -> v7.add(v8.getValue()));
		
	}
	
	@Test
	public void test5(){
		ValueWrapper vv1 = new ValueWrapper(Boolean.valueOf(true));
		Assertions.assertThrows(RuntimeException.class, () -> vv1.add(Integer.valueOf(5))); // ==> throws, since current value is boolean
	}
	
	@Test
	public void test6(){
		ValueWrapper vv2 = new ValueWrapper(Integer.valueOf(5));
		
		Assertions.assertThrows(RuntimeException.class, () ->vv2.add(Boolean.valueOf(true))); 

		
	}

	
}
