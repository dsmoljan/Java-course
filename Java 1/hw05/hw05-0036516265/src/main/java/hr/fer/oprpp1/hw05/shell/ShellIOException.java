package hr.fer.oprpp1.hw05.shell;

public class ShellIOException extends RuntimeException{
	
	/**
	 * Models any kind of exception while running a shell command.
	 */
	private static final long serialVersionUID = 1L;

	public ShellIOException(String message) {
		super(message);
	}

}
