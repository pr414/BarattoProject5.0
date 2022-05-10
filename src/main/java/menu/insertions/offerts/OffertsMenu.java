package menu.insertions.offerts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import account.Account;
import db.InsertionsDB;
import insertions.Insertion;
import insertions.Insertions;
import menu.LogoutInterrupt;
import menu.MenuBase;
import menu.io.MenuIO;

public class OffertsMenu extends MenuBase<Boolean>{

	public OffertsMenu(MenuIO io) {
		super(io);
	}

	@Override
	public Boolean start() throws LogoutInterrupt, IOException {
		try {
			while(true) {
				io.println("Premi:");
				io.println("1 -> per vedere le offerte aperte per categoria");
				io.println("2 -> per vedere le tue offerte");
				io.println("3 -> per fare un'offerta");
				io.println("4 -> per vedere se hai offerte di matching");
				io.println("cd.. -> per tornare al menù precedente");
				String opt = io.readString();
				switch(opt) {
				case "1":
					new ShowOpenOffertsMenu(io).start();
					break;
				case "2":
					showMyOfferts();
					break;
				case "3":
					new NewOffertMenu(io).start();
					break;
				case "4":
					new OffertsManagementMenu(io).start();
					break;
				case "cd..":
					return true;
				default:
					io.println("Comando inesistente.");
					break;
				}
			}
		} catch (LogoutInterrupt | IOException e) {
			io.println("Errore di I/O");
			return false;
		}
	}
	private void showMyOfferts() throws IOException {
		InsertionsDB db = new InsertionsDB();
		Insertions insertions = db.load();
		HashMap<String,Insertion> list = insertions.getInsertions();
		if(list.size()>0) {
			io.println("-----------------------------------------------------");
			io.println("Le tue offerte:");
			io.println("-----------------------------------------------------");
			for(Map.Entry<String, Insertion> entry : list.entrySet()) {
				if(entry.getValue().getUsername().equals(Account.getUserName())) {
					io.println(entry.getValue().toString());
					io.println("-----------------------------------------------------");
				}
			}
		}
		else
			io.println("Nessuna offerta trovata.");
		io.println("-----------------------------------------------------");
	}
	
	
}
