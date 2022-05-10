package db;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Classe astratta per gestire il caricamento/salvataggio e i parsing da/a json
 * di classi generiche.
 * @param <T>
 */
abstract class DBBase<T> {
	private String path;//<- dove salverà/carichierà la stringa json
	private Gson builder;//<- classe google per la gestione del parsing
	private Class<T> c;//<- class del dato da salvare
	
	/*
	 * PRECONDIZIONI:
	 * c != null, (filePath != null or !filePatg.isEmpty())
	 */
	/**
	 * @param c
	 * @param filePath
	 */
	protected DBBase(Class<T> c, String filePath) {
		this.path = filePath;
		this.c = c;
		this.builder = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
			.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
			.setPrettyPrinting().create();
	}
	
	/*
	 * PRECONDIZIONI:
	 * path != null, exists(path)
	 * POSTCONDIZIONI:
	 * return instance(T) != null.
	 */
	/**
	 * Restituisce un'istanza del tipo di dato generico T creata in due passi:
	 * - leggendo la stringa json da file (definito da path)
	 * - effettuando il parsing Json grazie a Gson
	 * @return l'istanza del dato caricato.
	 * @throws IOException
	 */
	public T load() throws IOException  {
		try(Reader reader = new FileReader(path)){
			return builder.fromJson(reader, c);
		}
	}
	
	/*
	 * PRECONDIZIONI:
	 * path != null, exists(path), instance != null.
	 * POSTCONDIZIONI:
	 * instance(init) == instance(final).
	 * esiste un file con testo == instance.toJson()
	 */
	/**
	 * Converte l'instance in Stringa json e successivamente la salva grazie a Gson.
	 * @param instance Istanza da salvare
	 * @return true se salvato e convertito con successo.
	 */
	public boolean save(T instance) {
		try(FileWriter writer = new FileWriter(path)){
			builder.toJson(instance, writer);
			return true;
		}catch(IOException ex) {
			return false;
		}
	}
}
