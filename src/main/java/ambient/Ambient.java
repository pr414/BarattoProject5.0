package ambient;

import account.Account;
import account.AuthMenu;
import db.Database;
import db.DatabaseException;
import menu.LogoutInterrupt;
import menu.MainMenu;
import menu.io.MenuIO;

public class Ambient {
	private MenuIO io;
	
	public Ambient() throws ConfigurationException{
		//Inizializza Ambient
		this.io = MenuIO.getMenu();
		if(!loadAmbientVariables())
			throw new ConfigurationException();
	}
	public void startAmbient() {
		//Esegue il menù principale + login se non loggato
		boolean res = true;
		while(res) {
			if(!Account.is_logged_in())
				if(!authentication())
					break;
			res = mainMenu();
		}
	}
	
	private boolean loadAmbientVariables() {
		try {
			if(ConfigurationManager.newInstance()) {
				addOnCloseSaveConfigurationChanges();
				Database.initialize();
				return true;
			}
		}catch(ConfigurationException | DatabaseException ex) {
			return false;
		}
		return false;
	}
	private void addOnCloseSaveConfigurationChanges() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void start() {
				ConfigurationManager.saveChanges();
			}
		});
	}
	
	private boolean authentication() {
		return new AuthMenu(io).start();
	}
	
	private boolean mainMenu(){
		try {
			return new MainMenu(io).start();
		} catch (LogoutInterrupt e) {
			return true;
		}
	}
	
	
	public static void main(String[] args) {
		try {
			new Ambient().startAmbient();
		}catch(ConfigurationException exc){
			System.out.println("Errore durante la creazione dell'ambiente");
		}
	}
}
