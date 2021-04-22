package hr.fer.oprpp1.hw05.shell.commands;

import java.util.ArrayList;
import java.util.List;

import hr.fer.oprpp1.hw05.shell.Enviroment;
import hr.fer.oprpp1.hw05.shell.ShellCommand;
import hr.fer.oprpp1.hw05.shell.ShellStatus;

public class ChangeSymbolShellCommand implements ShellCommand{

	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {
		String[] argumentsList = arguments.split(" ");
		
		boolean getSymbol = false;
		Character newSymbol = null;
		
		if (argumentsList.length == 1) {
			getSymbol = true;
		} else if (argumentsList.length == 2) {
			getSymbol = false;
			newSymbol = argumentsList[1].charAt(0);
		} else {
			env.writeln("Error! Invalid number of arguments for comman symbol! Write 'help symbol' for more information about this command");
		}
		
		switch(argumentsList[0]) {
		case "PROMPT":
			if (getSymbol) {
				env.writeln("Symbol for PROMPT is: " + Character.toString(env.getPromptSymbol()));
			}else {
				String oldSymbol = env.getPromptSymbol().toString();
				env.setPromptSymbol(newSymbol);
				env.writeln("Symbol for PROMPT changed from " + oldSymbol + " to " + env.getPromptSymbol());
			}
			break;
		case "MORELINES":
			if (getSymbol) {
				env.writeln("Symbol for MORELINES is: " + Character.toString(env.getMorelinesSymbol()));
			} else {
				String oldSymbol = env.getMorelinesSymbol().toString();
				env.setMorelinesSymbol(newSymbol);
				env.writeln("Symbol for MORELINES changed from " + oldSymbol + " to " + env.getMorelinesSymbol());
			}
			break;
		case "MULTILINES":
			if (getSymbol) {
				env.writeln("Symbol for MULTILINES is: " + Character.toString(env.getMultilineSymbol()));
			} else {
				String oldSymbol = env.getMultilineSymbol().toString();
				env.setMultiLinesSymbol(newSymbol);
				env.writeln("Symbol for MULTILINES changed from " + oldSymbol + " to " + env.getMultilineSymbol());
			}			
			break;
		}	
		
		
		return ShellStatus.CONTINUE;
	}

	@Override
	public String getCommandName() {
		return "symbol";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> description = new ArrayList<>();
		description.add("Allows user to change shell symbols, or to display currently used ones.");
		description.add("Command takes one or two arguments. The first one is the type of symbol we want to display or change, "
				+ "the second one is the new symbol");
		description.add("Allowed values for the first argument are: PROMPT, MORELINES, MULTILINE");
		description.add("If only one argument is given, currently used symbol is displayed, i.e symbol PROMPT");
		
		return description;
	}

}
