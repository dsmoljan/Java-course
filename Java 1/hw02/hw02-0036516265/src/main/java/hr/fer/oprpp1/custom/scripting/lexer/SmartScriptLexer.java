package hr.fer.oprpp1.custom.scripting.lexer;

import hr.fer.oprpp1.custom.scripting.parser.SmartScriptParserException;
import hr.fer.oprpp1.hw02.prob1.LexerException;
/**
 * Lexer which performs lexsic analyis of SmartScript
 * @author dorian
 *
 */
public class SmartScriptLexer {

	private char[] data;		//input text (original code)
	private Token token;		//current token
	private int currentIndex;	//index of the first unprocessed character
	private boolean passedEOF;
	private LexerState currentState;


	public SmartScriptLexer(String text) {
		if (text == null) {
			throw new NullPointerException("Input text must not be null!");
		}

		this.data = text.trim().toCharArray(); //removing leading and trailing blanks and converting String to char array
		this.currentIndex = 0;
		this.passedEOF = false;
		this.currentState = LexerState.TEXT;

	}

	/**
	 * Generates and returns next token
	 * @return next token
	 * @throws <code>LexerException</code>
	 */
	public Token nextToken() {

		//if we try calling nextToken once we've already passedEOF, throw an exception
		if (this.passedEOF) {
			throw new LexerException("Cannot call nextToken as you've reached the end of the text!");
		}

		if (currentIndex >= data.length) {
			this.passedEOF = true;
			this.token = new Token(TokenType.EOF, null);
			return this.token;
			//System.out.println("Creating EOF token");
		}
		if (this.currentState == LexerState.TAG) {
			try {
				this.token = nextTokenTag();
			} catch (LexerException e) {
				throw new SmartScriptParserException(e.getMessage());
			}
			return this.token;
		} else {
			try {
				this.token = nextTokenText();
			}catch (LexerException e) {
				throw new SmartScriptParserException(e.getMessage());
			}
			return this.token;
		}
	}

	/**
	 * returns last token generated, can be called multiple times, doesn't start tokenizing next token
	 * @return last token generated
	 */
	public Token getToken() {
		return this.token;
	}

	public void setState(LexerState state) {
		if (state == null) {
			throw new NullPointerException("Lexer state can't be null!");
		}
		this.currentState = state;

	}

	/**
	 * Private method used to skip \n, \r, \t, and ' ' in text
	 */
	private void skipBlankSpaces() {

		while (currentIndex < data.length && (data[currentIndex] == ' ' || data[currentIndex] == '\n' || data[currentIndex] == '\r' || data[currentIndex] == '\t')) {
			this.currentIndex++;
		}

	}

	/**
	 * Private method which processes text and returns tokens according to TAG lexer mode.
	 * @return
	 */
	private Token nextTokenTag() {
		this.skipBlankSpaces();

		//check if next token is a variable(identificator) or a keyword - string without ""
		if (currentIndex < data.length && Character.isLetter(data[currentIndex])) {
			String value = "";

			while (currentIndex < data.length && (Character.isLetter(data[currentIndex]) || Character.isDigit(data[currentIndex]) || data[currentIndex] == '_')) {
				value += data[currentIndex];
				currentIndex++;
			}

			if (value.toUpperCase().equals("FOR") || value.toUpperCase().equals("END")) { //provjeri radi li se o ključnoj riječi
				this.token = new Token(TokenType.KEYWORD, value.toUpperCase());
				return this.token;
			} else {
				//System.out.println("Creating token with value " + value.toUpperCase());
				this.token = new Token(TokenType.VAR, value); //inače, radi se o identifikatoru
				return this.token;
			}
		} else if (currentIndex < data.length && (Character.isDigit(data[currentIndex])) ||  //check if it is a number
				(currentIndex < data.length - 1 && data[currentIndex] == '-' && Character.isDigit(data[currentIndex + 1]))) {
			boolean doubleFlag = false;
			String value = "";

			while((currentIndex < data.length && (Character.isDigit(data[currentIndex])) || 
					(currentIndex < data.length - 1 && data[currentIndex] == '-' && Character.isDigit(data[currentIndex + 1]))) ||
					(currentIndex < data.length - 1 && data[currentIndex] == '.' && Character.isDigit(data[currentIndex + 1]))) {
				if (data[currentIndex] == '-') { //i.e -1, -2 etc.
					value += data[currentIndex];
					currentIndex++;
					value += data[currentIndex];
					currentIndex++;
				} else if (data[currentIndex] == '.') { //i.e 5.3, 2.1 etc
					value += data[currentIndex];
					currentIndex++;
					value += data[currentIndex];
					currentIndex++;
					doubleFlag = true;
				} else {
					value += data[currentIndex];
					currentIndex++;
				}
			}

			if (doubleFlag) {
				try {
					this.token = new Token(TokenType.CONSTANT_DOUBLE, Double.parseDouble(value));
					return this.token;
				} catch (NumberFormatException ex) {
					throw new LexerException("Error while parsing number as double!");
				}
			} else {
				try {
					this.token = new Token(TokenType.CONSTANT_INTEGER, Integer.parseInt(value));
					return this.token;
				} catch (NumberFormatException ex) {
					throw new LexerException("Error while parsing number as integer!");
				}
			}
		} else if (currentIndex < data.length && data[currentIndex] == '"') { //check if next token is a string
			//boolean hasClosedParenthesis = false;
			currentIndex++;
			
			String value = "";
			
			while (currentIndex < data.length && data[currentIndex] != '"') {
				if (currentIndex < data.length - 1 && data[currentIndex] == '\\') {
					if (data[currentIndex + 1] == '\\' || data[currentIndex + 1] == '"') { 
						value += data[currentIndex + 1];
						currentIndex += 2;
					} else if (data[currentIndex + 1] == 'n') {
						value += "\n";
						currentIndex += 2;
					} else if (data[currentIndex + 1] == 'r') {
						value += "\r";
						currentIndex += 2;
					} else if (data[currentIndex + 1] == 't') {
						value += "\t";
						currentIndex += 2;
					}else {
						throw new LexerException("Invalid escape sequence while parsing string!");
					}
				} else {
					value += data[currentIndex];
					currentIndex++;
				}
			}
			
			currentIndex++;
			this.token = new Token(TokenType.STRING, value);
			return this.token;
		} else if (currentIndex < data.length && (data[currentIndex] == '+' || data[currentIndex] == '-' || data[currentIndex] == '*' || //check if next token is an operator
				data[currentIndex] == '/' || data[currentIndex] == '^' || data[currentIndex] == '=')){
			
			this.token = new Token(TokenType.OPERATOR, data[currentIndex]);
			currentIndex++;
			return this.token;
		} else if (currentIndex < data.length && (data[currentIndex] == '{' || data[currentIndex] == '}' //check if next token is a symbol
					|| data[currentIndex] == '$')) {
			
			this.token = new Token(TokenType.SYMBOL, data[currentIndex]);
			currentIndex++;
			return this.token;
		}else if (currentIndex < data.length - 1 && data[currentIndex] == '@' && Character.isLetter(data[++currentIndex])) { //check if next token is a function
			String value = "";
			
			while(currentIndex < data.length && (Character.isLetter(data[currentIndex]) || Character.isDigit(data[currentIndex]) || data[currentIndex] == '_')) {
				value += data[currentIndex];
				currentIndex++;
			}
			
			this.token = new Token(TokenType.FUNCTION, value);
			return this.token;
		}
		else {
			throw new LexerException("Cannot parse next token!");
		}

	}

	/**
	 * Private method which processes text and returns tokens according to TEXT lexer mode.
	 * @return
	 */
	private Token nextTokenText() {

		if ((currentIndex < data.length && data[currentIndex] != '{' && data[currentIndex] != '}'
				&& data[currentIndex] != '$' && data[currentIndex] != '\\')) {
			String value = "";

			while (currentIndex < data.length && data[currentIndex] != '{' && data[currentIndex] != '}'
					&& data[currentIndex] != '$' && data[currentIndex] != '\\') {
				value += data[currentIndex];
				currentIndex++;
			}

			return new Token(TokenType.STRING, value);

		}else {
			return new Token(TokenType.SYMBOL, data[currentIndex++]);
		}
	}



}
