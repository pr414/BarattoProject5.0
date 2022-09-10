package account;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import ambient.ConfigurationManager;
import db.Database;
import db.DatabaseException;
import db.UsersDB;
import utils.APNUtils;

public class AccountTest{
	
	
	@BeforeClass
	public static void do_before()
	{
		assertEquals("Errore nell'inizializzazione", initialize(), true);
	}
	
	
	/*
	 * Test signUp racchiude anche la casistica SUCCESS di login.
	 * Controlla se viene correttamente effettuata la registrazione.
	 */
	
	@Test
	public void test_signUp_and_loginPartial() {
		//Creo utente randomico non esistente
		User temp_user = UserTest.generateRandomUser();
		//Effettuo la registrazione di un nuovo utente.
		SignUpMessages msg = Account.signUp(temp_user);
		assertEquals("Errore di signUp. [Espected:SUCCESS]",
				msg, SignUpMessages.SUCCESS);
		if(msg == SignUpMessages.SUCCESS) {
			//Testo parte del login
			assertEquals("Il login non è corretto. [Espected:SUCCESS]",
					Account.login(temp_user),
					LoginMessages.SUCCESS);
			
			//Controllo funzionamento di logged_in
			assertEquals("L'utente doveva essere autenticato."
					+ "[Espected:true]",
					Account.is_logged_in(), true);
			Account.logout();
			assertEquals("L'utente NON doveva essere autenticato."
					+ "[Espected:false]",
					Account.is_logged_in(), false);
			
			//Testo l'inserimento di un altro account uguale al precedente
			assertEquals("Errore di signUp. [Espected:USER_ALR_EXISTS]",
					Account.signUp(temp_user),
					SignUpMessages.USER_ALR_EXISTS);
		}
	}
	
	/**
	 * Testa le altre casistiche di login.
	 */
	@Test
	public void test_login_oth() {
		//Test utente non trovato
		User loc_user = generateRandomNotExistingUser();
		assertEquals("L'utente non doveva esistere."
				+ "[Espected:USER_NOT_FOUND]",
				Account.login(loc_user),
				LoginMessages.USER_NOT_FOUND);
		
		//Test Account temporaneo
		String uname = "fruitore";
		String pw = "fruitore";
		loc_user = new User();
		loc_user.setUsername(uname);
		loc_user.setPassword(APNUtils.getMd5(pw));
		loc_user.setRole(Roles.FRUITOR_CREATOR);
		assertEquals("L'utente doveva essere temporaneo."
				+ "[Espected:CREATE_NEW_USER]",
				Account.login(loc_user),
				LoginMessages.CREATE_NEW_USER);
		//Test Account temporaneo - non autenticato
		assertEquals("L'utente NON doveva essere autenticato"
				+ "dopo aver effettuato l'accesso con account"
				+ "temporaneo. [Espected:false]",
				Account.is_logged_in(), false);
	}
	
	static User generateRandomNotExistingUser() {
		User user = null;
		while(true) {
			user = UserTest.generateRandomUser();
			if(!user_alr_exists(user)) {
				return user;
			}
		}
	}
	
	static boolean user_alr_exists(User user) {
		UsersDB dbc = new UsersDB();
		Users users;
		try {
			users = dbc.load();
		} catch (IOException e) {
			return false;
		}
		List<User> users_list = users.getUsers();
		for(int i = 0; i < users_list.size(); i++) {
			User temp = users_list.get(i);
			if(temp.getUsername().equals(user.getUsername())){
				return true;
			}
		}
		return false;
	}
	
	static boolean removeUserIfExists(User user) {
		UsersDB dbc = new UsersDB();
		Users users;
		try {
			users = dbc.load();
		} catch (IOException e) {
			return false;
		}
		List<User> users_list = users.getUsers();
		int index = -1;
		for(int i = 0; i < users_list.size(); i++) {
			User temp = users_list.get(i);
			if(temp.getUsername().equals(user.getUsername())){
				//esiste e lo rimuovo
				index = i;
				break;
			}
		}
		if(index != -1) {
			users.getUsers().remove(index);
			return dbc.save(users);
		}
		return true;
	}
	
	
	public static boolean initialize() {
		assertEquals("Errore nell'inizializzazione", initialize_all(), true);
		return true;
	}
	
	/**
	 * Inizializzo il necessario per la classe Account
	 */
	private static boolean initialize_all() {
		if(ConfigurationManager.newInstance()) {
			try {
				Database.initialize();
				return true;
			} catch (DatabaseException e) {}
		}
		return false;
	}
}
