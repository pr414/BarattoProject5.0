package menu.hierarchies;

import java.util.HashMap;
import java.util.List;

import hierarchies.Category;
import hierarchies.Field;
import hierarchies.Hierarchies;
import hierarchies.Hierarchy;
import menu.LogoutInterrupt;
import menu.MenuBase;
import menu.io.MenuIO;

class CategoryManagerMenu extends MenuBase<Boolean> {
	private static final int NUMERO_MINIMO_CAT = 2;
	
	private Category parent;
	private NewHierarchyMenu context;
	
	public CategoryManagerMenu(MenuIO io, NewHierarchyMenu context, Category parent) {
		super(io);
		this.setContext(context);
		this.setParent(parent);
	}

	@Override
	public Boolean start() throws LogoutInterrupt {
		if(this.parent == null) {
			//prima inserisce la root
			Category root = menuNewCategory();
			//salvo le info della root
			//setto il parent
			this.parent = root;
		}
		while(true) {
			io.println("Ti trovi nella categoria <<"+parent.getName()+">>");
			io.println("Scrivi:");
			io.println("'new' -> per inserire una nuova categoria");
			io.println("'cd' -> per entrare in una nuova categoria");
			io.println("'ls' -> per visualizzare le categorie correnti");
			io.println("'cd..' -> per tornare alla categoria precedente / salvare");
			String opt = io.readString();
			switch(opt) {
			case "new":
				if(getHierarchy().getChildrenSize(parent)==0) {
					io.println("Devi inserire le prime due categorie figlie.");
					if(yes_no_request("Vuoi continuare con l'inserimento?")) {
						for(int i = 0; i < NUMERO_MINIMO_CAT; i++)
							menuNewCategory();
					}
				}
				else
					menuNewCategory();
				break;
			case "cd":
				menuEnterCategory();
				break;
			case "ls":
				List<Category> children = getHierarchy().getChildren(parent);
				if (children.isEmpty())
					io.println("Non sono presenti Categorie.");
				else {
					io.println("Categorie:");
					for(Category c : children)
						io.println(c.toString("\t"));
				}
				break;
			case "cd..":
				return true;
			default:
				io.println("Comando inesistente.");
				break;
			}
		}
	}
	
	/*
	 * PRECONDIZIONI:
	 * parent != null, alr_used != null, alr_used.len>=0
	 * POSTCONDIZIONI:
	 * alr_used.len >= alr_used.len(precedente)
	 */
	/**
	 * Metodo che permette di entrare nel contesto di un nuovo livello
	 * di categoria.
	 * Detto in altri termini visualizza le categorie e permette di "entrare" virtualmente in una
	 * nuova sottocategoria.
	 * @throws LogoutInterrupt 
	 */
	private void menuEnterCategory() throws LogoutInterrupt {
		while(true) {
			io.println("Categorie disponibili:");
			List<Category> children = getHierarchy().getChildren(getParent());
			for(Category c : children)
				io.println(c.getName());
			io.println("'quit' per tornare indietro");
			io.println();
			io.println("Scrivi il nome della categoria:");
			String cn = io.readString();
			if(cn.equals("quit"))
				return;
			Category selected = null;
			for(Category c : children)
				if(c.getName().equals(cn))
					selected = c;
			if(selected!=null) {
				new CategoryManagerMenu(io, this.getContext(), selected).start();
				return;
			}
			else
				io.println("Inserisci una categoria valida");
		}
	}

	
	/*
	 * PRECONDIZIONI:
	 * parent != null, alr_used != null, alr_used.len>=0
	 * POSTCONDIZIONI:
	 * per ogni i,j appartenenti [0, alr_used.len) alr_used[i] != alr_used[j],
	 * alr_used.len > alr_used.len(precedente)
	 * 
	 * (Notare lo STRETTAMENTE maggiore, al contrario del metodo menuEnterCategory
	 * e ho usato la notazione per array anche se è una stringa)
	 */
	/**
	 * Metodo per l'aggiunta di una nuova categoria in una gerarchia.
	 */
	private Category menuNewCategory() {
		while(true) {
			io.println("Inserisci il nome della categoria"+((this.parent == null)?" di root:":":"));
			String name = io.readString();
			if(this.parent == null && root_exists(name)) {
				io.println("Il nome della root è già esistente.");
				continue;
			}
			else if(!(this.parent == null) && this.getAlreadyUsed().contains(name)) {
				io.println("La categoria esiste già.");
				continue;
			}
			Category new_cat = new Category();
			new_cat.setName(name);//setto il nome
			io.println("Inserisci la descrizione:");
			String descr = io.readString();
			new_cat.setDescription(descr);//setto la descrizione
			//aggiungo i campi
			menuAddFields(new_cat);
			
			//setto il parent
			new_cat.setParent(this.parent);
			//aggiungo subito alla lista il nome
			this.getAlreadyUsed().add(name);
			//ho finito do creare la category
			//la aggiungo alla lista di categorie
			this.getHierarchy().newCategory(new_cat);
			//fine
			io.println("Inserito con successo.");
			return new_cat;
		}
	}

	/*
	 * PRECONDIZIONI:
	 * current != null
	 * POSTCONDIZIONI:
	 * current.fields.len >= current.fields.len(padre)
	 */
	/**
	 * Parte del menù per l'inserimento di nuovi campi di una categoria.
	 */
	private void menuAddFields(Category current) {
		if(!(this.parent == null)) {//NON è root
			HashMap<String,Field> old = parent.getFields();
			current.getFields().putAll(old);
		} else // E' root
			current.setFields(this.get_default_fields());
		//inizio il loop
		while(true) {
			if(yes_no_request("Vuoi inserire nuovi campi?")) {
				Field f = new Field();
				io.println("Nome del campo:");
				String fname = io.readString();
				f.setName(fname);
				boolean fmandatory = false;
				if(yes_no_request("Il campo è obbligatorio?"))
					fmandatory = true;
				f.setMandatory(fmandatory);
				current.getFields().put(fname, f);
			}
			else
				return;
		}
	}
	
	/**
	 * Metodo che restituisce la lista dei campi di base
	 * che ogni categoria root deve possedere.
	 */
	private HashMap<String, Field> get_default_fields() {
		HashMap<String, Field> fields = new HashMap<String, Field>();
		String s1 = "stato conservazione";
		String s2 = "descrizione libera";
		fields.put(s1, new Field(s1, true));
		fields.put(s2, new Field(s2, false));
		return fields;
	}
	
	/*
	 * PRECONDIZIONI:
	 * toFind != null, db != null
	 * POSTCONDIZIONI:
	 * db.hierarchies.len == db.hierarchies.len(precedente)
	 */
	/**
	 * Verifica se esiste già una gerarchia avente root di nome toFind.
	 * @param toFind
	 * @param db
	 * @return true se esiste.
	 */
	private boolean root_exists(String root_name) {
		Hierarchies hs = this.getContext().getHierarchiesInstance();
		for(Hierarchy hierarchy : hs.getHierarchies())
			if(hierarchy.getRoot().getName().equals(root_name))
				return true;
		return false;
	}
	
	//GETTERS & SETTERS
	public NewHierarchyMenu getContext() {
		return context;
	}
	public void setContext(NewHierarchyMenu context) {
		this.context = context;
	}
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	private List<String> getAlreadyUsed(){
		return this.getContext().getAlready_used();
	}
	private Hierarchy getHierarchy() {
		return this.getContext().getHierarchy();
	}
}
