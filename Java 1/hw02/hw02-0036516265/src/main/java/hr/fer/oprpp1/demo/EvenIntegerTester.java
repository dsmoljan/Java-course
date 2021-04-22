package hr.fer.oprpp1.demo;

import hr.fer.oprpp1.custom.collections.Tester;

public class EvenIntegerTester implements Tester{

	@Override
	public boolean test(Object obj) {
		if (!(obj instanceof Integer)) return false;
		Integer i = (Integer)obj;
		return i % 2 == 0;
	}

}
