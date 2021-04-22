package hr.fer.oprpp1.hw05.crypto;


/**
 * Class containing methods used to convert String hex data
 * to binary form and vice versa.
 * @author Dorian
 *
 */
public class Util {
	
	/**
	 * Method takes in a hex-encoded String and returns it as a byte array.
	 * Hex encoded string is converted to a byte array using the 2nd complement.
	 * @param keyText
	 * @return
	 */
	public static byte[] hextobyte(String keyText) {
		
		char[] keyTextChar = keyText.toCharArray();
		byte[] resultArray = new byte[(keyText.length()/2)];
		if (keyText.length()%2 != 0) {
			throw new IllegalArgumentException("String's length isn't even!");
		}
		
		for (int i = 0; i < keyText.length(); i += 2) {
			if ((Character.isLetter(keyTextChar[i]) || Character.isDigit(keyTextChar[i])) &&
					(Character.isLetter(keyTextChar[i + 1]) || Character.isDigit(keyTextChar[i + 1]))) {
				
				//koristiti činjenicu da znaš da A ima ASCII vrijednost 65, B 66 itd. sve do F
				//onda od A (65) oduzmeš 55 i dobiješ 10, od B(66) oduzmeš 55 dobiješ 11 itd.
				//i onda to samo pretvoriš u byte i dobiješ taj broj zapisan u binarnom obliku
				//a brojeve možeđ odmah u byte pretvoriti
				
				int firstCharInt = 0;
				int secondCharInt = 0;
				
				firstCharInt = hexToDec(keyTextChar[i]); //i.e converts a or A to 10, b or B to 11, 1 to 1 etc.
				secondCharInt = hexToDec(keyTextChar[i + 1]);
				
				firstCharInt = firstCharInt << 4; //mičemo bitove prvog dijela byte-a na odgovarajuće mjesto
				
				resultArray[i/2] = (byte) (firstCharInt + secondCharInt);
				
				
			} else {
				throw new IllegalArgumentException("Unsupported character! Allowed characters are ASCII letters and digits 0 - 9!");
			}
		}
		
		
		return resultArray;
	}
	
	/**
	 * Method takes in a byte array and returns it as a hex-encoded String.
	 * @param byteArray
	 * @return
	 */
	public static String bytetohex(byte[] byteArray) {
		
		String s = "";
		
		for (byte b : byteArray) {
			int firstChar = 0;
			int secondChar = 0;
			
			firstChar =   (0xf0 & b)>>4; 
			secondChar =   0x0f & b;
			
			s += decToHex(firstChar);
			s += decToHex(secondChar);
			
			
		}
		
		
		return s;
	}
	
	/**
	 * Private method used to convert a number from base 16 to base 10
	 * @param c
	 * @return
	 */
	private static int hexToDec(char c) {
		
		int res = 0;
		
		if (Character.isLetter(c)) {
			res = (int) Character.toUpperCase(c);
			res -= 55;
			
			if (res > 15) {
				throw new IllegalArgumentException("Allowed characters are A-F and digits 0-9!");
			}
		} else if (Character.isDigit(c)) {
			res = (int) c;
			res -= 48;
			
			if (res > 9) {
				throw new IllegalArgumentException("Allowed characters are A-F and digits 0-9!");
			}
		}
		
		return res;
	}
	
	private static char decToHex(int n) {
		
		char res = 0;
		
		
		if (n >= 0 && n <= 9) {
			res = (char) (n + 48);
		} else if (n >= 10 && n <= 15) {
			res = (char) (n + 87);
		}
		
		return res;
	}

}
