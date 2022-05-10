package menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseException;
import db.HierarchiesDB;
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
					HierarchiesDB new_db = new HierarchiesDB(path);
					HierarchiesDB current = new HierarchiesDB();
					boolean sovr = super.yes_no_request("Vuoi sovrascrivere le altre categorie?");
					if(sovr) {
						//sovrascrive anche le altre categorie.
						overWriteHierarchies(new_db,current);
					}else {
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
	
	
	private void overWriteHierarchies(HierarchiesDB new_db, HierarchiesDB current) throws IOException {
		Hierarchies to_add = new_db.load();
		current.load();
		current.save(to_add);
	}
	
	private void appendHierarchies(HierarchiesDB new_db, HierarchiesDB current) throws IOException {
		Hierarchies to_add = new_db.load();
		Hierarchies current_hierarchies = current.load();
		for(Hierarchy h : to_add.getHierarchies()) {
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
		}
		current.save(current_hierarchies);
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
