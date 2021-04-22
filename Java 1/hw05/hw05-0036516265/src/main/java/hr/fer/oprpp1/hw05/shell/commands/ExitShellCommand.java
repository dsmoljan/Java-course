package hr.fer.oprpp1.hw05.shell.commands;

import java.util.ArrayList;
import java.util.List;

import hr.fer.oprpp1.hw05.shell.Enviroment;
import hr.fer.oprpp1.hw05.shell.ShellCommand;
import hr.fer.oprpp1.hw05.shell.ShellStatus;

public class ExitShellCommand implements ShellCommand{

	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {
		return ShellStatus.TERMINATE;
	}

	@Override
	public String getCommandName() {
		return "exit";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> commandDescription = new ArrayList<>();
		commandDescription.add("Exits the shell.");
		return commandDescription;
	}

}
