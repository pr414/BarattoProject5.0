package account;

import java.io.IOException;

import db.UsersDB;

/**
 * Classe "globale" per la gestione degli account.
 */
public class Account {
	/**
	 * Sessione corrente: nulla se nessun utente ha effettuato l'accesso.
	 */
	private static Session current = null;
	
	public static String getCurrentSessionId() {
		if(current != null) //occhio che potrebbe essere nulla se richiamata scorrettamente.
			return current.getId();
		else
			return null;
	}
	
	public static String getUserName() {
		if(current != null)
			return current.getUser().getUsername();
		else
			return null;
	}
	
	/**
	 * Se la sessione corrente è != null allora un utente è "loggato".
	 * @return true se current user is logged in.
	 */
	public static boolean is_logged_in() {
		return current != null;
	}
	
	public static void logout() {
		current = null;
	}
	
	public static Roles getAccountRole() {
		if(current!=null)
			return current.getUser().getRole();
		return null;
	}
	
	/*
	 * PRECONDIZIONI:
	 * user != null, user.password != null, user.username != null
	 * 
	 * POSTCONDIZIONI:
	 * current != null, current.user = user
	 * 
	 * (Se l'utente ha effettuato il login con successo
	 * postcondizione: user.role != null)
	 */
	/**
	 * Metodo per effettuare il login
	 * @param user
	 * @return messaggio di login
	 */
	static LoginMessages login(User user) {
		try {
			Users users = new UsersDB().load();
			for(User temp : users.getUsers()) {
				if(temp.credentialsMatch(user)) {
					user.setRole(temp.getRole());
					if(user.must_new_credentials())
						return LoginMessages.CREATE_NEW_USER;
					current = new Session(user);
					return LoginMessages.SUCCESS;
				}
			}
		}
		catch (IOException e) {
			return LoginMessages.IO_ERROR;
		}
		return LoginMessages.USER_NOT_FOUND;
	}
	
	/*
	 * PRECONDIZIONI:
	 * user != null, user.password != null, user.username != null, user.role != null
	 * 
	 * POSTCONDIZIONI:
	 * current != null, current.user = user
	 */
	/**
	 * Metodo per effettuare la registrazione di un nuovo utente.
	 * @param user
	 * @return messaggio di sign up
	 */
	static SignUpMessages signUp(User user) {
		try {
			UsersDB db = new UsersDB();
			Users users = db.load();
			for(User temp : users.getUsers())
				if(temp.getUsername().equals(user.getUsername()))
					return SignUpMessages.USER_ALR_EXISTS;
			users.getUsers().add(user);
			current = new Session(user);
			db.save(users);
		} catch (IOException ex) {
			return SignUpMessages.IO_ERROR;
		}
		return SignUpMessages.SUCCESS;
	}
}
