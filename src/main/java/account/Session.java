package account;

import java.util.Date;
import java.util.UUID;

/**
 * Classe per la gestione della sessione utente.
 * In particolare indica quando l'utente ha effettuato l'accesso
 * e quale utente ha effettuato l'accesso.
 * Per implementazioni future si potranno salvare anche le sessioni.
 * Ora sarebbe abbastanza inutile.
 */
class Session{
	private String id;
	private User user;
	private Date startAt;
	
	/*
	 * PRECONDIZIONI:
	 * user != null
	 */
	/**
	 * Inizializza una sessione associandoci un utente.
	 * @param user
	 */
	public Session(User user) {
		this.setId(UUID.randomUUID().toString());
		this.setUser(user);
		this.setStartAt(new Date());
	}
	
	//getters & setters
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getStartAt() {
		return startAt;
	}
	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
