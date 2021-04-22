package hr.fer.oprpp1.hw05.shell.commands;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import hr.fer.oprpp1.hw05.shell.Enviroment;
import hr.fer.oprpp1.hw05.shell.ShellCommand;
import hr.fer.oprpp1.hw05.shell.ShellStatus;

public class CharsetsShellCommand implements ShellCommand{

	@Override
	public ShellStatus executeCommand(Enviroment env, String arguments) {
		SortedMap<String, Charset> s = Charset.availableCharsets();
		env.writeln("Following charsets are avalible on your Java platform:");
		for (String charsetName : s.keySet()) {
			env.writeln(charsetName);
		}
		return ShellStatus.CONTINUE;
	}

	@Override
	public String getCommandName() {
		return "charsets";
	}

	@Override
	public List<String> getCommandDescription() {
		List<String> description = new ArrayList<>();
		description.add("Lists the names of all supported charsets for your Java platform.");
		return description;
	}
	

}
