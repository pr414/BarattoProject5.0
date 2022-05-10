package menu.io;

import java.io.Closeable;

/**
 * Classe per la gestione dell'input/output
 * di un menù a linea di comando.
 */
public abstract class MenuIO implements Closeable{
	//---------POSSIBLE ACTIONS-------
	public abstract void println(String str);
	public abstract void println();
	public abstract void print(String str);
	
	public abstract String onlyReadLine();//legge solo la linea
	protected abstract String onlyReadPassword();//legge solo la password
	
	
	/**
	 * Legge una stringa e ritorna null se vuota.
	 */
	public String readString() {
		String line = onlyReadLine();
		return (line=="")?null:line;
	}
	/**
	 * Legge una password e ritorna null se vuota.
	 */
	public String readPassword() {
		String line = onlyReadPassword();
		return (line=="")?null:line;
	}
	
	/**
	 * Metodo che ritorna un'istanza di MenuIO in base alla disponibilità
	 * di ConsoleIO.
	 * @return
	 */
	public static MenuIO getMenu() {
		MenuIO io = ConsoleIO.tryGetInstance();
		if(io==null)//Siamo in Eclipse quindi System.Console non fuziona
			io = new NoMaskIO();
		return io;
	}
}
