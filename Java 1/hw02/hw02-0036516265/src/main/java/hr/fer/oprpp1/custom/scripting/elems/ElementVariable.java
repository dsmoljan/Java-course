package hr.fer.oprpp1.custom.scripting.elems;

/**
 * Class representing a variable
 * @author dorian
 *
 */
public class ElementVariable extends Element{
	
	private String name;
	
	public ElementVariable(String name) {
		this.name = name;
	}
	
	@Override
	public String asText() {
		if (this.name != null) {
			return this.name;
		} else {
			return "";
		}
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElementVariable)) {
			return false;
		}
		
		return this.name.equals(((ElementVariable)obj).getName());
	}
	
	
	
	

}
