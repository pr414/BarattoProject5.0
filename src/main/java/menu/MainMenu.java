package menu;

import java.io.IOException;

import account.Account;
import account.Roles;
import db.DatabaseException;
import menu.io.MenuIO;

/**
 * Classe MainMenu è un Menù base che ritorna un Booleano
 * con il metodo "start".
 */
public class MainMenu extends MenuBase<Boolean>{
	public MainMenu(MenuIO io) {
		super(io);
	}
	
	//ritorna falso se vuoi stoppare il loop.
	@Override
	public Boolean start() throws LogoutInterrupt{
		io.println("Menù avviato correttamente.");
		if(Account.getAccountRole().equals(Roles.CONFIGURATOR)) {
			try {
				return new ConfiguratorMenu(io).start();
			} catch (DatabaseException | IOException e) {
				io.println("Errore durante il caricamento delle gerarchie.");
				return false;
			}
		}
		else if(Account.getAccountRole().equals(Roles.FRUITOR)) {
			try {
				return new FruitorMenu(io).start();
			} catch (IOException e) {
				io.println("Errore durante il caricamento delle gerarchie.");
				return false;
			}
		}
		else {
			io.println("Errore successivo al login. Per favore riprovare ad effettuare il login.");
			logout();
		}
		return false;
	}
}
