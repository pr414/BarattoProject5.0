package menu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ambient.ConfigurationManager;
import db.DatabaseException;
import db.HierarchiesDB;
import hierarchies.Category;
import hierarchies.Hierarchies;
import hierarchies.Hierarchy;
import menu.hierarchies.NewHierarchyMenu;
import menu.insertions.offerts.ShowOpenOffertsMenu;
import menu.io.MenuIO;

/**
 * Menù relativo al configuratore.
 */
class ConfiguratorMenu extends MenuBase<Boolean>{

	public ConfiguratorMenu(MenuIO io) throws DatabaseException, IOException {
		super(io);
	}

	@Override
	public Boolean start() throws LogoutInterrupt, IOException {
		boolean cont = true;
		String opt;
		while(cont) {
			io.println("Premi:");
			io.println("1 -> per visualizzare le gerarchie");
			io.println("2 -> per aggiungere una nuova gerarchia");
			io.println("3 -> per vedere le offerte per categoria aperte, in scambio e chiuse");
			io.println("import -> per importare le gerarchie da file");
			io.println("logout -> per effettuare il logout");
			io.println("quit -> per uscire");
			opt = io.readString();
			switch(opt) {
				case LOGOUT_STR:
					logout();
				case "1":
					try {
						showHierarchies();
					} catch (IOException e) {
						io.println("Errore durante la lettura delle gerarchie.");
					}
					break;
				case "2":
					new NewHierarchyMenu(io).start();
					break;
				case "3":
					try {
						new ShowOpenOffertsMenu(io).start();
					} catch (IOException e) {
						io.println("Errore di I/O");
					}
					break;
				case "import":
					io.println("Inserisci la path del file da importare:");
					String path = io.readString();
					boolean sovr = super.yes_no_request("Vuoi sovrascrivere tutte le precedenti categorie con le nuove?");
					HierarchiesDB new_db = new HierarchiesDB(path);
					if(sovr) {
						HierarchiesDB.onNewDB();
						//sovrascrive anche le altre categorie.
						overWriteHierarchies(new_db, path);
					}else {
						HierarchiesDB current = new HierarchiesDB();
						//effettua un append
						appendHierarchies(new_db,current);
					}
					io.println("Importato con successo!");
					break;
				case "quit":
					return false;
				default:
					io.println("Il tasto premuto non consente di effettuare alcuna operazione. Per favore riprovare.");
					continue;
			}
		}
		return false;
	}
	
	
	private void overWriteHierarchies(HierarchiesDB new_db, String path) throws IOException {
		//Prendo la path dati
		Path destination = Paths.get(
			ConfigurationManager.getDatabase_path(),
			HierarchiesDB.HIERARCHIES_JSON
		);
		//Elimino se esiste
		Files.deleteIfExists(destination);
		HierarchiesDB.onNewDB();
		
		Hierarchies to_add = new_db.load();
		HierarchiesDB current = new HierarchiesDB();
		Hierarchies current_hierarchies = current.load();
		for(Hierarchy h : to_add.getHierarchies()) {
			adjustHierarchy(h);
			current_hierarchies.getHierarchies().add(h);
		}
		current.save(current_hierarchies);
	}
	
	private void appendHierarchies(HierarchiesDB new_db, HierarchiesDB current) throws IOException {
		Hierarchies to_add = new_db.load();
		Hierarchies current_hierarchies = current.load();
		for(Hierarchy h : to_add.getHierarchies()) {
			adjustHierarchy(h);
			int index = hierarchyExists(h, current_hierarchies);
			if(index != -1) {
				io.println("La categoria di root \""+h.getRoot().getName()+"\" esiste già.");
				if(super.yes_no_request("Vuoi sovrascriverla?")) {
					//sovrascrivi
					current_hierarchies.getHierarchies().remove(index);
					current_hierarchies.getHierarchies().add(h);
				}
				//lasciarla inalterata
			}
			else {
				current_hierarchies.getHierarchies().add(h);
			}
		}
		current.save(current_hierarchies);
	}
	
	private void adjustHierarchy(Hierarchy newh) {
		HashMap<Long,Category> old_new = new HashMap<Long,Category>();
		//Creo un identificativo valido
		newh.setNewId();
		long otherid = newh.getId();
		Category root = newh.getRoot();
		//Renew Id
		for(Entry<Integer, Category> cat : newh.getCategories().entrySet()) {
			long id = ConfigurationManager.getIdManager().nextCategoryId(otherid);
			old_new.put(cat.getValue().getId(), cat.getValue());
			cat.getValue().setId(id);
		}
		//Adjust parent
		for(Entry<Integer, Category> cat : newh.getCategories().entrySet()) {
			if(cat.getValue().getParent()==null) {
				//ignoro è la root
				continue;
			}
			long parentid = cat.getValue().getParent().getId();
			cat.getValue().setParent(old_new.get(parentid));
		}
	}
	
	private int hierarchyExists(Hierarchy h, Hierarchies current_hierarchies) {
		for(int i = 0; i < current_hierarchies.getHierarchies().size(); i++) {
			Hierarchy to_comp = current_hierarchies.getHierarchies().get(i);
			if(to_comp.getRoot().getName().equals(h.getRoot().getName())) {
				return i;
			}
		}
		return -1;
	}
}
