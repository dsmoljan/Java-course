package hr.fer.zemris.java.custom.scripting.elems;


/**
 * Class representing constant of type double
 * @author dorian
 *
 */
public class ElementConstantDouble extends Element{
	
	private double value;
	
	public ElementConstantDouble(double value) {
		this.value = value;
	}
	
	@Override
	public String asText() {
		return Double.toString(this.value);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElementConstantDouble)) {
			return false;
		}
		
		return this.value == ((ElementConstantDouble)obj).getValue();
	}

	public double getValue() {
		return value;
	}

}
