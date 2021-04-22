package hr.fer.oprpp1.hw05.shell.commands;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import hr.fer.oprpp1.hw05.shell.Enviroment;
import hr.fer.oprpp1.hw05.shell.ShellCommand;
import hr.fer.oprpp1.hw05.shell.ShellIOException;
import hr.fer.oprpp1.hw05.shell.ShellStatus;

/**
 * Class modeling a cat command.
 * Command takes one or two arguments. The first argument is the path to some file and it is mandatory.
 * The second argument is charset name that should be used to interpret chars from bytes.
 * If not provided, a default platform charset is used.
 * @author Dorian
 *
 */
public class CatShellCommand implements ShellCommand{
	
	//ovdje isto dodaj podršku za navodnike

	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {		
		Charset c;
		Path path;
		
		//argument parsing
		
		String[] argumentsList = arguments.split(" ");
		if (argumentsList.length == 0) {
			throw new ShellIOException("Invalid number of arguments for this command. Minimum is 1 (path)");
		}
		
		String pathString = "";
		String charset = "";
		
		boolean passedFirstArg = false;
		boolean parsingQuotes = false;

		for (int i = 0; i < arguments.length(); i++) {
			char currentChar = arguments.charAt(i);
			
			if (currentChar == '"') {
				if (parsingQuotes == true) {
					parsingQuotes = false;
					continue;
				}else {
					parsingQuotes = true;
					continue;
				}
			}
			
			if (currentChar == ' ' && parsingQuotes == false) {
				passedFirstArg = true;
				continue;
			}
			
			if (passedFirstArg == false) {
				pathString += currentChar;
			}else {
				charset += currentChar;
			}
		}
		
//		path = Paths.get(argumentsList[0]);
//		
//		if (argumentsList.length == 2) {
//			try {
//				c = Charset.forName(argumentsList[1]);
//			} catch(IllegalArgumentException e) {
//				throw new ShellIOException("Invalid charset!");
//			}
//		}else {
//			c = Charset.defaultCharset();
//		}
//		
//		//kao i u zadatku 1, čitati ćemo korak po korak (4096 bajtova) jer se može desiti da imamo veliku datoteku
//		
		path = Paths.get(pathString);
		
		//set charset
		if (charset != "") {
			try {
				c = Charset.forName(charset);
			} catch(IllegalArgumentException e) {
				env.writeln("Invalid charset!");
				return ShellStatus.CONTINUE;
			}	
		}else {
			c = Charset.defaultCharset();
		}
		
		//check if the given path is actually a path to a directory
		
		if (Files.isDirectory(path)){
			env.writeln("Cannot cat directory!");
			return ShellStatus.CONTINUE;
		}
		
		
		//read and print the file
		byte[] tmpBuff = new byte[4096];
		String tmpString = "";
		
		try(InputStream is = Files.newInputStream(path)) {
			
			while(true) {
				int r = is.read(tmpBuff); 
				
				if (r >= 1) {
					tmpString = new String(tmpBuff, c);
					env.write(tmpString);
				}

				if (r < 1) {
					env.writeln("");
					break;
				}
			}
		} catch(IOException e) {
			env.writeln("Error while trying to read from file!");
		}
				
		return ShellStatus.CONTINUE;
	}

	@Override
	public String getCommandName() {
		return "cat";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> description = new ArrayList<>();
		description.add("Command takes one or two arguments");
		description.add("The first argument is the path to some file and it is mandatory");
		description.add("The second argument is charset name that should be used to interpret chars from bytes");
		description.add("If not provided, a default platform charset is used.");
		return description;
	}

}
