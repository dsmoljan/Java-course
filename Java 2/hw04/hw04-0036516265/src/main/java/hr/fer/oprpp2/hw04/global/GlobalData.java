package hr.fer.oprpp2.hw04.global;

import java.util.Random;

/**
 * Globalni podaci dijeljeni na razini čitave web-aplikacije
 * @author Dorian
 *
 */
public class GlobalData {
	private Random random = new Random();
	
	public GlobalData() {
		
	}
	
	public synchronized int createRandomInt(int lowerBound, int upperBound) {
		if(lowerBound > upperBound) throw new IllegalArgumentException(String.format("lowerBound (was %d) can not be bigger than upper bound (was %d)!", lowerBound, upperBound));
		int intervalSize = upperBound - lowerBound + 1;
		if(intervalSize == 1) return lowerBound;
		return lowerBound + random.nextInt(intervalSize);
	}

}
