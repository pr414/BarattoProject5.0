package db;

import java.nio.file.Path;
import java.nio.file.Paths;

import insertions.history.StatusHistory;

public class StatusHistoryDB extends DBBase<StatusHistory> {
	private static String FILE_JSON = "status_history.json";
	static Path statusHistoryPath;
	
	public StatusHistoryDB() {
		super(StatusHistory.class, statusHistoryPath.toString());
	}
	
	/*
	 * PRECONDIZIONI:
	 * Database.getDataPath() != null
	 */
	/**
	 * Metodo richiamato durante l'inizializzazione del database.
	 * Ovviamente se non lo richiamo nella classe Database non succede nulla.
	 * In particolare salva la path per salvare/recuperare le gerarchie.
	 */
	static void setPath() {
		statusHistoryPath = Paths.get(Database.getDataPath(), FILE_JSON);
	}
	
	/**
	 * Metodo (opzionalmente) richiamato da Database nel momento in cui non esiste
	 * il file .json delle gerarchie.
	 * @return true successo operazione
	 */
	static boolean onNewDB() {
		return new StatusHistoryDB().save(new StatusHistory());
	}
}
