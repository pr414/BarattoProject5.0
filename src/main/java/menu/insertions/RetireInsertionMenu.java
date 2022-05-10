package menu.insertions;

import java.io.IOException;

import account.Account;
import db.InsertionsDB;
import insertions.Insertion;
import insertions.Insertions;
import insertions.offerts.OffertStatus;
import menu.LogoutInterrupt;
import menu.MenuBase;
import menu.io.MenuIO;

public class RetireInsertionMenu extends MenuBase<Boolean> {

	public RetireInsertionMenu(MenuIO io) {
		super(io);
	}

	@Override
	public Boolean start() throws LogoutInterrupt, IOException {
		showMyInsertions();
		return true;
	}

	private void showMyInsertions() throws IOException {
		InsertionsDB db = new InsertionsDB();
		Insertions insertions = db.load();
		Insertion selected = menuSelectOptionFromHashMap(
			insertions.getInsertions(),
			(ins) -> {
				return new StringBuilder()
					.append("Per eliminare ").append(System.lineSeparator())
					.append(ins.toString()).toString();
			},
			(ins) -> {
				return ins.getUsername().equals(Account.getUserName())
						&& ins.getStatus().equals(OffertStatus.OPEN);
			},
			"Premi: ",
			"Per annullare l'operazione"
		);
		if(selected == null)
			return; // ha selezionato bac
		selected.retireOffert();
		db.save(insertions);
		io.println("Offerta ritirata con successo.");
	}
}
