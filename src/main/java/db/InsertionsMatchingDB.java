package db;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import insertions.Insertion;
import insertions.Insertions;
import insertions.offerts.InsertionsMatching;
import insertions.offerts.Meeting;

public class InsertionsMatchingDB extends DBBase<InsertionsMatching>{
	private static String INSERTIONS_MATCHING_JSON = "insertions_matching.json";
	static Path insertionsMatchingPath;
	
	public InsertionsMatchingDB() {
		super(InsertionsMatching.class, insertionsMatchingPath.toString());
	}

	
	@Override
	public InsertionsMatching load() throws IOException{
		InsertionsMatching loaded = super.load();
		do_interconnections(loaded);
		return loaded;
	}
	
	
	
	private void do_interconnections(InsertionsMatching loaded) throws IOException {
		Insertions insertions = new InsertionsDB().load();
		for(Meeting m : loaded.getMeeting()) {
			String from_id = m.getFrom_insertion_id();
			Insertion from_insertion = insertions.getInsertions().get(from_id);
			m.setFrom_insertion(from_insertion);
			String to_id = m.getTo_insertion_id();
			Insertion to_insertion = insertions.getInsertions().get(to_id);
			m.setTo_insertion(to_insertion);
		}
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
		insertionsMatchingPath = Paths.get(Database.getDataPath(), INSERTIONS_MATCHING_JSON);
	}
	
	/**
	 * Metodo (opzionalmente) richiamato da Database nel momento in cui non esiste
	 * il file .json delle gerarchie.
	 * @return true successo operazione
	 */
	static boolean onNewDB() {
		return new InsertionsMatchingDB().save(new InsertionsMatching());
	}
}