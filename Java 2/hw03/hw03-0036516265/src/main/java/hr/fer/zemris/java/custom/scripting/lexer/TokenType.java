package hr.fer.zemris.java.custom.scripting.lexer;

public enum TokenType {
	EOF, //ok
	CONSTANT_DOUBLE, //ok
	CONSTANT_INTEGER, //ok
	FUNCTION,  //
	OPERATOR, //ok
	STRING, //ok, ekvivalent word iz prošlog leksera, ovdje stavljaj i ono što će parser kasnije identificirati da su varijable
	KEYWORD,  //ok, for i end, treba li ih razdvojiti, nemamo ih u elementima jer nam oni služe kao smjernice za parsiranje
	SYMBOL,   //za {,},$, .,
	VAR


	
	

}
