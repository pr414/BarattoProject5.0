package hierarchies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

import ambient.ConfigurationManager;
import ambient.IdManager;

public class Hierarchy {
	public static String[] DAYS_OF_WEEK = {
		"lunedì", "lunedi",
		"martedì", "martedi",
		"mercoledì", "mercoledi",
		"giovedì", "giovedi",
		"venerdì", "venerdi",
		"sabato", "domenica"
	};

	@Expose
	private long id;
	@Expose
	private HashMap<Integer,Category> categories;
	@Expose
	private HashMap<String,Integer> int_map;
	@Expose
	private String piazza;
	@Expose
	private List<String> luoghi;
	@Expose
	private List<String> giorni;
	@Expose
	private List<Interval> intervalli;
	@Expose
	private int scadenza;
	
	public Hierarchy() {
		this.setLuoghi(new ArrayList<String>());
		this.setGiorni(new ArrayList<String>());
		this.setIntervalli(new ArrayList<Interval>());
		this.setCategories(new HashMap<Integer,Category>());
		this.setInt_map(new HashMap<String,Integer>());
	}

	/*
	 * PRECONDIZIONI:
	 * tab != null
	 */
	public String toString(String tab) {
		StringBuilder sb = new StringBuilder();
		//printo l'albero
		sb.append("------------------------------------------------")
			.append(System.lineSeparator())
			.append("-------------------GERARCHIA--------------------")
			.append(System.lineSeparator())
			.append("Info gerarchia:")
			.append(System.lineSeparator());
		
		toStringInfo(sb, tab);//printo le informazioni
		sb.append("------------------------------------------------")
			.append(System.lineSeparator());
		
		toStringCategories(sb, tab);//printo le categorie
		sb.append("------------------------------------------------")
			.append(System.lineSeparator());
		return sb.toString();
	}
	
	
	public Category getRoot() {
		return this.getCategories().get(IdManager.ROOT_ID);
	}
	
	public List<Category> getChildren(Category parent){
		List<Category> children = new ArrayList<Category>();
		for(Map.Entry<Integer, Category> entry : this.getCategories().entrySet()) {
			if(entry.getValue().getParent() == null) {
				if(parent == null)
					children.add(entry.getValue());
			}
			else {//entry parent != null
				if(entry.getValue().getParent().equals(parent))
					children.add(entry.getValue());
			}
		}
		return children;
	}
	
	public int getChildrenSize(Category parent) {
		int i = 0;
		for(Map.Entry<Integer, Category> entry : this.getCategories().entrySet()) {
			if(entry.getValue().getParent() == null) {
				if(parent == null)
					i++;
			}else {
				if(entry.getValue().getParent().equals(parent))
					i++;
			}
		}
		return i;
	}
	
	/**
	 * Metodo che trasforma in stringa la gerarchia.
	 * @param sb
	 * @param tab
	 */
	private void toStringCategories(StringBuilder sb,String tab) {
		if(this.getCategories().size()==0) {//nessuna categoria presente
			sb.append("Nessuna categoria presente.").append(System.lineSeparator());
			return;
		}//altrimenti printo le category
		toStringCategoryWithParent(sb, tab, null);
	}
	
	/**
	 * Metodo ricorsivo che trasforma stringa in gerarchia aggiungendo tab di volta in volta
	 * e ritorna il numero di categorie toStringate.
	 * @param sb
	 * @param tab
	 * @param parent
	 * @return
	 */
	private int toStringCategoryWithParent(StringBuilder sb, String tab, Category parent) {
		for(Map.Entry<Integer,Category> entry : this.getCategories().entrySet()) {//per ogni categoria
			Category current = entry.getValue();
			if(parent == null && current.getParent() == null)//se è root
			{
				sb.append(entry.getValue().toString(tab));
				sb.append(System.lineSeparator());
				String newTab = tab+'\t';
				toStringCategoryWithParent(sb, newTab, current);
				
			}
			else if(parent != null && current.getParent() != null) {//altrimenti
				if(current.getParent().equals(parent)) {
					String newTab = tab+'\t';
					sb.append(entry.getValue().toString(newTab));
					sb.append(System.lineSeparator());
					newTab = newTab+'\t';
					toStringCategoryWithParent(sb, newTab, current);
				}
			}
		}
		return 1;
	}
	
	
	private void toStringInfo(StringBuilder sb,String tab) {
		sb.append(tab)
		.append("Piazza: ").append(this.getPiazza())
		.append(System.lineSeparator());
		//printo i luoghi
		sb.append(tab).append("Luoghi: ");
		for(int i = 0; i < this.getLuoghi().size(); i++) {
			sb.append(this.getLuoghi().get(i));
			if(i+1<this.getLuoghi().size())
				sb.append(", ");
		}
		sb.append(System.lineSeparator());
		sb.append(tab).append("Giorni: ");
		for(int i = 0; i < this.getGiorni().size(); i++) {
			sb.append(this.getGiorni().get(i));
			if(i+1<this.getGiorni().size())
				sb.append(", ");
		}
		sb.append(System.lineSeparator());
		sb.append(tab).append("Intervalli orari: ");
		for(int i = 0; i < this.getIntervalli().size(); i++) {
			sb.append(this.getIntervalli().get(i));
			if(i+1<this.getIntervalli().size())
				sb.append(", ");
		}
		sb.append(System.lineSeparator())
			.append(tab)
			.append("Scadenza (in giorni): ").append(this.getScadenza())
			.append(System.lineSeparator());
	}
	
	/**
	 * Metodo che genera un nuovo id di una categoria e lo setta nella classe category.
	 * NB: incrementa anche il valore di IdManager.
	 * @param context
	 * @param category
	 */
	public void newCategory(Category category) {
		//alla fine setto l'id
		long otherid=getId();//4294967296
		long id = ConfigurationManager.getIdManager().nextCategoryId(otherid);
		category.setId(id);//4294967297 correct
		this.getCategories().put((int)id, category);
	}
	
	
	public String getPiazza() {
		return piazza;
	}
	public void setPiazza(String piazza) {
		this.piazza = piazza;
	}
	public List<String> getLuoghi() {
		return luoghi;
	}
	public void setLuoghi(List<String> luoghi) {
		this.luoghi = luoghi;
	}
	public List<String> getGiorni() {
		return giorni;
	}
	public void setGiorni(List<String> giorni) {
		this.giorni = giorni;
	}
	public List<Interval> getIntervalli() {
		return intervalli;
	}
	public void setIntervalli(List<Interval> intervalli) {
		this.intervalli = intervalli;
	}
	public int getScadenza() {
		return scadenza;
	}
	public void setScadenza(int scadenza) {
		this.scadenza = scadenza;
	}
	public long getId() {
		return this.id;//hierarchy id
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setNewId() {
		this.id = ConfigurationManager.getIdManager().nextHierarchyId();
	}
	public HashMap<Integer, Category> getCategories() {
		return categories;
	}
	public void setCategories(HashMap<Integer, Category> categories) {
		this.categories = categories;
	}
	public HashMap<String, Integer> getInt_map() {
		return int_map;
	}
	public void setInt_map(HashMap<String, Integer> int_map) {
		this.int_map = int_map;
	}
	
	public static boolean is_day_of_week(String str) {
		for(String s : DAYS_OF_WEEK)
			if(s.equals(str))
				return true;
		return false;
	}
}
