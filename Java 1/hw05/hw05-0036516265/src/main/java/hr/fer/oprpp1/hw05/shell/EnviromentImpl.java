package hr.fer.oprpp1.hw05.shell;

import java.util.Collections;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * An implementation of the Enviroment interface. Works with standard input and output (command prompt, linux terminal etc.)
 * @author Dorian
 *
 */
class EnviromentImpl implements Enviroment{
	
	private Character multilineSymbol;
	private Character promptSymbol;
	private Character morelinesSymbol;
	private SortedMap<String, ShellCommand> commandMap;
	
	private Scanner sc;
	
	/**
	 * 
	 * @param multilineSymbol
	 * @param promptSymbol
	 * @param morelinesSymbol
	 */
	@SuppressWarnings("unchecked")
	public EnviromentImpl(Character multilineSymbol, Character promptSymbol, Character morelinesSymbol, SortedMap commands) {
		this.multilineSymbol = multilineSymbol;
		this.promptSymbol = promptSymbol;
		this.morelinesSymbol = morelinesSymbol;
		this.sc = new Scanner(System.in);
		
		this.commandMap = Collections.unmodifiableSortedMap(commands);

	}

	@Override
	public String readLine() throws ShellIOException {
		return sc.nextLine();
	}

	//mislim da je jedina razlika između write i write line ta što write nema \n na kraju, a writeln
	@Override
	public void write(String text) throws ShellIOException {
		System.out.print(text);
		
	}

	@Override
	public void writeln(String text) throws ShellIOException {
		System.out.println(text);
		
	}

	@Override
	public SortedMap<String, ShellCommand> commands() {
		return this.commandMap;
	}

	@Override
	public Character getMultilineSymbol() {
		return this.multilineSymbol;
	}

	@Override
	public void setMultiLinesSymbol(Character symbol) {
		this.multilineSymbol = symbol;
		
	}

	@Override
	public Character getPromptSymbol() {
		return this.promptSymbol;
	}

	@Override
	public void setPromptSymbol(Character symbol) {
		this.promptSymbol = symbol;
		
	}

	@Override
	public Character getMorelinesSymbol() {
		return this.morelinesSymbol;
	}

	@Override
	public void setMorelinesSymbol(Character symbol) {
		this.morelinesSymbol = symbol;
		
	}
	
}