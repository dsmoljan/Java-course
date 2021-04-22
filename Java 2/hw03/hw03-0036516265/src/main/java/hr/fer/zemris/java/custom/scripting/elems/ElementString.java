package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class representing datatype string
 * @author dorian
 *
 */
public class ElementString extends Element{
	
	private String value;
	
	public ElementString(String value) {
		this.value = value;
	}
	
	@Override
	public String asText() {

		if (this.value != null) {
			String res = this.value;
			res = res.replace("\\", "\\\\");
			res = res.replace("\"", "\\\"");
			res += "\"";
			return "\"" + res;
		} else {
			return "";
		}
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElementString)) {
			return false;
		}
		
		return this.value.equals(((ElementString)obj).getValue());
	}
	
	

}
