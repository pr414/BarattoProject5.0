package db;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import hierarchies.Hierarchies;
import hierarchies.Hierarchy;

/**
 * Classe per il caricamento/ salvataggio delle gerarchie.
 */
public class HierarchiesDB extends DBBase<Hierarchies> {
	private static String HIERARCHIES_JSON = "hierarchies.json";
	static Path hierarchiesPath;
	
	public HierarchiesDB() {
		super(Hierarchies.class, hierarchiesPath.toString());
	}
	public HierarchiesDB(String path) {
		super(Hierarchies.class, path);
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
		hierarchiesPath = Paths.get(Database.getDataPath(), HIERARCHIES_JSON);
	}
	
	/**
	 * Metodo (opzionalmente) richiamato da Database nel momento in cui non esiste
	 * il file .json delle gerarchie.
	 * @return true successo operazione
	 */
	static boolean onNewDB() {
		Hierarchies h = new Hierarchies();
		List<Hierarchy> list = new ArrayList<Hierarchy>();
		h.setHierarchies(list);
		return new HierarchiesDB().save(h);
	}
}