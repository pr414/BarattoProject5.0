package menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import account.Account;
import db.HierarchiesDB;
import hierarchies.Hierarchy;
import menu.io.MenuIO;

public abstract class MenuBase<T> implements IMenu<T>{
	protected static final String LOGOUT_STR = "logout";
	protected static final String DEFAULT_BACK_MESSAGE = "per tornare indietro";
	
	protected MenuIO io;
	
	public MenuBase(MenuIO io) {
		this.io = io;
	}
	
	/*
	 * POSTCONDIZIONI:
	 * Account.current == null
	 */
	/**
	 * Metodo necessario per interrompere il menù in caso di logout. (Effettua anche il logout).
	 * @throws LogoutInterrupt
	 */
	protected void logout() throws LogoutInterrupt {
		Account.logout();
		io.println("Logout avvenuto con successo!");
		throw new LogoutInterrupt();
	}
	
	// ------- START SHOW HIERARCHIES REGION
	
	private void showHierarchies(boolean onlyRoot) throws IOException {
		List<Hierarchy> hierarchies = new HierarchiesDB().load().getHierarchies();
		if(hierarchies.size()>0) {
			for(Hierarchy hierarchy : hierarchies) {
				if(onlyRoot)
					io.println(hierarchy.getRoot().toString(""));
				else
					io.println(hierarchy.toString(""));
			}
		}
		else
			io.println("Non sono presenti ancora gerarchie.");
	}
	/**
	 * Metodo che visualizza solo le categorie radice di tutte le gerarchie presenti.
	 * @throws IOException
	 */
	protected void showRootHierarchies() throws IOException {
		showHierarchies(true);//onlyRoot = true;
	}
	
	protected void showHierarchies() throws IOException {
		showHierarchies(false);//onlyRoot = false;
	}
	// ------- END SHOW HIERARCHIES REGION
	
	
	
	
	/**
	 * y per yes n per no
	 * @param title
	 * @return
	 */
	protected boolean yes_no_request(String title) {
		return yes_no_request(title,"y","n");
	}
	protected boolean yes_no_request(String title, String yes, String no) {
		StringBuilder sb = new StringBuilder();
		sb.append(title).append('(').append(yes).append('/').append(no).append(')');
		StringBuilder sbError = new StringBuilder();
		sbError.append("Non hai inserito un'opzione valida: scrivi \"")
		.append(yes).append(" per confermare, oppure ").append(no);
		while(true) {
			io.println(sb.toString());
			String opt = io.readString();
			if(opt.equals(yes))
				return true;
			else if(opt.equals(no))
				return false;
			else
				io.println(sbError.toString());
		}
	}
	
	/**
	 * Default backable
	 * @param <C>
	 * @param list
	 * @param toString
	 * @return
	 */
	protected <C> C menuSelectOptionFromList(List<C> list, Function<C, String> toString, String back_message){
		return menuSelectOptionFromList(list, toString, true, back_message);
	}
	
	protected <C> C menuSelectOptionFromListNotBackable(List<C> list, Function<C, String> toString){
		return menuSelectOptionFromList(list, toString, false, null);
	}
	
	protected <C> C menuSelectOptionFromList(List<C> list,
			Function<C, String> toString,
			boolean backable, String back_message){
		while(true) {
			io.println("Premi:");
			for(int i = 0; i < list.size(); i++) {
				C element = list.get(i);
				String toPrint = toString.apply(element);
				io.println((i+1)+"> per selezionare "+toPrint);
			}
			if(backable)
				io.println("back> "+back_message);
			String opt = io.readString();
			if(opt != null) {
				if(backable && opt.equals("back"))
					return null;
				try {
					int opt_int = Integer.parseInt(opt);
					if(opt_int-- <= list.size()) {//confronto il numero e successivamente riduco a index
						C result = list.get(opt_int);//uso l'index
						io.println("Hai selezionato " + toString.apply(result));
						return result;
					}else
						io.println("Hai inserito un numero non valido.");
				}catch(NumberFormatException e) {
					io.println("Non hai inserito un numero.");
				}
			}
			else
				io.println("Seleziona una delle opzioni.");
		}
	}
	
	
	protected <C> C menuSelectOptionFromList(List<C> in_list,
			Function<C, String> toString, Function<C,Boolean> satisfy,
			String title, String back_message) {
		List<C> c_list = new ArrayList<C>();
		while(true) {
			io.println(title);
			int i = 1;
			for(C instance : in_list) {
				if(satisfy.apply(instance)) {
					String toPrint = toString.apply(instance);
					//è mio
					io.println(i+">");//segnalo la posizione
					io.println(toPrint);
					c_list.add(instance);
					i++;
				}
			}
			io.println("back> "+back_message);
			String opt = io.readString();
			if(opt != null) {
				if(opt.equals("back"))
					return null;
				try {
					int index = Integer.parseInt(opt) - 1;
					C result = c_list.get(index);
					return result;
				}catch(Exception e) {
					io.println("Non hai inserito un numero.");
				}
			} else
				io.println("Tasto non riconosciuto.");
		}
	}
	
	
	
	protected <K,C> C menuSelectOptionFromHashMap(HashMap<K,C> in_map,
			Function<C,Boolean> satisfy, String title){
		return menuSelectOptionFromHashMap(in_map,
				(c)->c.toString(), satisfy, title, DEFAULT_BACK_MESSAGE);
	}
	
	/**
	 * Metodo che permette la selezione di un'opzione da un'hashmap.
	 * 
	 * @param <K>
	 * @param <C>
	 * @param in_map
	 * @param toString
	 * @param satisfy
	 * @param title
	 * @param back_message
	 * @return null per "back", C altrimenti.
	 */
	protected <K,C> C menuSelectOptionFromHashMap(HashMap<K,C> in_map,
			Function<C, String> toString, Function<C,Boolean> satisfy,
			String title, String back_message){
		List<C> c_list = new ArrayList<C>();
		while(true) {
			io.println(title);
			int i = 1;
			for(Map.Entry<K, C> entry : in_map.entrySet()) {
				if(satisfy.apply(entry.getValue())) {
					String toPrint = toString.apply(entry.getValue());
					//è mio
					io.println(i+">");
					io.println(toPrint);
					c_list.add(entry.getValue());
					i++;
				}
			}
			io.println("back> "+back_message);
			String opt = io.readString();
			if(opt != null) {
				if(opt.equals("back"))
					return null;
				try {
					int index = Integer.parseInt(opt) - 1;
					C result = c_list.get(index);
					return result;
				}catch(Exception e) {
					io.println("Non hai inserito un numero.");
				}
			} else
				io.println("Tasto non riconosciuto.");
		}
	}
}
