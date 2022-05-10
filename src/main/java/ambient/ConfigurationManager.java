package ambient;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Classe per gestire i parametri di configurazione (per ora solo paths).
 */
public class ConfigurationManager{
	private static final String BASE_PATH = System.getProperty("user.dir");
	private static final String DATA_FOLDER = "Data";
	private static final String CONFIGURATION_FOLDER = "Configuration";
	private static final String CONFIGURATION_FILE = "configuration.json";
	private static final Path CONFIG_PATH = Paths.get(BASE_PATH, CONFIGURATION_FOLDER, CONFIGURATION_FILE);
	
	/**
	 * true se è appena stato creato il ConfigurationManager.
	 */
	private boolean first_time;
	
	/**
	 * L'unico parametro (per ora) da salvare in locale.
	 * Expose serve per Gson.
	 * Indica la path relativa alla cartella Data dove verranno
	 * salvati i files json di DB.
	 */
	@Expose
	private String database_path;
	@Expose
	private IdManager id_manager;
	
	/**
	 * Inizializza la configurazione.
	 * @return true se inizializzato con successo.
	 */
	public boolean initialize(){
		if(!Files.exists(CONFIG_PATH)) { //se il file dovesse esistere
			first_time = true; //sarebbe la prima volta
			try {
				createDirectories(); // crea le directories
				setAndSaveInitialData(); // setta e salva i dati iniziali
				return true;
			} catch (IOException e) {
	        	return false;
	        }
		}
		else { // se non dovesse esistere il file
			first_time = false;//non sarebbe la prima volta
			/*
			 * Quindi recupera con Gson l'istanza della classe ConfigurationManager,
			 * in particolare la database_path.
			 */
			try (Reader reader = new FileReader(CONFIG_PATH.toString())) {
				ConfigurationManager cm = new GsonBuilder()
				  .excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
				  .fromJson(reader, ConfigurationManager.class);
				this.database_path = cm.database_path;
				this.id_manager = cm.id_manager;
				return true;
	        } catch (IOException e) {
	        	return false;
	        }
		}
	}
	
	/**
	 * Metodo che crea appunto le directories(paths).
	 * @throws IOException se qualcosa va storto.
	 */
	private void createDirectories() throws IOException {
		//costruisco la directory base => ../Configuration
		Path configuration_dir = Paths.get(BASE_PATH, CONFIGURATION_FOLDER);
		if(!Files.exists(configuration_dir))//se non esiste
			Files.createDirectory(configuration_dir);//creo la directory
		//creo il file => CONFIG_PATH = ../Configuration/config.json
		Files.createFile(CONFIG_PATH);//create full directory
		
		Path data_dir = Paths.get(BASE_PATH, DATA_FOLDER);
		if(!Files.exists(data_dir))//se non esiste
			Files.createDirectory(data_dir);//creo la directory
	}
	
	/**
	 * Metodo richiamato nel momento in cui per la prima volta si
	 * crea il file di configurazione.
	 * @throws IOException
	 */
	private void setAndSaveInitialData() throws IOException {
		//Creo la path dei dati
		Path temporary_init_path = Paths.get(BASE_PATH, DATA_FOLDER);
		this.database_path = temporary_init_path.toString();
		this.id_manager = IdManager.initialIdManager();
		//La scrivo su file
		try(Writer writer = new FileWriter(CONFIG_PATH.toString())){
			new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
			.setPrettyPrinting().create().toJson(this, writer);
		}
	}
	
	
	private static ConfigurationManager instance = null;
	public static boolean newInstance()
	{
		if(instance == null) {
			instance = new ConfigurationManager();//creo il configurationManager
			return instance.initialize();//lo inizializzo
		}
		else
			return true;
	}
	public static boolean saveChanges() {
		try(Writer writer = new FileWriter(CONFIG_PATH.toString())){
			new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
			.setPrettyPrinting().create().toJson(instance, writer);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean isFirstTime() {
		return (instance==null)?null:instance.first_time;
	}
	public static String getDatabase_path() {
		return  (instance==null)?null:instance.database_path;
	}
	public static IdManager getIdManager() {
		return (instance==null)?null:instance.id_manager;
	}
}
