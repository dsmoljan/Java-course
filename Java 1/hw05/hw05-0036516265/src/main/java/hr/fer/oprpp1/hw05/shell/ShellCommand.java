package hr.fer.oprpp1.hw05.shell;

import java.util.List;

/**
 * Interface modeling a shell command
 * @author Dorian
 *
 */
public interface ShellCommand {
	
	/**
	 * Executes the command
	 * @param env enviroment in which the command is to be executed
	 * @param arguments string which represents everything that user entered AFTER the command name
	 * @return
	 */
	ShellStatus executeCommand(Enviroment env, String arguments); //arguments is a string which represents everything that user entered AFTER the command name
	
	/**
	 * Returns the name of the command.
	 * @return
	 */
	String getCommandName();
	
	/**
	 * Returns the command description. Since the description can span multiple lines, each line is an entry in this list.
	 * The list is read-only.
	 * @return
	 */
	List<String> getCommandDescription(); //this list has to be read only
	

}
