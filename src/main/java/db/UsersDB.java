package db;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import account.Roles;
import account.User;
import account.Users;

/**
 * Classe per il caricamento/ salvataggio degli utenti.
 */
public class UsersDB extends DBBase<Users>{
	private static String USERS_JSON = "users.json";
	static Path usersPath;
	
	public UsersDB() {
		super(Users.class, usersPath.toString());
	}
	
	/*
	 * PRECONDIZIONI:
	 * Database.getDataPath() != null
	 */
	/**
	 * Metodo richiamato durante l'inizializzazione del database.
	 * Ovviamente se non lo richiamo nella classe Database non succede nulla.
	 * In particolare salva la path per salvare/recuperare gli utenti.
	 */
	static void setPath() {
		usersPath = Paths.get(Database.getDataPath(), USERS_JSON);
	}
	
	/**
	 * Metodo (opzionalmente) richiamato da Database nel momento in cui non esiste
	 * il file .json degli utenti.
	 * @return true successo operazione
	 */
	static boolean onNewDB() {
		Users u = new Users();
		ArrayList<User> list = new ArrayList<User>();
		
		//creo un creatore di configuratori
		User configurator_creator_account = new User();
		configurator_creator_account.setUsername("configuratore");
		configurator_creator_account.setPassword("0a71e98bf6ee58d4e4c83e1fff5dfcb8");//configuratore
		configurator_creator_account.setRole(Roles.CONFIGURATOR_CREATOR);
		
		//creo un creatore di fruitori
		User init_fruitor_account = new User();
		init_fruitor_account.setUsername("fruitore");
		init_fruitor_account.setPassword("8f7568eddca12432357ad7303456ad43");//fruitore
		init_fruitor_account.setRole(Roles.FRUITOR_CREATOR);
		list.add(init_fruitor_account);

		list.add(configurator_creator_account);
		u.setUsers(list);
		return new UsersDB().save(u);
	}
}