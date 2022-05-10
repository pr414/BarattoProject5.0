package db;

/**
 * Eccezione per operazioni non ammesse durante salvataggi/load nei database.
 */
public class DatabaseException extends Exception{
	private static final long serialVersionUID = 7964850487474410989L;

	public DatabaseException(String description) {
		super(description);
	}
}
