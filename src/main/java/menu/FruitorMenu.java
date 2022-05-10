package menu;

import java.io.IOException;

import menu.insertions.NewInsertionsMenu;
import menu.insertions.RetireInsertionMenu;
import menu.insertions.offerts.OffertsMenu;
import menu.io.MenuIO;

/**
 * Menù relativo al fruitore.
 */
public class FruitorMenu extends MenuBase<Boolean> {

	public FruitorMenu(MenuIO io) {
		super(io);
	}

	@Override
	public Boolean start() throws LogoutInterrupt, IOException {
		boolean cont = true;
		String opt;
		while(cont) {
			io.println("Premi:");
			io.println("1 -> per visualizzare le gerarchie");
			io.println("2 -> per pubblicare un nuovo articolo");
			io.println("3 -> per ritirare l'offerta un articolo");
			io.println("4 -> per entrare nel menù delle offerte");
			io.println("logout -> per effettuare il logout");
			io.println("quit -> per uscire");
			opt = io.readString();
			switch(opt) {
			case LOGOUT_STR:
				logout();
				return false;
			case "1":
				try {
					showRootHierarchies();
				} catch (IOException e) {
					io.println("Errore durante la lettura delle gerarchie.");
				}
				break;
			case "2":
				new NewInsertionsMenu(io).start();
				break;
			case "3":
				new RetireInsertionMenu(io).start();
				break;
			case "4":
				new OffertsMenu(io).start();
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
}
