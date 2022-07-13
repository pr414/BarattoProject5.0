package menu.insertions.offerts;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ambient.IdManager;
import db.HierarchiesDB;
import db.InsertionsDB;
import hierarchies.Category;
import hierarchies.Hierarchy;
import insertions.Insertion;
import insertions.Insertions;
import insertions.offerts.OffertStatus;
import menu.LogoutInterrupt;
import menu.MenuBase;
import menu.io.MenuIO;

public class ShowOpenOffertsMenu extends MenuBase<Boolean> {

	public ShowOpenOffertsMenu(MenuIO io) {
		super(io);
	}
	
	@Override
	public Boolean start() throws LogoutInterrupt, IOException {
		showLeafCategories();
		return true;
	}
	private void showLeafCategories() throws IOException {
		List<Hierarchy> hierarchies = new HierarchiesDB().load().getHierarchies();
		while(true) {
			io.println("-----------------------------------------------------");
			io.println("Premi:");
			HashMap<Integer,Integer> symb_hierarchyIndex = new HashMap<Integer,Integer>();
			HashMap<Integer,Long> symb_categoryId = new HashMap<Integer,Long>();
			//i è l'indice da visualizzare
			//r è l'indice reale
			//h è l'indice della gerarchia
			int i = 1, h = 0;
			for(Hierarchy hierarchy : hierarchies) {
				for(Map.Entry<Integer, Category> entry : hierarchy.getCategories().entrySet()) {
					if(hierarchy.getChildrenSize(entry.getValue())==0) {//è foglia
						StringBuilder sb = new StringBuilder();
						sb.append(">").append(i)
							.append(" per visualizzare le offerte aperte, in scambio e chiuse di ")
							.append(entry.getValue().getName());
						io.println(sb.toString());
						symb_hierarchyIndex.put(i, h);
						symb_categoryId.put(i, entry.getValue().getId());
						i++;
					}
				}
				h++;
			}
			io.println("back> per interrompere l'operazione.");
			io.println("-----------------------------------------------------");
			String opt = io.readString();
			if(opt != null) {
				if(opt.equals("back"))
					return;
				try {
					int opt_int = Integer.parseInt(opt);
					if(opt_int <= symb_categoryId.size()) {//confronto il numero e successivamente riduco a index
						long category_id = symb_categoryId.get(opt_int);
						int hierarchy_index = symb_hierarchyIndex.get(opt_int);
						Hierarchy selh = hierarchies.get(hierarchy_index);
						Category selected = selh.getCategories()
								.get(IdManager.getCategoryShortId(category_id));
						if(showOpenOfferts(selected))
							continue;
						else
							return;
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

	private boolean showOpenOfferts(Category selected) throws IOException {
		InsertionsDB db = new InsertionsDB();
		Insertions insertions = db.load();
		HashMap<String,Insertion> map = insertions.getInsertions(OffertStatus.OPEN, selected);
		map.putAll(insertions.getInsertions(OffertStatus.TRADING, selected));
		map.putAll(insertions.getInsertions(OffertStatus.CLOSED, selected));
		if(map.size()>0) {
			io.println("-----------------------------------------------------");
			io.println("Offerte aperte, in scambio e chiuse della categoria " + selected.getName());
			io.println("-----------------------------------------------------");
			for(Map.Entry<String, Insertion> entry : map.entrySet())
				io.println(entry.getValue().toString());
			
		}
		else
			io.println("Nessuna offerta aperta trovata.");
		io.println("-----------------------------------------------------");
		if(yes_no_request("Vuoi tornare al menù principale?"))
			return false;
		return true;
	}
}
