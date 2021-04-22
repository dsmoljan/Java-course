package hr.fer.zemris.java.custom.scripting.parser;

import java.util.EmptyStackException;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.lexer.LexerState;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexer;
import hr.fer.zemris.java.custom.scripting.lexer.Token;
import hr.fer.zemris.java.custom.scripting.lexer.TokenType;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;

public class SmartScriptParser {

	private String body;
	private SmartScriptLexer lexer;
	private ObjectStack stack = new ObjectStack();
	private Token token;
	private boolean outsideOfTag = true;
	private DocumentNode documentNode;

	public SmartScriptParser(String body) {
		this.lexer = new SmartScriptLexer(body);
		stack.push(new DocumentNode());

		token = lexer.nextToken();

		while (lexer.getToken().getType() != TokenType.EOF) {
			
			if (outsideOfTag) { 
				processTextOutsideTag();
			}

			if (!outsideOfTag) {
				processTextInsideTag();
			}

		}

		if (stack.peek() == null) {
			throw new SmartScriptParserException("Too many closing tags!");
		}
		try {
			this.documentNode = (DocumentNode)stack.pop();
		} catch(ClassCastException e) {
			throw new SmartScriptParserException("A tag has not been properly closed!");
		}
	}


	private void processTextOutsideTag() {
		String value = "";

		while (outsideOfTag && lexer.getToken().getType() != TokenType.EOF) {

			if (token.getType() == TokenType.STRING) {
				value += (String)token.getValue();
				//System.out.println(value);
				token = lexer.nextToken();
			} else {								
				char chr = (char)token.getValue();
				if (chr == '\\'){					
					token = lexer.nextToken();
					if (token.getType() == TokenType.STRING) { 
						throw new SmartScriptParserException("Parsing error - invalid escape sequence \\" + token.getValue().toString().charAt(0));
					}
					chr = (char)token.getValue();
					if (chr == '\\' || chr == '{') {
						value += chr; 
						token = lexer.nextToken();
					} else {
						throw new SmartScriptParserException("Parsing error - invalid escape sequence \\" + chr);
					}
				} else if (chr == '{') {
					token = lexer.nextToken();
					if (token.getType() == TokenType.STRING) {
						value += '{';
						value += token.getValue();
						token = lexer.nextToken();
					} else {
						chr = (char)token.getValue();

						if (chr == '$') { 								
							outsideOfTag = false;
							lexer.setState(LexerState.TAG);
							
						} else { 										
							value += '{';
							value += chr;
							token = lexer.nextToken();
						}
					}

				} else {
					value += chr;
					token = lexer.nextToken();
				}
			}
		} 

		Node node = (Node)stack.pop();		
		TextNode textNode = new TextNode(value);
		node.addChild(textNode);
		stack.push(node);
		if (token.getType() != TokenType.EOF){
			token = lexer.nextToken();
		}
	}

	private void processTextInsideTag() {
		//System.out.println("Token value is " + token.getValue().toString());

		if (token.getType() == TokenType.KEYWORD && ((String)token.getValue()).equals("FOR")) {
			int noOfParameters = 0;
			ElementVariable var = null;
			Element startExpression = null;
			Element endExpression = null;
			Element stepExpression = null;

			token = lexer.nextToken();

			while (!outsideOfTag && token.getType() != TokenType.EOF && token.getType() != TokenType.SYMBOL) {
				if (token.getType() == TokenType.EOF) { 
					throw new SmartScriptParserException("Error while parsing - for tag not closed!");
				}

				switch(noOfParameters) {
				case 0:
					if (token.getType() == TokenType.VAR) {
						noOfParameters++;
						var = new ElementVariable((String)token.getValue());
					} else {
						throw new SmartScriptParserException("Error while parsing - invalid argument in for loop!");
					}
					break;
				case 1:		
					startExpression = checkAndSetForLoopArgument();
					noOfParameters++;
					break;
				case 2:
					endExpression = checkAndSetForLoopArgument();
					noOfParameters++;
					break;
				case 3:
					stepExpression = checkAndSetForLoopArgument();
					noOfParameters++;
					break;
				case 4:
					throw new SmartScriptParserException("Too many for loop arguments!");
				}
				token = lexer.nextToken();


			}
			if (noOfParameters < 3) {
				throw new SmartScriptParserException("Too few for loop arguments!");
			}
			ForLoopNode forLoopNode = new ForLoopNode(var, startExpression, endExpression, stepExpression);
			try {
				Node parentNode = (Node)stack.pop();
				parentNode.addChild(forLoopNode);
				stack.push(parentNode);
				stack.push(forLoopNode);
			} catch (EmptyStackException e) {
				throw new SmartScriptParserException("Too many closing tags!");
			}

			//check if tag is closed properly
			checkIfTagIsClosedOK();

		} else if (token.getType() == TokenType.KEYWORD && ((String)token.getValue()).equals("END")) { // for end tag
			
			try {
				stack.pop();
			} catch (EmptyStackException e) {
				throw new SmartScriptParserException("Too many closing tags!");
			}
			
			token = lexer.nextToken();
			checkIfTagIsClosedOK();
		} else if (token.getType() == TokenType.OPERATOR && (char)token.getValue() == '=') { //for echo node
			token = lexer.nextToken();
			ArrayIndexedCollection elementsArray = new ArrayIndexedCollection();
			
			while (token.getType() != TokenType.SYMBOL && !outsideOfTag && token.getType() != TokenType.EOF) {
				if (token.getType() == TokenType.VAR) {
					elementsArray.add(new ElementVariable((String)token.getValue()));
				} else if (token.getType() == TokenType.CONSTANT_DOUBLE) {
					try {
						elementsArray.add(new ElementConstantDouble((Double)token.getValue()));
					} catch(NumberFormatException e) {
						throw new SmartScriptParserException("Error while parsing number in echo node!");
					}
				} else if (token.getType() == TokenType.CONSTANT_INTEGER) {
					try {
						elementsArray.add(new ElementConstantInteger((Integer)token.getValue())); //bilo token.getValue()
					} catch(NumberFormatException e) {
						System.out.println(token.getValue().toString());
						throw new SmartScriptParserException("Error while parsing number in echo node!");
					}
				} else if (token.getType() == TokenType.FUNCTION) {
					elementsArray.add(new ElementFunction((String)token.getValue()));
				} else if (token.getType() == TokenType.OPERATOR) {
					elementsArray.add(new ElementOperator(String.valueOf((char)token.getValue())));
				} else if (token.getType() == TokenType.STRING) {
					elementsArray.add(new ElementString((String)token.getValue()));
				} else {
					throw new SmartScriptParserException("Error while parsing echo node - unsupported character!");
				}
				
				//System.out.println("Current token value is " + token.getValue().toString());
				token = lexer.nextToken();
			}
			
			Element[] array = new Element[elementsArray.size()];
			for (int i = 0; i < elementsArray.size(); i++) {
				array[i] = (Element)elementsArray.get(i);
			}
			//Element[] array = (Element[]) elementsArray.toArray(); //ako nesto nece raditi, 99% da je ovdje pogreška
			EchoNode echoNode = new EchoNode(array);
			
			try {
				Node parentNode = (Node)stack.pop();
				parentNode.addChild(echoNode);
				stack.push(parentNode);
			} catch (EmptyStackException e) {
				throw new SmartScriptParserException("Too many closing tags!");
			}
			
			
			checkIfTagIsClosedOK();
		}
	}

	/**
	 * Private method, used to check and set arguments of for tag.
	 * @param element
	 */
	private Element checkAndSetForLoopArgument() {
		if (token.getType() == TokenType.VAR) {
			return new ElementVariable((String)token.getValue());
		} else if (token.getType() == TokenType.STRING) {
			return new ElementString((String)token.getValue());
		} else if (token.getType() == TokenType.CONSTANT_DOUBLE) {
			try {
				return new ElementConstantDouble(((Double)token.getValue()).doubleValue());
			} catch (NumberFormatException ex) {
				throw new SmartScriptParserException("Error while attempting to parse double (for loop argument)!");
			}
		} else if (token.getType() == TokenType.CONSTANT_INTEGER) {
			try {
				return new ElementConstantInteger(((Integer)token.getValue()).intValue());
			} catch (NumberFormatException ex) {
				throw new SmartScriptParserException("Error while attempting to parse integer (for loop argument)!");
			}
		} else {
			throw new SmartScriptParserException("Invalid type of for loop argument - allowe types are variable, string and number");
		}
	}

	/**
	 * Private method used for checking if tag is closed in an ok way ($})
	 * It also gives control to the method <code>processTextOutsideTag</code> and gets the next suitable token
	 */
	private void checkIfTagIsClosedOK() {
		
		if (token.getType() == TokenType.SYMBOL && (char)token.getValue() == '$') {
			token = lexer.nextToken();
			if (token.getType() == TokenType.SYMBOL && (char)token.getValue() == '}') {
				this.outsideOfTag = true;
				lexer.setState(LexerState.TEXT);
				token = lexer.nextToken();
			} else {
				throw new SmartScriptParserException("Tag not properly closed!");
			}
		} else {
			throw new SmartScriptParserException("Tag not properly closed!");		
		}



	}
	
	public DocumentNode getDocumentNode() {
		return this.documentNode;
	}
}

	
