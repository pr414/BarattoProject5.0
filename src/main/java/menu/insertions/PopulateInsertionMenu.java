package menu.insertions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import account.Account;
import db.InsertionsDB;
import hierarchies.Category;
import hierarchies.Field;
import insertions.EvaluatedField;
import insertions.Insertion;
import menu.LogoutInterrupt;
import menu.MenuBase;
import menu.io.MenuIO;

public class PopulateInsertionMenu extends MenuBase<Insertion>{

	private Category category;
	private Insertion insertion;
	
	public PopulateInsertionMenu(MenuIO io, Category category) throws IOException {
		super(io);
		this.category = category;
		//setup insertion
		this.insertion = Insertion.newInsertion(getInsertionsFromDB());
		this.insertion.setCategory_reference(category);
		this.insertion.setUsername(Account.getUserName());
	}
	
	private HashMap<String,Insertion> getInsertionsFromDB() throws IOException{
		return new InsertionsDB().load().getInsertions();
	}
	
	/**
	 * Menù per popolare un parametro Field di una categoria
	 * @param f
	 */
	private void populateCategoryParameter(Field f){
		EvaluatedField ef = new EvaluatedField(f);
		if(!f.isMandatory()) {
			if(!yes_no_request("Vuoi compilare il campo "+ f.getName())) {
				this.insertion.getEvaluated_fields().add(ef);// lo aggiunge lo stesso vuoto
				io.println("Campo non compilato.");
				return;
			}
		}
		io.println("Compilazione del campo "+f.getName()+((f.isMandatory())?"(obbligatorio)":"")+":");
		String value = io.readString();
		ef.setValue(value);
		this.insertion.getEvaluated_fields().add(ef);
		io.println("Campo compilato con successo!");
	}

	@Override
	public Insertion start() throws LogoutInterrupt, IOException {
		//popolo i campi dell'inserzione
		io.println("Ora devi compilare i parametri relativi alla categoria selezionata.");
		for(Map.Entry<String, Field> entry : category.getFields().entrySet())
			populateCategoryParameter(entry.getValue());
		return this.insertion;
	}
}