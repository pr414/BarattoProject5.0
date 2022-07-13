package account;

import menu.MenuBase;
import menu.io.MenuIO;
import utils.APNUtils;

/**
 * Menù per l'autenticazione.
 */
public class AuthMenu extends MenuBase<Boolean>{
	private User user;
	private Roles parent_role;
	
	public AuthMenu(MenuIO io) {
		super(io);
	}
	
	@Override
	public Boolean start(){
		return elaborateLogin();
	}

	/**
	 * Elabora il menù della registrazione di un nuovo utente.
	 * @return
	 */
	private boolean elaborateSignUp() {
		return elaborate(false);//false sta per login? no -> false
	}
	/**
	 * Elabora il menù di login
	 * @return
	 */
	private boolean elaborateLogin() {
		return elaborate(true);//false sta per login? sì -> true
	}
	
	/*
	 * PRECONDIZIONI:
	 * this.user != null, user.password != null, user.username != null
	 */
	/**
	 * Prova ad effettuare la registrazione.
	 * @return true se registrato
	 */
	private boolean tryDoSignUp() {
		SignUpMessages result = Account.signUp(this.user);
		switch(result) {
		case SUCCESS:
			io.println("Registrazione avvenuta con successo.");
			io.println("Sessione avviata con successo (SID:"+Account.getCurrentSessionId()+")");
			return true;
		case USER_ALR_EXISTS:
			io.println("L'utente da lei inserito esiste già.");
			return false;
		default:
			io.println("Errore di I/O.");
			return false;
		}
	}
	
	/*
	 * PRECONDIZIONI:
	 * this.user != null, user.password != null, user.username != null
	 */
	/**
	 * Prova ad effettuare il login.
	 * @return true se credenziali corrette.
	 */
	private boolean tryDoLogin() {
		LoginMessages result = Account.login(this.user);
		switch(result) {
		case SUCCESS:
			io.println("Login avvenuto con successo.");
			io.println("Sessione avviata con successo (SID:"+Account.getCurrentSessionId()+")");
			io.println("Ruolo d'accesso: "+this.user.getRole().getTitle());
			return true;
		case USER_NOT_FOUND:
			io.println("Errore: l'utente da lei inserito non è stato trovato.");
			return false;
		case CREATE_NEW_USER:
			io.println("Hai effettuato il login con le credenziali temporanee.");
			/*
			 * Il padre lo considero l'utente con credenziali temporanee.
			 * In base alle credenziali che usa avrà diverse temporary_references.
			 * La temporary reference è il riferimento al ruolo che il figlio deve avere.
			 */
			String role_name = null;
			Roles role_ref = this.user.getRole().getTemporary_reference();
			if(role_ref != null)
				role_name = role_ref.getTitle();
			else
				return false;
			io.println("Registra un nuovo utente come "+ role_name +":");
			this.parent_role = this.user.getRole().getTemporary_reference();
			return elaborateSignUp();
		default:
			io.println("Errore di I/O.");
			return false;
		}
	}
	
	/*
	 * POSTCONDIZIONI:
	 * se return true => Account.session != null, Account.session.user.role != null.
	 * altrimenti nessuna.
	 */
	/**
	 * Menù per l'elaborazione del login o della registrazione di un nuovo utente.
	 * @param is_login
	 * @return
	 */
	private boolean elaborate(boolean is_login) {
		if(is_login)
			io.println("Login");
		int max_attemps = 5, count = 0;
		while(count < max_attemps) {
			this.user = new User();
			if(count>0) {//se è sià stato fatto un tentativo, visualizza quanti tentativi rimangono.
				StringBuilder sb = new StringBuilder()
				.append("Per effettuare ")
				.append((is_login)?"il login":"la registrazione")
				.append(" ti sono rimasti ancora ")
				.append((max_attemps-count))
				.append(" tentativi.");
				io.println(sb.toString());
			}
			//Se falso readLoginParameters -> raggiunto il massimo numero di
			//tentativi per leggere la password o il nome utente.
			if(!readParameters()) //parametri letti scorrettamente
			{
				count++;
				continue;
			}
			if(is_login) {// se è login
				if(!tryDoLogin()) {
					//prova ad effettuare il login con
					//le credenziali settate da readParameters()
					count++;		
				 //se errore incremento il numero di tentativi fatti
				} else
					return true;
			}
			else { //se è una registrazione
				//setto il ruolo, avendo le credenziali già settate da readParameters()
				this.user.setRole(this.parent_role);
				if(!tryDoSignUp())//provo ad effettuare la registrazione
					count++; //se errore incremento il numero di tentativi fatti
				else
					return true;
			}
		}
		return false;//Raggiunto il massimo numero di tentativi.
	}
	
	/*
	 * PRECONDIZIONI:
	 * this.user != null
	 * POSTCONDIZIONI:
	 * user.username != null, user.password != null,
	 * 8 <= user.username.len <= 20, 8 <= user.password.len <= 20
	 */
	/**
	 * Legge i parametri.
	 * @return
	 */
	private boolean readParameters() {
		int max_attemps = 5,count = 0;
		while(count < max_attemps) {
			if(count>0)
				io.println("Per inserire l'username ti sono rimasti ancora "+(max_attemps-count)+" tentativi.");
			if(!readUsername()) {//prima leggo l'username
				io.println("Riprova per favore.");
				count++;
			}
			else {
				count = 0;
				while(count < max_attemps) {
					if(count>0)
						io.println("Per inserire la password ti sono rimasti ancora "+(max_attemps-count)+" tentativi.");
					if(!readPassword()) {//se tutto ok leggo la passwor
						io.println("Riprova per favore.");
						count++;
					}
					else
						return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * POSTCONDIZIONI:
	 * user.username != null, 8 <= username.len <= 20
	 */
	/**
	 * Legge l'username.
	 * Invia un messaggio d'errore in caso di fallimento.
	 * @return
	 */
	private boolean readUsername() {
		io.println("Inserisci username:");
		String str_read = io.readString();
		//controllo la stringa
		if(str_read!=null) {
			if(str_read.length()>=8 && str_read.length()<=20) {
				user.setUsername(str_read);
				return true;
			}
			else
				io.println("Non hai rispettato i vincoli di lunghezza [8,20].");
		}
		else
			io.println("L'username non può essere nullo.");
		return false;
	}
	
	/*
	 * POSTCONDIZIONI:
	 * user.password != null, 8 <= password.len <= 20
	 */
	/**
	 * Legge la password.
	 * Invia un messaggio d'errore in caso di fallimento.
	 * @return
	 */
	private boolean readPassword() {
		io.println("Inserisci password:");
		String str_read = io.readPassword();
		//controllo la stringa
		if(str_read!=null) {
			if(str_read.length()>=8 && str_read.length()<=20) {
				user.setPassword(APNUtils.getMd5(str_read));//<-- MD5!!
				return true;
			}
			else
				io.println("Non hai rispettato i vincoli di lunghezza [8,20].");
		}
		else
			io.println("La password non può essere nulla.");
		return false;
	}
}
