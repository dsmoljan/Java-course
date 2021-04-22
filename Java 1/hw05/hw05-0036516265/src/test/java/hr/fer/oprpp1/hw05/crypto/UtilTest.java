package hr.fer.oprpp1.hw05.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilTest {
	
	@Test
	public void testHexToByte() {
		String givenExample = "01aE22";
		byte[] array = Util.hextobyte(givenExample);
		
		byte[] expected = {1,-82,34};
		
		Assertions.assertArrayEquals(expected, array);
		
		String example1 = "0102030405";
		array = Util.hextobyte(example1);
		
		byte[] expected1 = {1,2,3,4,5};
		
		Assertions.assertArrayEquals(expected1, array);
		
		String example2 = "ef0005";
		byte[] array2 = Util.hextobyte(example2);

		
		byte[] expected2 = {-17,0,5};
		
		Assertions.assertArrayEquals(expected2, array2);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> Util.hextobyte("AABBC"), "Exception not thrown when given an odd number of hex characters!");
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> Util.hextobyte("ae00gg"), "Exception not thrown when given illegal characters!");

		String example3 = "";
		byte[] array3 = Util.hextobyte(example3);
		
		byte[] expected3 = {};
		
		Assertions.assertArrayEquals(expected3, array3);
		
	}
	
	@Test
	public void testByteToHex() {
		
		byte[] array = {1,-82,34};
		String string = Util.bytetohex(array);
		
		Assertions.assertEquals("01ae22", string);
		
		byte[] array2 = {1,2,3,4,5};
		string = Util.bytetohex(array2);
		
		Assertions.assertEquals("0102030405", string);
		
		byte[] array3 = {-17,0,5};
		string = Util.bytetohex(array3);
		
		Assertions.assertEquals("ef0005", string);
		
		byte[] array4 = {};
		string = Util.bytetohex(array4);
		
		Assertions.assertEquals("", string);
		
	}

}
