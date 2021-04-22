package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class representing an operator
 * @author dorian
 *
 */
public class ElementOperator extends Element{
	
	private String symbol;
	
	public ElementOperator(String symbol) {
		this.symbol = symbol;
	}
	
	@Override
	public String asText() {
		if (symbol != null) {
			return this.symbol;
		} else {
			return "";
		}
	}

	public String getSymbol() {
		return symbol;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElementOperator)) {
			return false;
		}
		
		return this.symbol.equals(((ElementOperator)obj).getSymbol());
	}
	
	

}
