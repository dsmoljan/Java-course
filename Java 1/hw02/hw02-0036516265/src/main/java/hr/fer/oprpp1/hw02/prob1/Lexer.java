package hr.fer.oprpp1.hw02.prob1;

/**
 * Class which models a simple lexsic analyzer. The analyzer takes as an input the original program code (we want to "translate", execute etc.)
 * and outputs a list of tokens.
 * @author dorian
 *
 */
public class Lexer {
	
	private char[] data;		//input text (original code)
	private Token token;		//current token
	private int currentIndex;	//index of the first unprocessed character
	private boolean passedEOF;
	private LexerState currentState;
	
	//constructor takes as an argument the text we wish to tokenize
	public Lexer(String text) {
		if (text == null) {
			throw new NullPointerException("Input text must not be null!");
		}
		
		this.data = text.trim().toCharArray(); //removing leading and trailing blanks and converting String to char array
		this.currentIndex = 0;
		this.passedEOF = false;
		this.currentState = LexerState.BASIC;
		
		//this.token = new Token();
	}
	
	/**
	 * Generates and returns next token
	 * @return next token
	 * @throws <code>LexerException</code>
	 */
	public Token nextToken() {
		
		this.skipBlankSpaces();

		
		//char currentChar = null;
		
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
		
		//this.skipBlankSpaces();
		
		if (this.currentState == LexerState.BASIC) {
			//check if token is a word
			if ((currentIndex < data.length && (Character.isLetter(data[currentIndex]))) ||
					(currentIndex < data.length - 1 && data[currentIndex] == '\\' && (Character.isDigit(data[currentIndex + 1]) || data[currentIndex + 1] == '\\'))) {
				String value = "";
				
				while (currentIndex < data.length && Character.isLetter(data[currentIndex]) ||
						(currentIndex < data.length - 1 && data[currentIndex] == '\\' && (Character.isDigit(data[currentIndex + 1]) || data[currentIndex + 1] == '\\'))) {
					
					if (data[currentIndex] == '\\') {
						value += data[currentIndex + 1];
						currentIndex += 2;
					} else {
						value += data[currentIndex];
						currentIndex++;
					}

				}
				//this.skipBlankSpaces();
				this.token = new Token(TokenType.WORD, value);
				return this.token;
			} else if ((currentIndex < data.length && (Character.isDigit(data[currentIndex])))) {
				
				String value = "";
				while((currentIndex < data.length && (Character.isDigit(data[currentIndex])))){
					value += data[currentIndex];
					currentIndex++;
					}
				
				long res = 0;
				try {
					res = Long.parseLong(value);
				} catch (NumberFormatException ex) {
					throw new LexerException("Error - cannot parse " + value + " as long!");
				}
				
				this.skipBlankSpaces();
				this.token = new Token(TokenType.NUMBER, res);
				return this.token;
			} else if (data[currentIndex] == '\\') { //we've already checked all legal escape sequences for this token above, so if we reach another \, we know it is illegal
				throw new LexerException("Invalid escape sequence!");
			} else {
				this.token = new Token(TokenType.SYMBOL, data[currentIndex++]);
				return this.token;
			}
			
		} else {
			if (currentIndex < data.length && data[currentIndex] != '#') {
				String value = "";
				
				while (currentIndex < data.length && data[currentIndex] != '#' && data[currentIndex] != ' ') {
					value+= data[currentIndex++];
				}
				
				this.token = new Token(TokenType.WORD, value);
				return this.token;
			} else if (currentIndex < data.length && data[currentIndex] == '#') {
				currentIndex++;
				this.token = new Token(TokenType.SYMBOL, '#');
				return this.token;
			} else {
				throw new LexerException("Error! Cannot process next character!");
			}
			
			
			
		}
		


	}
	
	/**
	 * returns last token generated, can be called multiple times, doesn't start tokenizing next token
	 * @return last token generated
	 */
	public Token getToken() {
		return this.token;
	}
	
	//lekser treba raditi kao lijeni - tako da grupiranje znakova tj. ekstrakcju svakog sljedećeg tokena radi tek kad ga se to eksciplitno zatraži pozivom metode za dohvat sljedećeg tokena
	
	/**
	 * Private method used to skip \n, \r, \t, and ' ' in text
	 */
	private void skipBlankSpaces() {
		
		while (currentIndex < data.length && (data[currentIndex] == ' ' || data[currentIndex] == '\n' || data[currentIndex] == '\r' || data[currentIndex] == '\t')) {
			this.currentIndex++;
		}
		
	}

	public void setState(LexerState state) {
		if (state == null) {
			throw new NullPointerException("Lexer state can't be null!");
		}
		this.currentState = state;
		
	}
	
}
