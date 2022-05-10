package menu.hierarchies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import db.HierarchiesDB;
import hierarchies.Hierarchies;
import hierarchies.Hierarchy;
import hierarchies.Interval;
import menu.LogoutInterrupt;
import menu.MenuBase;
import menu.io.MenuIO;

public class NewHierarchyMenu extends MenuBase<Boolean> {
	private List<String> already_used;
	private Hierarchy hierarchy;
	private Hierarchies hierarchies;
	
	public NewHierarchyMenu(MenuIO io) throws IOException {
		super(io);
		this.setAlready_used(new ArrayList<String>());
		this.setHierarchy(new Hierarchy());
		this.setHierarchiesInstance(new HierarchiesDB().load());
	}

	@Override
	public Boolean start() throws LogoutInterrupt {
		io.println("Inserimento di una nuova gerarchia.");
		io.println("Inserisci prima i parametri, poi crei la gerarchia.");
		menuAddHierarchyParameters();
		this.getHierarchy().setNewId();
		//avvio il menù delle categorie
		new CategoryManagerMenu(io, this, null).start();//null perchè creo la root prima
		//finito di inserire il menù delle categorie aggiungo la gerarchia alla lista
		this.getHierarchiesInstance().getHierarchies().add(this.getHierarchy());
		//salvo la gerarchia
		new HierarchiesDB().save(this.getHierarchiesInstance());
		io.println("Aggiunta con successo!");
		return true;
	}
	
	
	

	/*
	 * PRECONDIZIONI:
	 * current_hierarchy != null, current_hierarchy.luoghi != null,
	 * current_hierarchy.giorni != null, current_hierarchy.scadenze != null
	 */
	/**
	 * Menù per l'aggiunta dei parametri delle gerarchie (Piazza, etc..)
	 * @param current_hierarchy riferimento alla gerarchia corrente
	 */
	private void menuAddHierarchyParameters() {
		//piazza
		io.println("Inserisci la piazza:");
		String piazza = io.readString();
		this.getHierarchy().setPiazza(piazza);
		
		//luoghi
		io.println("Inserimento luoghi");
		while(true) {
			io.println("Inserisci il nome del luogo:");
			String luogo = io.readString();
			this.getHierarchy().getLuoghi().add(luogo);
			//chiedo se ne vuole inserire altri
			if(!yes_no_request("Vuoi inserire altri luoghi?"))
				break;
		}
		
		//giorni
		io.println("Inserimento giorni");
		while(true) {
			io.println("Inserisci il giorno:");
			String dow = io.readString();
			if(!Hierarchy.is_day_of_week(dow))
			{
				io.println("Non hai inserito un valido giorno della settimana.");
				continue;
			}
			this.getHierarchy().getGiorni().add(dow);
			
			//chiedo se ne vuole inserire altri
			if(!yes_no_request("Vuoi inserire altri giorni?"))
				break;
		}
		
		//intervalli
		setInterval();
		
		//scadenza
		while(true) {
			io.println("Inserisci una scadenza (in giorni):");
			try {
				int scad = Integer.parseInt(io.readString());
				this.getHierarchy().setScadenza(scad);
				break;
			}catch(NumberFormatException ex) {
				io.println("Non hai inserito un numero.");
				continue;
			}
		}
	}
	
	/*
	 * PRECONDIZIONI:
	 * current_hierarchy != null, current_hierarchy.intervalli != null,
	 * POSTCONDIZIONI:
	 * current_hierarchy.intervalli.len >= current_hierarchy.intervalli.len(precedente)
	 */
	/**
	 * Metodo per settare gli intervalli di una gerarchia
	 * @param current_hierarchy riferimento alla gerarchia corrente
	 */
	private void setInterval() {
		io.println("Inserimento intervalli:");
		while(true) {
			Interval interval = new Interval();
			while(true) {
				io.println("Orario valido ogni mezz'ora (p.e. 17:00 oppure 17:30, ma non 17:34)");
				io.println("Inserisci orario di inizio:");
				String startat = io.readString();
				if(!interval.setStartAt(startat))
				{
					io.println("Non hai rispettato i vincoli.");
					continue;
				}
				break;
			}
			while(true) {
				io.println("Orario valido ogni mezz'ora (p.e. 17:00 oppure 17:30, ma non 17:34)");
				io.println("Inserisci orario di fine:");
				String startat = io.readString();
				if(!interval.setEndAt(startat))
				{
					io.println("Non hai rispettato i vincoli.");
					continue;
				}
				break;
			}
			this.getHierarchy().getIntervalli().add(interval);
			if(!yes_no_request("Vuoi inserire altri intervalli?"))
				break;
		}
	}
	

	public List<String> getAlready_used() {
		return already_used;
	}
	public void setAlready_used(List<String> already_used) {
		this.already_used = already_used;
	}
	public Hierarchies getHierarchiesInstance() {
		return hierarchies;
	}
	public void setHierarchiesInstance(Hierarchies hierarchies) {
		this.hierarchies = hierarchies;
	}
	public Hierarchy getHierarchy() {
		return hierarchy;
	}
	public void setHierarchy(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}
}
