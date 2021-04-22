package hr.fer.oprpp1.hw05.shell;

import java.util.SortedMap;

/**
 * Abstract interface declaring methods passed to each defined command. 
 * The each implemented command communicates with user only through this interface.
 * @author Dorian
 *
 */
public interface Enviroment {
	
	/**
	 * Used for reading from console
	 * @return
	 * @throws ShellIOException
	 */
	String readLine() throws ShellIOException;
	
	/**
	 * Writes the given text to the console (possibly as multiple lines?)
	 * @param text
	 * @throws ShellIOException
	 */
	void write(String text) throws ShellIOException;
	
	/**
	 * Writes the given line of text as a single line to the console.
	 * @param text
	 * @throws ShellIOException
	 */
	void writeln(String text) throws ShellIOException;
	
	/**
	 * Returns the list of commands supported by this enviroment.
	 * @return
	 */
	SortedMap<String, ShellCommand> commands();
	
	/**
	 * Returns the symbol currently used as the multiline symbol.
	 * @return
	 */
	Character getMultilineSymbol();
	
	
	/**
	 * Sets a new multilines symbol.
	 * @param symbol
	 */
	void setMultiLinesSymbol(Character symbol);
	
	/**
	 * Returns the symbol currently used as the prompt symbol.
	 * @return
	 */
	Character getPromptSymbol();
	
	/**
	 * Sets a new prompt symbol.
	 * @param symbol
	 */
	void setPromptSymbol(Character symbol);
	
	/**
	 * Returns the symbol currently used as the morelines symbol.
	 * @return
	 */
	Character getMorelinesSymbol();
	
	/**
	 * Sets a new morelines symbol.
	 * @param symbol
	 */
	void setMorelinesSymbol(Character symbol);
	

}
