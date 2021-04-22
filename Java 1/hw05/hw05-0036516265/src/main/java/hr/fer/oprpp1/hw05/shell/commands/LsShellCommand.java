package hr.fer.oprpp1.hw05.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hr.fer.oprpp1.hw05.shell.Enviroment;
import hr.fer.oprpp1.hw05.shell.ShellCommand;
import hr.fer.oprpp1.hw05.shell.ShellStatus;

public class LsShellCommand implements ShellCommand{

	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {
		//tu recimo staviš Files.walkFileTree(path,visitor) gdje je visitor instanca tvoje implementacije FileVisitora
		//najbolje napravi svoju klasu koja extenda SimpleFileVisitor i nadjačaj potrebne metode
		
		String path = "";
		
		//ovaj dio koda ti je zajednički i za tree i za ls i za mkdir, tako da ga izdvoji u zasebnu klasu
		
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
				env.writeln("Invalid number of arguments!Command ls supports only zero or one arguments! For more information type 'help ls'");
				return ShellStatus.CONTINUE;
			}
		}
		
		List<Path> filesInDir = null;
		
		//check if the URI user has given is maybe a file and not a directory
		
		if (Files.isDirectory(Paths.get(path)) == false) {
			env.writeln("Error - given URI is not a directory!");
			return ShellStatus.CONTINUE;
		}
		
		try(Stream<Path> walk = Files.walk(Paths.get(path), 1)){
			filesInDir = walk.map(x -> x.toAbsolutePath()).collect(Collectors.toList());
			
		} catch (IOException e) {
			env.writeln("Error while attempting to read directory!");
			return ShellStatus.CONTINUE;
		}
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		BasicFileAttributeView faView = null;
		BasicFileAttributes attributes = null;
		FileTime fileTime = null;
		String formattedDateTime = null;
		
		for (Path p : filesInDir) {
			try {
				
				String result = "";
				
				String isDirectory = Files.isDirectory(p) ? "d" : "-";
				String isReadable = Files.isReadable(p) ? "r" : "-";
				String isWritable = Files.isWritable(p) ? "w" : "-";
				String isExecutable = Files.isExecutable(p) ? "x" : "-";
				
				result += isDirectory + isReadable + isWritable + isExecutable + " ";
				
				
				String size = Long.toString(Files.size(p));
				
				for (int i = 0; i < 10 - size.length(); i++) {
					result += " ";
				}
				
				result += size + " ";
				
				faView = Files.getFileAttributeView(p, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
				attributes = faView.readAttributes();
				fileTime = attributes.creationTime();
				formattedDateTime = sdf.format(new Date(fileTime.toMillis()));
				
				result += formattedDateTime + " ";
				
				result += p.getFileName().toString();
				
				env.writeln(result);
				
			} catch (Exception e) {
				env.writeln("Error while reading file " + p.toString());
				return ShellStatus.CONTINUE;
			}
		}
		
		
		return ShellStatus.CONTINUE;
	}

	@Override
	public String getCommandName() {
		return "ls";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> description = new ArrayList<>();
		description.add("Takes zero or one argument.");
		description.add("If called wihtout arguments, lists all the directories and files in the current directory");
		description.add("If given one argument, a path to a directory, lists all directories and files in that directory");
		return description;
	}

}
