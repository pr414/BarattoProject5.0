package db;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import hierarchies.Category;
import hierarchies.Hierarchies;
import insertions.Insertion;
import insertions.Insertions;

public class InsertionsDB extends DBBase<Insertions> {
	private static String INSERTIONS_JSON = "insertions.json";
	static Path insertionsPath;
	
	public InsertionsDB() {
		super(Insertions.class, insertionsPath.toString());
	}

	@Override
	public Insertions load() throws IOException{
		Insertions loaded = super.load();
		do_interconnections(loaded);
		return loaded;
	}
	
	private void do_interconnections(Insertions loaded) throws IOException {
		Hierarchies hierarchies = new HierarchiesDB().load();
		/* Per ogni Inserzione devo popolare la categoria di riferimento */
		for(Map.Entry<String, Insertion> entry : loaded.getInsertions().entrySet()) {
			long categoryId = entry.getValue().getCategory_id();
			Category cat = hierarchies.getCategoryFromId(categoryId);
			entry.getValue().setCategory_reference(cat);
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
		insertionsPath = Paths.get(Database.getDataPath(), INSERTIONS_JSON);
	}
	
	/**
	 * Metodo (opzionalmente) richiamato da Database nel momento in cui non esiste
	 * il file .json delle gerarchie.
	 * @return true successo operazione
	 */
	static boolean onNewDB() {
		return new InsertionsDB().save(new Insertions());
	}
}
