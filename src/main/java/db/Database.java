package db;

import java.nio.file.Files;

import ambient.ConfigurationManager;

/**
 * Classe per la gestione globale delle variabili in comune tra tutti i database.
 */
public class Database{
	//Path nella quale verranno salvati ( o recuperati) i files json dei dati da salvare.
	private static String dataPath;
	
	/**
	 * Metodo che deve essere eseguito durante l'inizializzazione del programma, ma dopo
	 * l'inizializzatore del configuratore.
	 * Inizializza il database.
	 * @throws DatabaseException
	 */
	public static void initialize() throws DatabaseException {
		initializeDatabasesPaths();
		checkExistances();
	}
	
	/**
	 * Metodo che inizializza le path di ogni tipo di dato da salvare: prima la path base
	 * che la recupera dal configuration manager, poi le path di ogni singolo DB.
	 * "PRECONDIZIONE" fondamentale per il corretto funzionamento:
	 * ConfigurationManager inizializzato precedentemente.
	 * @throws DatabaseException
	 */
	private static void initializeDatabasesPaths() throws DatabaseException {
		//se la databasepath non esiste allora => errore necessariamente.
		if(ConfigurationManager.getDatabase_path()==null)
			throw new DatabaseException("Il ConfigurationManager non è stato inizializzato correttamente.");
		dataPath = ConfigurationManager.getDatabase_path();
		
		UsersDB.setPath();
		HierarchiesDB.setPath();
		InsertionsDB.setPath();
		StatusHistoryDB.setPath();
		InsertionsMatchingDB.setPath();
	}
	
	/**
	 * Metodo eseguito successivamente all'inizializzazione delle paths che controlla
	 * l'esistenza dei files. Se non esistono da la possibilità alle varie classi DB
	 * di gestire la situazione tramite il metodo onNewDB().
	 * NB: se non si richiama il metodo save(T) non si salverà alcun files e, se si dovesse
	 * riaccedere si riscontrerebbe sempre lo stesso problema.
	 */
	private static void checkExistances() {
		if(!Files.exists(UsersDB.usersPath))
			UsersDB.onNewDB();
		if(!Files.exists(HierarchiesDB.hierarchiesPath))
			HierarchiesDB.onNewDB();
		if(!Files.exists(InsertionsDB.insertionsPath))
			InsertionsDB.onNewDB();
		if(!Files.exists(StatusHistoryDB.statusHistoryPath))
			StatusHistoryDB.onNewDB();
		if(!Files.exists(InsertionsMatchingDB.insertionsMatchingPath))
			InsertionsMatchingDB.onNewDB();
	}
	
	//getter del dataPath solo a livello package
	static String getDataPath() {
		return dataPath;
	}
}
