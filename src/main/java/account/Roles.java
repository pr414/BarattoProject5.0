package account;

/**
 * Enumeratore che rappresenta la lista completa di tutti i ruoli che può
 * assumere un utente. NB: se un elemento dell'enum ha un ruolo di riferimento,
 * allora significa che si potrà e si dovrà necessariamente creare un nuovo
 * account (con ruolo = ruolo di riferimento).
 */
public enum Roles
{
	CONFIGURATOR(1, "Configuratore"),
	CONFIGURATOR_CREATOR(30, "Creatore di un nuovo configuratore", CONFIGURATOR),
	FRUITOR(2, "Fruitore"),
	FRUITOR_CREATOR(31, "Creatore di un nuovo fruitore", FRUITOR);
	
	private int id;
	private String title;
	private Roles temporary_reference;
	
	Roles(int id, String title){
		this.id = id;
		this.title = title;
		this.temporary_reference = null;
	}
	Roles(int id, String title, Roles ref_role){
		this.id = id;
		this.title = title;
		this.temporary_reference = ref_role;
	}
	
	/**
	 * Ritorna l'enum specifico relativo all'identificativo in input.
	 * @return Roles se lo trova. Null altrimenti.
	 */
	public static Roles fromId(int id) {
		for(int i = 0; i < Roles.values().length; i++)
			if(Roles.values()[i].getId()==id)
				return Roles.values()[i];
		return null;
	}
	
	
	public boolean must_new_credentials() {
		return this.temporary_reference!=null;
	}
	
	public int getId() {
		return this.id;
	}
	public String getTitle() {
		return this.title;
	}
	public Roles getTemporary_reference() {
		return temporary_reference;
	}
}