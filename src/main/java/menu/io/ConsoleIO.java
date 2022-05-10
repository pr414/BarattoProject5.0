package menu.io;

import java.io.Console;
import java.io.IOException;

/**
 * Classe che gestisce gli I/O con System.console(). Le password saranno invisibili.
 */
class ConsoleIO extends MenuIO{
	private Console io;
	
	private ConsoleIO(Console console) {
		this.io = console;
	}

	public static ConsoleIO tryGetInstance() {
		Console console = System.console();
		return (console==null)?null:new ConsoleIO(console);
	}
	
	@Override
	public void close() throws IOException {}
	@Override
	protected String onlyReadPassword() {
		return new String(this.io.readPassword());
	}
	@Override
	public String onlyReadLine() {
		return io.readLine();
	}
	/*
	 * PRECONDIZIONI:
	 * str != null
	 */
	@Override
	public void println(String str) {
		this.io.printf("%s\n", str);
	}
	@Override
	public void println() {
		this.io.printf("\n");
	}
	/*
	 * PRECONDIZIONI:
	 * str != null
	 */
	@Override
	public void print(String str) {
		this.io.printf("%s", str);
	}

}
