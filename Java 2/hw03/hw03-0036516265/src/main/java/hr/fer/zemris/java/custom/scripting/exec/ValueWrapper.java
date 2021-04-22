package hr.fer.zemris.java.custom.scripting.exec;

import javax.management.RuntimeErrorException;

public class ValueWrapper {
	
	private Object value;
	
	public ValueWrapper(Object value) {
		this.value = value;
	}
	
	public void add(Object incValue) {
		calculate(incValue, "add");
		
	}
	public void subtract(Object decValue) {
		calculate(decValue, "subtract");
		
	}
	public void multiply(Object mulValue) {
		calculate(mulValue, "multiply");
		
	}
	public void divide(Object divValue) {
		calculate(divValue, "divide");
	}
	
	public int numCompare(Object withValue) {
		return calculate(withValue, "compare");
		
	}
	
//	private boolean checkIfCorrectType(Object other){
//		if ((value.getClass() != Integer.class && value.getClass() != Double.class && value.getClass() != String.class) || (
//				other.getClass() != Integer.class && other.getClass() != Double.class && other.getClass() != String.class)) {
//			return false;
//		}
//		return true;
//	}
	
	private int calculate(Object other, String operation) {
		
		if (value == null) {
			value = Integer.valueOf(0);
			System.out.println("Izvodim ovo...");
		}
		
		if (other == null) {
			other = Integer.valueOf(0);
		}
		
		if ((value.getClass() != Integer.class && value.getClass() != Double.class && value.getClass() != String.class) || (
				other.getClass() != Integer.class && other.getClass() != Double.class && other.getClass() != String.class)) {
			throw new RuntimeException("Classes incompatible! First operand class is : " + value.getClass().toString() + ", second operand class is : " + other.getClass().toString());
		}
		
		boolean bothDouble = false;
		
		double doubleFirst = 0;
		double doubleSecond = 0;
		
		int intFirst = 0;
		int intSecond = 0;
		
		//ove dvije metode se mogu spojiti u jednu
		if (value.getClass() == String.class) {
			String s  = (String)value;
			if (s.contains(".") || s.contains("E")) {
				try {
					value = Double.parseDouble((String)value);
				}catch(Exception e) {
					throw new RuntimeException("Error while converting first argument to double!");
				}
				bothDouble = true;
			}else {
				try {
					value = Integer.parseInt((String)value);
				}catch(Exception e) {
					throw new RuntimeException("Error while converting first argument to integer!");
				}
			}
		}
		
		if (other.getClass() == String.class) {
			String s  = (String)other;
			if (s.contains(".") || s.contains("E")) {
				try {
					other = Double.parseDouble((String)other);
				}catch(Exception e) {
					throw new RuntimeException("Error while converting second argument to double!");
				}
				bothDouble = true;
			}else {
				try {
					other = Integer.parseInt((String)other);
				}catch(Exception e) {
					throw new RuntimeException("Error while converting second argument to integer!");
				}
			}
		}
		
		if (value.getClass() == Double.class || other.getClass() == Double.class) {
			bothDouble = true;
		}
		
		if (bothDouble) {
			doubleFirst = Double.valueOf(value.toString());
			doubleSecond = Double.valueOf(other.toString());
		}else {
			intFirst = Integer.valueOf(value.toString());
			intSecond = Integer.valueOf(other.toString());
		}
		
		switch(operation)
		{
		case "add":
			if (bothDouble) {
				value = doubleFirst + doubleSecond;
			}else {
				value = intFirst + intSecond;
			}
			break;
		case "subtract":
			if (bothDouble) {
				value = doubleFirst - doubleSecond;
			}else {
				value = intFirst - intSecond;
			}
			break;
		case "multiply":
			if (bothDouble) {
				value = doubleFirst * doubleSecond;
			}else {
				value = intFirst * intSecond;
			}
			break;
		case "divide":
			if (bothDouble) {
				value = doubleFirst / doubleSecond;
			}else {
				value = intFirst / intSecond;
			}
			break;
		case "compare":
			if (bothDouble) {
				if (doubleFirst > (doubleSecond)) {
					return 1;
				}else if (doubleFirst < doubleSecond){
					return -1;
				}else {
					return 0;
				}
			}else {
				if (intFirst > intSecond) {
					return 1;
				}else if (intFirst < intSecond){
					return -1;
				}else {
					return 0;
				}
			}
		}
		
		return 0;
	
	//ovo što trebaš napraviti je https://refactoring.guru/design-patterns/strategy
	//tldr imati češ jednu metodu koja radi konverzije tipova po potrebi
	//njoj proslijedi argument tipa add, subtract, multiply, divide
	//kad jednom pripremi argumente, izvrši odgovarajuću operaciju te promijenti value i to je to

	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object o) {
		this.value = o;
		
	}
	
	
}
