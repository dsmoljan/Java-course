package hr.fer.oprpp1.hw05.shell.commands;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import hr.fer.oprpp1.hw05.shell.Enviroment;
import hr.fer.oprpp1.hw05.shell.ShellCommand;
import hr.fer.oprpp1.hw05.shell.ShellStatus;
import hr.fer.oprpp1.hw05.shell.ShellUtil;

public class HexdumpShellCommand implements ShellCommand{
	
	//sve je ok, samo zadnje byteove ne ispisuje

	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {
		
		String pathString = "";
		Path path = null;
		
		//parse arguments, which may contain ""
		if (arguments.charAt(0) == '"' && arguments.charAt(arguments.length() - 1) == '"') {
			pathString = arguments.substring(1,arguments.length() - 1); 
		}
		else {
			String[] argumentsList = arguments.split(" ");
			
			if (argumentsList.length == 1) {
				pathString = argumentsList[0];
			}else {
				env.writeln("Invalid number of arguments! Command hexdump supports only zero or one arguments! For more information type 'help hexdump'");
				return ShellStatus.CONTINUE;
			}
		}
		
		
		try {
			path = Paths.get(pathString);
		} catch (InvalidPathException e) {
			env.writeln("File cannot be opened!");
			return ShellStatus.CONTINUE;
		}
		
		//check if user gave a directory
		if (Files.isDirectory(path)) {
			env.writeln("Error - cannot hexdump a directory. Please type 'help hexdump' for more information.");
			return ShellStatus.CONTINUE;
		}
		//otvori datoteku kao stream, postoji metoda koja čita byte po byte
		
		try(InputStream is = Files.newInputStream(path)) {
			byte[] tmpArray = new byte[16];
			byte currentByte;
			
			String bytesAsString = "";
			
			int bytesToRead = 16;
			int bytesActuallyRead = 0;
			
			int counterDecimal = 0;
			int counterHex = 0;
			
			String result = "";
			
			while(true) {
				int r = is.read(tmpArray);
				
				if (r > -1) {
					
				}
				bytesActuallyRead += r;
				
				//this is just in case less than 16 bytes have been read
				while (bytesActuallyRead != 16 && r > 1) {
					r = is.read(tmpArray, bytesActuallyRead, 16 - bytesActuallyRead);
					if (r > -1) {
						bytesActuallyRead += r;
					}
				}
	
				
				if (r < 1 && bytesActuallyRead <= 0) {
					break;
				} else { //this way, we are sure there are either 16 bytes in tmpArray, or we have reached the end of the file and are just writing the remaining bytes
					bytesToRead = 16;
					
					for (int i = 0; i < 8 - Integer.toString(counterHex).length(); i++) {
						result += "0";
					}
					
					result += Integer.toString(counterHex) + ": ";
					int i = 0;
					for (i = 0; i < bytesActuallyRead; i++) {
						
				
						currentByte = tmpArray[i];
						
						if (currentByte < 32 | currentByte > 127) {
							bytesAsString += ".";
						} else {
							bytesAsString += (char) currentByte; //kao character jel
						}
						
						result += ShellUtil.bytetohex(currentByte).toUpperCase();
						if (i == 7) {
							result += "|";
						}else {
							result += " ";
						}
					}
					
					for (int j = i; j < 16; j++) {
						if (j == 7) {
							result += "  |";
						}else {
							result += "   ";
						}
					}
					
					result += "| ";
					
					result += bytesAsString;
					
					result += "\n";
					
					counterHex += 10;
					bytesAsString = "";
					
					bytesActuallyRead = 0;
					
				}
			}
			
			env.write(result);
			
		} catch (IOException e) {
			env.writeln("Error while reading file!");
			return ShellStatus.CONTINUE;
		}
		
		
		return ShellStatus.CONTINUE;
	}

	@Override
	public String getCommandName() {
		return "hexdump";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> description = new ArrayList<>();
		description.add("Command takes in a single argument - file name, and produces hex-output.");
		return description;
	}

}
