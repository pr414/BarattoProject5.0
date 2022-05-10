package menu.insertions;

import java.io.IOException;
import java.util.List;

import db.HierarchiesDB;
import db.InsertionsDB;
import hierarchies.Category;
import hierarchies.Hierarchies;
import hierarchies.Hierarchy;
import insertions.Insertion;
import insertions.Insertions;
import menu.LogoutInterrupt;
import menu.MenuBase;
import menu.io.MenuIO;

public class NewInsertionsMenu extends MenuBase<Boolean> {
	//variabili di contesto (attributi)
	private Hierarchy hierarchy_reference;
	private Category parent;
	
	public NewInsertionsMenu(MenuIO io) {
		super(io);
	}

	@Override
	public Boolean start() throws LogoutInterrupt, IOException {
		io.println("Operazioni per la pubblicazione di un nuovo articolo.");
		//inizio selezione della gerarchia.
		if(!menuSetHierarchyAndRoot())
			return true;//interrompe l'inserimento
		boolean cont = true;
		while(cont)
			cont = menuShowPossibleNestedCategories();
		//fine selezione della gerarchia.
		//inizio inserimento parametri
		Insertion created_insertion = new PopulateInsertionMenu(io, this.parent).start();
		//salvo la nuova inserzione
		InsertionsDB db = new InsertionsDB();
		Insertions ins_db = db.load();
		ins_db.getInsertions().put(created_insertion.getId(), created_insertion);
		if(!db.save(ins_db)) {
			io.println("Errore di I/O");
			return false;
		}else
			io.println("Inserzione pubblicata con successo!");
		return true;
	}
	
	/**
	 * Parte del menù volta a selezionare la gerarchia e la root.
	 * Servirà per tutto il ciclo di vita dell'InsertionsManagerMenu.
	 * @throws IOException
	 * @return true per continuare, false per interrompere l'operazione
	 */
	private boolean menuSetHierarchyAndRoot() throws IOException {
		io.println("Scegli la categoria di root che ti interessa:");
		Hierarchy h_selected = menuSelectOptionFromList(
			getHierarchies(),
			(h)-> h.getRoot().getName(),
			"per interrompere l'inserimento"
		);
		if(h_selected==null)
		{
			io.println("Hai annullato l'inserimento di una nuova inserzione.");
			return false;
		}
		this.hierarchy_reference = h_selected;
		this.parent = h_selected.getRoot();
		return true;
	}

	/**
	 * Parte del menù diviso in due parti:
	 * Se la categoria corrente è foglia allora esegue il menù specifico per la categoria foglia
	 * (seleziona o torna alla categoria precedente); se invece la categoria corrente non dovesse
	 * essere foglia, allora presenta le categorie possibili nel contesto corrente e permette al-
	 * l'utente di selezionarne una ricominciando il loop.
	 * @return true -> continua, false si blocca
	 * @throws IOException 
	 */
	private boolean menuShowPossibleNestedCategories() throws IOException {
		if(this.hierarchy_reference.getChildrenSize(this.parent)==0) {
			/* La categoria selezionata è una categoria foglia (non ha figli).
			 * E' infatti possibile selezionarla. */
			return menu_category_foglia();//se false -> ha selezionato la categoria
			//se true --> ha settato come parent il padre del parent (è tornato indietro). 
		}
		Category selected = menuSelectOptionFromList(//backable
			this.hierarchy_reference.getChildren(parent),
			(c)-> c.getName(),
			"per tornare alla categoria genitrice"
		);
		if(selected!=null)
			this.parent = selected;
		else
			backToParentCategory();
		return true;//continua il loop
	}
	
	/**
	 * Menù della categoria foglia. Seleziona la categoria corrente
	 * oppure torna alla categoria precedente.
	 * @return true per continuare il menù
	 * @throws IOException 
	 */
	private boolean menu_category_foglia() throws IOException {
		while(true) {
			io.println("Premi:");
			io.println("1) per selezionare la categoria corrente ("+this.parent.getName()+")");
			io.println("back) per tornare alla categoria padre");
			String opt = io.readString();
			if(opt.equals("1"))
				return false;
			else if(opt.equals("back")) {
				backToParentCategory();
				return true;
			} else
				io.println("Comando non riconosciuto");
		}
		
	}
	
	private void backToParentCategory() throws IOException {
		this.parent = this.parent.getParent();//torna al padre precedente
		if(this.parent == null)//se è root
			menuSetHierarchyAndRoot();//torno alla selezione delle categorie di root
	}
	
	/**
	 * Carica le gerarchie da json.
	 * @return
	 * @throws IOException
	 */
	private List<Hierarchy> getHierarchies() throws IOException {
		Hierarchies h = new HierarchiesDB().load();
		return h.getHierarchies();
	}
}
