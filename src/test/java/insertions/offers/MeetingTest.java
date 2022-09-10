package insertions.offers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import account.User;
import account.UserTest;
import ambient.ConfigurationManager;
import db.Database;
import db.DatabaseException;
import hierarchies.Category;
import hierarchies.Field;
import insertions.EvaluatedField;
import insertions.Insertion;
import insertions.offerts.OffertStatus;

public class MeetingTest{
	
	@BeforeClass
	public static void do_before()
	{
		assertEquals("Errore nell'inizializzazione",
				initialize(), true);
	}
	
	@Test
	public void test() {
		Category category1 = generate_category(1);
		Category category2 = generate_category(2);
		
		//Creazione utenti fittizzi
		User user1 =  UserTest.generateRandomUser();
		User user2 = UserTest.generateRandomUser();
		
		//Creazione inserzioni
		//"Pubblica un'offerta"
		Insertion ins1 = generate_insertion(user1, category1);
		assertEquals("Doveva essere OPEN",
				ins1.getStatus(),OffertStatus.OPEN);
		Insertion ins2 = generate_insertion(user2, category2);
		assertEquals("Doveva essere OPEN",
				ins2.getStatus(),OffertStatus.OPEN);
		//Test ritiro
		try {
			ins2.retireOffert();
			assertEquals("Doveva essere RETIRED",
					ins2.getStatus(),OffertStatus.RETIRED);
			ins2.openOffert();
			assertEquals("Doveva essere OPEN",
					ins2.getStatus(),OffertStatus.OPEN);
			//1 sceglie 2 ins1 selector ins2 selected
			ins1.matchOffert(ins2);
			assertEquals("Ins1 doveva essere MATCHED",
					ins1.getStatus(),OffertStatus.MATCHED);
			assertEquals("Ins1 doveva essere SELECTED",
					ins2.getStatus(),OffertStatus.SELECTED);
			
			ins1.closeMatching();
			ins2.closeMatching();
			assertEquals("Ins1 doveva essere CLOSED",
					ins1.getStatus(),OffertStatus.CLOSED);
			assertEquals("Ins2 doveva essere CLOSED",
					ins2.getStatus(),OffertStatus.CLOSED);
		} catch (IOException e) {
			assertEquals("Errore generale I/O",true,false);
		}
	}

	private Category generate_category(int i) {
		Category cat = new Category();
		cat.setId(i);
		cat.setName("name"+i);
		HashMap<String, Field> fields = new HashMap<String, Field>();
		String s1 = "stato conservazione";
		String s2 = "descrizione libera";
		fields.put(s1, new Field(s1, true));
		fields.put(s2, new Field(s2, false));
		cat.setFields(fields);
		cat.setParent(null);
		return cat;
	}

	private Insertion generate_insertion(User user, Category category) {
		
		try {
			Insertion ins = new Insertion();
			ins.setId(UUID.randomUUID().toString());
			ins.setUsername(user.getUsername());
			ins.openOffert();
			assertEquals(ins.getStatus(), OffertStatus.OPEN);
			ins.setCategory_id(category.getId());
			ins.setCategory_reference(category);
			for(Entry<String, Field> field : category.getFields().entrySet()) {
				EvaluatedField temp_eval_field = new EvaluatedField();
				temp_eval_field.setFieldName(field.getValue().getName());
				temp_eval_field.setValue("acaso");
				ins.getEvaluated_fields().add(temp_eval_field);
			}
			return ins;
		} catch (IOException e) {}
		return null;
	}
	
	
	public static boolean initialize() {
		assertEquals("Errore nell'inizializzazione", initialize_all(), true);
		return true;
	}
	
	/**
	 * Inizializzo il necessario per la classe Account
	 */
	private static boolean initialize_all() {
		if(ConfigurationManager.newInstance()) {
			try {
				Database.initialize();
				return true;
			} catch (DatabaseException e) {}
		}
		return false;
	}
}
