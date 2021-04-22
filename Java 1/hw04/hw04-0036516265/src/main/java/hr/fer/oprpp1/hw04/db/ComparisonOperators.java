package hr.fer.oprpp1.hw04.db;

/**
 * Contains concrete implementations of the string comparison strategy
 * @author dorian
 *
 */
public class ComparisonOperators {
	
	public static final IComparisonOperator LESS = ((s1,s2) -> s1.compareTo(s2) < 0);
	public static final IComparisonOperator LESS_OR_EQUALS = ((s1,s2) -> s1.compareTo(s2) <= 0);
	public static final IComparisonOperator GREATER = ((s1,s2) -> s1.compareTo(s2) > 0);
	public static final IComparisonOperator GREATER_OR_EQUALS = ((s1,s2) -> s1.compareTo(s2) >= 0);
	public static final IComparisonOperator EQUALS = ((s1,s2) -> s1.equals(s2));
	public static final IComparisonOperator NOT_EQUALS = ((s1,s2) -> !s1.equals(s2));
	//for LIKE - first argument: string to be checked, 2nd argument - pattern to be checked
	//* represents one or more character, eg. a* is a, ab, abc, abcd etc.
	
	/**
	 * First argument - string to be checked
	 * Second argument - pattern to be checked
	 */
	public static final IComparisonOperator LIKE = (new IComparisonOperator() {

		@Override
		public boolean satisfied(String string, String pattern) {
			
			boolean passedAsterisk = false;
			String patternMain = "";
			String patternCheck = "";
			char[] stringArray = string.toCharArray();
			char[] patternArray = pattern.toCharArray();

			//*ab -> npr. xxab LIKE *ab, ili ab LIKE ab
			//ab*ab -> npr. abxxxab LIKE ab*ab 
			//ab* -> npr. abxxx
			
			if (string.equals(pattern)) { //if the strings are equal, there is nothing to check, we return true
				return true;
			}
						
			//otherwise, there is either an asterisk present or LIKE is false
			
			for (int i = 0; i < patternArray.length; i++) {
				char curChar = patternArray[i];
				if (curChar == '*') {
					if (passedAsterisk) { //if we've come across a 2nd wildcard in the same string
						throw new RuntimeException("Error! Maximum numberof wildcards is 1!");
					}
					passedAsterisk = true;
					
					if (patternMain.length() == (pattern.length() - 1) && patternMain.equals(string.substring(0,i))) { //if we reach the end of the pattern and come across *, and the patterns match, eg. ab* like abeceda, we don't have to check anything else               
						return true;
					}
					
					//else, the * is either the first character, or somewhere in the middle
					
					//prvo pretpostavimo da * modelira 0 znakova, dakle ne preskačemo ništa i uspoređujemo do kraja stringa
					//zatim pretpostavimo da * modelira 1 znak, preskačemo 1 znak i uspoređujemo ostatak stringa
					//zatim za 2, pa za 3 itd itd.
					//ako dođemo do kraja, tj. provjerimo ostatak niza i pattern i string i dalje nisu jednaki, možemo vratiti false
					
					int j = 0;
					patternCheck = patternMain;
					String fullPattern = patternMain + pattern.substring(i+1,pattern.length()); //pattern without *
					
					while (i+j < stringArray.length) {
//						for (int k = i+j; k < string.length(); k++) {
//							patternCheck += stringArray[k]; 
//						}
						
						patternCheck = patternMain + string.substring(i+j); //provjeri...
						if (fullPattern.equals(patternCheck)){
							return true;
						}
						
						j++; //preskacemo jedan znak vise
					}
					return false;
				}else {
					patternMain += curChar;
					if (!(patternMain.equals(string.substring(0, patternMain.length())))) { //i.e abx* LIKE abeceda -> once patternMain becomes abx, we compare it to abc and see they're not equal
						return false;
					}
				}
				
				
			}
			
			return false;
		}
		
	}); 

}
