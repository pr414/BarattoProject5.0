package account;

import com.google.gson.annotations.Expose;

/**
 * Classe Modello per gestire un singolo utente.
 */
public class User{
	@Expose
	private String username;
	 @Expose
	private String password;
	 @Expose
	private Roles role;
	
	 /*
	  * PRECONDIZIONI:
	  * another != null, another.username != null, another.password != null
	  * 				    this.username != null,    this.password != null
	  */
	 /**
	  * Metodo per confrontare le credenziali
	  * @param another
	  * @return true se le credenziali sono uguali.
	  */
	public boolean credentialsMatch(User another) {
		return another.getPassword().equals(this.getPassword())
				&& another.getUsername().equals(this.getUsername());
	}
	
	//getters & setters
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Roles getRole() {
		return role;
	}
	public int getRoleId() {
		return role.getId();
	}
	public void setRole(Roles role) {
		this.role = role;
	}
	/**
	 * Setta il ruolo per mezzo di un identificatore del ruolo.
	 * Per esempio setRole(1) setta come ruolo CONFIGURATOR
	 * @param role_id
	 * @return true se ha trovato il ruolo e l'ha settato correttamente.
	 */
	public boolean setRole(int role_id) {
		Roles role = Roles.fromId(role_id);
		if(role==null)
			return false;
		setRole(role);
		return true;
	}
	
	/*
	 * PRECONDIZIONI:
	 * this.role != null
	 */
	/**
	 * @return true se il ruolo corrente è volto alla creazione di nuovi utenti.
	 */
	public boolean must_new_credentials() {
		return this.getRole().must_new_credentials();
	}
}
