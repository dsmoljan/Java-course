package hr.fer.oprpp1.hw02.prob1;


/**
 * Class which models a token. Token is a lexical unit which groups one or more character of the original code, etc. decimal numbers, identificators, keywords etc.
 * @author dorian
 *
 */
public class Token {
	private TokenType type;
	private Object value;
	
	public Token(TokenType type, Object value) {
		this.type = type;
		this.value = value;
		
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public TokenType getType() {
		return this.type;
	}

}
