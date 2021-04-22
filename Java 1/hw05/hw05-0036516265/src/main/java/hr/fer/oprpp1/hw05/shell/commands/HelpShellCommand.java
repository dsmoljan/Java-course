package hr.fer.oprpp1.hw05.shell.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import hr.fer.oprpp1.hw05.shell.Enviroment;
import hr.fer.oprpp1.hw05.shell.ShellCommand;
import hr.fer.oprpp1.hw05.shell.ShellIOException;
import hr.fer.oprpp1.hw05.shell.ShellStatus;

public class HelpShellCommand implements ShellCommand{
	

	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {
		Map<String, ShellCommand> commandMap = env.commands();

		if (arguments.length() == 0) {
						
			env.writeln("List of avalible commands: ");
			
			for (String commandName : commandMap.keySet()) {
				env.writeln(commandName);
			}
			
			env.writeln("For a description of a specific command, write 'help commandName'");
;
		} else {
			if (commandMap.get(arguments) != null) {
				ShellCommand cmd = commandMap.get(arguments);
				
				try {
					for (String s : cmd.getCommandDescription()) {
						env.write(s); //he he he
						env.readLine();
					}
				} catch (NoSuchElementException | IllegalStateException e) {
					throw new ShellIOException("Error while reading from shell!");
				}

			} else {
				env.writeln("Command " + arguments + " doesn't exsist!" );
			}
		}
		return ShellStatus.CONTINUE;
	}

	@Override
	public String getCommandName() {
		return "help";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> descriptionList = new ArrayList<>();
		descriptionList.add("If started with no arguments, lists the names of all supported commands.");
		descriptionList.add("If started with a single argument, prints the name and the description of the selected command");
		descriptionList.add("or the appropriate error message if no such command exsists.");
		
		return descriptionList;
	}

}
