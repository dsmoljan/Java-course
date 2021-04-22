package hr.fer.oprpp1.hw05.shell.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hr.fer.oprpp1.hw05.shell.Enviroment;
import hr.fer.oprpp1.hw05.shell.ShellCommand;
import hr.fer.oprpp1.hw05.shell.ShellIOException;
import hr.fer.oprpp1.hw05.shell.ShellStatus;

/**
 * Creates a new directory in the current one.
 * @author Dorian
 *
 */
public class MkdirShellCommand implements ShellCommand{
	
	//dodaj još provjeru da taj direktorij postoji

	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {
		
		String[] argumentsList = arguments.split(" ");
		
		if (argumentsList.length != 1) {
			env.writeln("Error - wrong number of arguments! mkdir takes only one argument. For more information, write 'help mkdir'");
			return ShellStatus.CONTINUE;
		}
		
		Path dir = Paths.get("./" + argumentsList[0]);
		
		String overwrite = "";
		
		if (Files.exists(dir)){
			env.writeln("Specified directory currenlty exsists. Would you like to overwrite it?");
			env.write("yes/no:");
			try {
				overwrite = env.readLine();
			}catch (Exception e) {
				throw new ShellIOException("Error while reading from shell!");
			}
		}
		
		if (overwrite.toLowerCase().equals("no")) {
			return ShellStatus.CONTINUE;
		}
		
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			env.writeln("Error while creating directory!");
			return ShellStatus.CONTINUE;
		}
		
		env.writeln("Directory created");
		
		return ShellStatus.CONTINUE;
	}

	@Override
	public String getCommandName() {
		return "mkdir";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> description = new ArrayList<>();
		description.add("Creates a new directory in the current location. Takes only one argument, directory name.");
		return description;
	}

}
