package menu.io;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Classe che gestisce gli Input con lo Scanner e gli output con PrintStream.
 * Le password saranno visibili. Preferibilmente usato in fase di testing o se non dovesse esistere
 * la System.console(). In Eclipse System.console() == null, quindi è necessario usare questa classe.
 */
class NoMaskIO extends MenuIO{
	private PrintStream out;
	private Scanner in;
	
	public NoMaskIO() {
		this.out = System.out;
		this.in = new Scanner(System.in);
	}
	/*
	 * PRECONDIZIONI:
	 * str != null
	 */
	@Override
	public void println(String str) {
		out.println(str);
	}
	@Override
	public void println() {
		out.println();
	}
	/*
	 * PRECONDIZIONI:
	 * str != null
	 */
	@Override
	public void print(String str) {
		out.print(str);
	}
	
	@Override
	public String onlyReadPassword(){
		return onlyReadLine();
	}
	@Override
	public String onlyReadLine() {
		return in.nextLine();
	}
	
	@Override
	public void close() throws IOException {
		if(this.in!=null)
			this.in.close();
		if(this.out!=null)
			this.out.close();
	}
}
