package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class representing a function
 * @author dorian
 *
 */
public class ElementFunction extends Element{
	
	private String name;
	
	public ElementFunction(String name) {
		this.name = name;
	}
	
	@Override
	public String asText() {
		if (this.name != null) {
			return "@" + this.name;
		} else {
			return "";
		}
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElementFunction)) {
			return false;
		}
		
		return this.name.equals(((ElementFunction)obj).getName());
	}
	
	

}
