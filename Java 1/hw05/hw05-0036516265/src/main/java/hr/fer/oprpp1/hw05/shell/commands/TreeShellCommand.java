package hr.fer.oprpp1.hw05.shell.commands;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import hr.fer.oprpp1.hw05.shell.Enviroment;
import hr.fer.oprpp1.hw05.shell.ShellCommand;
import hr.fer.oprpp1.hw05.shell.ShellStatus;

public class TreeShellCommand implements ShellCommand{

	
	//ovo napravi preko file visitora
	//malo treba izlaz popraviti (recimo ispisuje i početnu točku ., ali čini se oke za sad)
	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {
		String path = "";
		
		if (arguments.length() == 0) {
			path = "./";
		}
		else if (arguments.charAt(0) == '"' && arguments.charAt(arguments.length() - 1) == '"') {
			path = arguments.substring(1,arguments.length() - 1); 
		}
		else {
			String[] argumentsList = arguments.split(" ");
			
			if (argumentsList.length == 1) {
				path = argumentsList[0];
			}else {
				env.writeln("Invalid number of arguments! Command tree supports only zero or one arguments! For more information type 'help tree'");
				return ShellStatus.CONTINUE;
			}
		}
		
		if (Files.isDirectory(Paths.get(path)) == false) {
			env.writeln("Error - given URI is not a directory!");
			return ShellStatus.CONTINUE;
		}
		
		try {
			Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>(){
				int level = -1;
				
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					String indent = "";
					for (int i = 0; i < level; i++) {
						indent += "  ";
					}
					if (level != 0) {
						env.writeln(indent + dir.getFileName());
					}
					level += 1;
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String indent = "";
					for (int i = 0; i < level; i++) {
						indent += "  ";
					}
					env.writeln(indent + file.getFileName().toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					// TODO Auto-generated method stub
					return super.visitFileFailed(file, exc);
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					level -= 1;
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			env.writeln("Error while attempting to read file " + path);
		}

		return ShellStatus.CONTINUE;
	}

	@Override
	public String getCommandName() {
		return "tree";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> description = new ArrayList<>();
		description.add("Recursivly lists contents of a directory and all of its children");
		description.add("Takes zero or one argument.");
		description.add("If no argument is given, lists the content of the current directory.");
		description.add("If an argument, path to a directory is given, lists the contents of that directory");
		
		return description;
	}

}
