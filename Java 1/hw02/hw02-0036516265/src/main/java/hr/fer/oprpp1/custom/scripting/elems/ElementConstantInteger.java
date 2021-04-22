package hr.fer.oprpp1.custom.scripting.elems;

/**
 * Class representing constant of type integer
 * @author dorian
 *
 */
public class ElementConstantInteger extends Element{

	private int value;
	
	public ElementConstantInteger(int value) {
		this.value = value;
	}
	
	@Override
	public String asText() {
		return Integer.toString(this.value);
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElementConstantInteger)) {
			return false;
		}
		
		return this.value == ((ElementConstantInteger)obj).getValue();
	}
}
