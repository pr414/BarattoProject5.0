package menu.insertions.offerts;

import java.io.IOException;
import java.util.HashMap;

import account.Account;
import db.InsertionsDB;
import insertions.Insertion;
import insertions.Insertions;
import insertions.offerts.OffertStatus;
import menu.LogoutInterrupt;
import menu.MenuBase;
import menu.io.MenuIO;

public class NewOffertMenu extends MenuBase<Boolean> {

	public NewOffertMenu(MenuIO io) {
		super(io);
	}

	@Override
	public Boolean start() throws LogoutInterrupt, IOException {
		InsertionsDB db = new InsertionsDB();
		Insertions insertions = db.load();
		HashMap<String,Insertion> insertion_map = insertions.getInsertions();
		Insertion selected = menuSelectOptionFromHashMap(
			insertion_map, //mappa da cui selezionare l'inserzione
			(ins) -> { //condizioni di filtro delle entry
				return ins.getUsername().equals(Account.getUserName())
						&& ins.getStatus().equals(OffertStatus.OPEN);
			},
			// titolo da visualizzare all'inizio del menù si selezione
			"Premi per scambiare:"
		);
		if(selected != null) {// NOT back
			match(selected, insertion_map); //inserzioni modificate!!!
			db.save(insertions);//le salvo
			io.println("Matching effettuato con successo!");
		}
		//ritorna true se è back (selected==null) oppure se è andato tutto
		//per il verso giusto.
		return true;
	}

	/**
	 * Effettua il matching tra l'offerta riferita all'inserzione dell'utente corrente
	 * e l'inserzione di un altro utente.
	 * @param offert
	 * @param insertions
	 * @return true per continuare il menù precedente, false per interromperlo.
	 * @throws IOException 
	 */
	private void match(Insertion offert, HashMap<String,Insertion> insertion_map) throws IOException {
		Insertion selected_receiver = menuSelectOptionFromHashMap( //di default visualizza oggetto.toString()
			insertion_map, //mappa da cui selezionare l'inserzione
			(ins) -> { //condizioni di filtro delle entry
				return ins.getCategory_id() == offert.getCategory_id()
						&& ins.getStatus().equals(OffertStatus.OPEN)
						&& !ins.getUsername().equals(Account.getUserName());
			},
			// titolo da visualizzare all'inizio del menù si selezione
			"da scambiare con:"
		);
		if(selected_receiver != null) {
			offert.matchOffert(selected_receiver);
			//Mi raccomando devo salvare le due inserzioni perchè sono state modificate!!
		}
	}
}
