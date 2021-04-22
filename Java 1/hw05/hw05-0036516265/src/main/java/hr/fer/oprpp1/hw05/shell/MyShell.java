package hr.fer.oprpp1.hw05.shell;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import hr.fer.oprpp1.hw05.shell.commands.CatShellCommand;
import hr.fer.oprpp1.hw05.shell.commands.ChangeSymbolShellCommand;
import hr.fer.oprpp1.hw05.shell.commands.CharsetsShellCommand;
import hr.fer.oprpp1.hw05.shell.commands.CopyShellCommand;
import hr.fer.oprpp1.hw05.shell.commands.ExitShellCommand;
import hr.fer.oprpp1.hw05.shell.commands.HelpShellCommand;
import hr.fer.oprpp1.hw05.shell.commands.HexdumpShellCommand;
import hr.fer.oprpp1.hw05.shell.commands.LsShellCommand;
import hr.fer.oprpp1.hw05.shell.commands.MkdirShellCommand;
import hr.fer.oprpp1.hw05.shell.commands.TreeShellCommand;

/**
 * Main class for MyShell
 * @author Dorian
 *
 */
public class MyShell {
	
	//e ovdje bi ja još dodao catch bilo koje shellioexception, jer se program mora zaustaviti ako dođe do nje
	//tj stavio bi da ako OVDJE dođe do pogreške u čitanju/pisanju sa shella, samo se izađe
	//a da se catcha takva greška iz pozvanih naredbi i onda izađe
	
	public static void main(String[] args) {
		System.out.println("Welcome to MyShell v 1.0");
		
		SortedMap<String, ShellCommand> commands = new TreeMap<String, ShellCommand>();
		commands.put("exit", new ExitShellCommand());
		commands.put("charsets", new CharsetsShellCommand());
		commands.put("cat", new CatShellCommand());
		commands.put("ls", new LsShellCommand());
		commands.put("tree", new TreeShellCommand());
		commands.put("copy", new CopyShellCommand());
		commands.put("mkdir", new MkdirShellCommand());
		commands.put("hexdump", new HexdumpShellCommand());
		commands.put("help", new HelpShellCommand());
		commands.put("symbol", new ChangeSymbolShellCommand());
		
		
		Enviroment env = new EnviromentImpl('|', '>', '\\', commands);
		ShellStatus status = ShellStatus.CONTINUE;
		Scanner sc = new Scanner(System.in);
		
		do {
			System.out.print(env.getPromptSymbol() + " ");
			String line;
			
			try {
				line = sc.nextLine();
			} catch(NoSuchElementException | IllegalStateException e) {
				env.writeln("Error while reading from shell!");
				break;
			};
			
//			System.out.println("line is " + line);
//			if (line.equals("exit")) {
//				status = ShellStatus.TERMINATE;
//				continue;
//			}
			
			//if the user just keeps pressing enter
			if (line.length() == 0){
				continue;
			}
			
			String commandArguments = ""; //list of command arguments to be sent to the command
			String commandName = "";
			
			//imaj na umu da ako u terminalu prelaziš u novi red, moraš ispisati MULTILINESYMBOL na početku, a ne >
			//shell zna kad treba čitati dalje naredbe kad naiđe na MORELINESSYMBOL
			
			//shell uvijek čita samo jednu narebdu; nije moguće zadati dvije naredbe odjednom
			//samo je moguće da se ta naredba svojim argumentima rasprostire kroz više redaka koristeći MORELINESSYMBOL
			
			line = line.trim();
			
			
			//we find the commandName
			int i = 0;
			while (i < line.length() && line.charAt(i) != ' ') {
				commandName += line.charAt(i);
				i++;
			}
//			System.out.println("Command name is " + commandName);
			
			//we remove the command name from the rest of the arguments
			line = line.substring(i, line.length());
			
			List<String> lines = new ArrayList<>();
			
			boolean endOfCommandFlag = false;
			
			
			while (endOfCommandFlag != true && line.length() != 0) {
				line = line.trim();
				
				if (line.charAt(line.length() - 1) == env.getMorelinesSymbol()) {
					line = line.substring(0, line.length() - 1); // mičemo morelines symbol
				} else {
					endOfCommandFlag = true;
				}
				
//				if (line.length() > 0 && line.charAt(0) == env.getMultilineSymbol()) {
//					line = line.substring(1, line.length());
//				}
				
				commandArguments += line.trim() + " ";
				if (endOfCommandFlag != true) {
					System.out.print(env.getMultilineSymbol() + " ");
					try {
						line = sc.nextLine();
					} catch(NoSuchElementException | IllegalStateException e) {
						env.writeln("Error while reading from shell!");
						break;
					}
				}

			}
			
			//System.out.println("DEBUG Arguments are : " + commandArguments.trim());
			ShellCommand command = env.commands().get(commandName);
			if (command == null) {
				env.writeln("Unrecognized command. Type 'help' for a list of avalible commands");
				continue;
			}
			try {
				status = command.executeCommand(env, commandArguments.trim());
			} catch (ShellIOException e) {
				env.writeln("Error while reading/writing to shell. Exiting shell");
				break;
			}
			
			
		} while (status == ShellStatus.CONTINUE);

		
	}

}
