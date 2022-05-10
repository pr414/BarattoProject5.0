package account;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * Classe Modello per gestire la lista degli utenti.
 */
public class Users{
	@Expose
	private List<User> users;
	
	public List<User> getUsers(){
		return this.users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
}