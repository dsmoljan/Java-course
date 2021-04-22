package hr.fer.oprpp1.hw05.shell.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
 * Command used to copy a file.
 * @author Dorian
 *
 */
public class CopyShellCommand implements ShellCommand{
	
	// e dodaj i ovdje ono za navodnike!

	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {
		Path originalFilePath;
		Path newfilePath;
		
		String path1 = "";
		String path2 = "";
		
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
				path1 += currentChar;
			}else {
				path2 += currentChar;
			}
		}
		
		try {
			originalFilePath = Paths.get(path1);
			newfilePath = Paths.get(path2);
		} catch (InvalidPathException e) {
			env.writeln("Cannot instanciate paths - check your paths!");
			return ShellStatus.CONTINUE;
		}

		
		//check if the user has passed all neccessary arguments
		
		if (path1.length() == 0 || path2.length() == 0) {
			env.writeln("Wrong number of arguments! Type 'help copy' for more information.");
			return ShellStatus.CONTINUE;
		}
		
		if (Files.isDirectory(originalFilePath)) {
			env.writeln("Cannot copy a directory!");
			return ShellStatus.CONTINUE;
		}
				
//		String[] argumentsList = arguments.split(" ");
//		
//		if (argumentsList.length == 2) {
//			originalFilePath = Paths.get(argumentsList[0]);
//			newfilePath = Paths.get(argumentsList[1]);
//		} else {
//			env.writeln("Error - command copy expects 2 arguments! Type 'help copy' for more information.");
//			return ShellStatus.CONTINUE;
//		}
		
		//korisnik želi staviti datoteku u neki direktorij - moramo onda izgraditi novi path u kojem je nova datoteka u tom direktoriju
		if (Files.isDirectory(newfilePath)) {
			newfilePath = Paths.get(newfilePath.toString() + "/" + originalFilePath.toFile().getName());
		}
		
		String overwrite = "";
		//provjeravamo postoji li već neka datoteka s istim imenom, ako postoji pitamo korisnika želi li ju zamijeniti
		if (Files.exists(newfilePath)) {
			try {
				env.writeln("There already exists such a file. Would you like to overwrite it?");
				env.write("yes/no:");
				overwrite = env.readLine();
			}catch (Exception e) {
				throw new ShellIOException("Error while reading from shell!");
			}
			
			if (overwrite.toLowerCase().equals("no")) {
				return ShellStatus.CONTINUE;
			}
		}
		
		try {
			copy(originalFilePath, newfilePath);		
		} catch(IOException e) {
			env.writeln("Error while copying file!");
			return ShellStatus.CONTINUE;
		}
		
		env.writeln("File succesfully copied");
		
		return ShellStatus.CONTINUE;
	}

	@Override
	public String getCommandName() {
		return "copy";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> description = new ArrayList<>();
		description.add("Copies a file to a different location. Takes in two arguments: source file name and destination file name");
		description.add("If destination file already exsists, command asks the usaer if they want to overwrite it.");
		description.add("Works only with files, not directories.");
		description.add("If the second argument is a directory, command assumes that the user wants to copy the original file using the original file name");
		return description;
	}
	
	
	/**
	 * Private method used to copy contents of original file into a new file.
	 * @param origianl
	 * @throws IOException 
	 */
	private static void copy(Path originalFilePath, Path newFilePath) throws IOException {
		
		try(InputStream is = Files.newInputStream(originalFilePath);
				OutputStream os = Files.newOutputStream(newFilePath,  java.nio.file.StandardOpenOption.CREATE_NEW)) {
			
			byte[] tmpBuff = new byte[4096];
			
			while(true) {
				int r = is.read(tmpBuff);
				if (r < 1) {
					break;
				} else {
					os.write(tmpBuff);
				}
			}
			
		}catch (IOException e) {
			throw new IOException(e.getMessage());
		}
		
	}

}
