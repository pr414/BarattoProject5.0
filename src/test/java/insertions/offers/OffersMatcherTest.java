package insertions.offers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;

import account.Account;
import account.User;
import account.UserTest;
import account.Users;
import ambient.ConfigurationManager;
import db.Database;
import db.DatabaseException;
import db.HierarchiesDB;
import db.InsertionsDB;
import db.UsersDB;
import hierarchies.Category;
import hierarchies.Field;
import hierarchies.Hierarchies;
import hierarchies.Hierarchy;
import hierarchies.Interval;
import insertions.EvaluatedField;
import insertions.Insertion;
import insertions.Insertions;
import insertions.offerts.OffertStatus;

public class OffersMatcherTest {
	
	@BeforeClass
	public static void do_before()
	{
		assertEquals("Errore nell'inizializzazione",
				initialize(), true);
	}
	
	@Test
	public void test() {		
		try {
			init_test();
		} catch (IOException ex) {
			assertEquals("IOException triggered", false, true);
		}
	}
	
	private void init_test() throws IOException {
		//Creo categoria
		Category category1 = generate_category(1);
		//Creo gerarchie
		Hierarchies hierarchies = new Hierarchies();
		Hierarchy hierarchy = new Hierarchy();
		hierarchy.setNewId();
		ArrayList<String> luoghi = new ArrayList<String>();
		luoghi.add("West Virginia");
		hierarchy.setLuoghi(luoghi);
		
		ArrayList<Interval> intervals = new ArrayList<Interval>();
		Interval int1 = new Interval();
		int1.setStartAt(LocalTime.of(18, 0, 0));
		int1.setEndAt(LocalTime.of(19, 0, 0));
		intervals.add(int1);
		Interval int2 = new Interval();
		int2.setStartAt(LocalTime.of(15, 0, 0));
		int2.setEndAt(LocalTime.of(16, 0, 0));
		intervals.add(int2);
		hierarchy.setIntervalli(intervals);
		
		ArrayList<String> giorni = new ArrayList<String>();
		giorni.add("lunedi");
		giorni.add("mercoledi");
		hierarchy.setGiorni(giorni);
		
		
		hierarchy.newCategory(category1);
		hierarchies.getHierarchies().add(hierarchy);
		new HierarchiesDB().save(hierarchies);
		
		//Creazione utenti fittizi
		User user1 = UserTest.generateRandomUser();
		User user2 = UserTest.generateRandomUser();
		Users users = new UsersDB().load();
		users.getUsers().add(user1);
		users.getUsers().add(user2);
		new UsersDB().save(users);
		Account.login(user2);
		
		//Creazione inserzioni
		//"Pubblica un'offerta"
		
		InsertionsDB insdb = new InsertionsDB();
		Insertion ins1 = generate_insertion(user1, category1);
		Insertion ins2 = generate_insertion(user2, category1);
		Insertions insertions = new Insertions();
		insertions.getInsertions().put(ins1.getId(), ins1);
		insertions.getInsertions().put(ins2.getId(), ins2);
		insdb.save(insertions);
		assertEquals("Le inserzioni non sono tutte e due OPEN", ins1.getStatus(), OffertStatus.OPEN);
		assertEquals("Le inserzioni non sono tutte e due OPEN", ins2.getStatus(), OffertStatus.OPEN);

		ins1.matchOffert(ins2);
		insdb.save(insertions);
		assertEquals("Le inserzioni non sono matchate", ins1.getStatus(), OffertStatus.MATCHED);
		assertEquals("Le inserzioni non sono matchate", ins2.getStatus(), OffertStatus.SELECTED);
		Insertions ins = insdb.load();
		String id1 = ins1.getId();
		String id2 = ins2.getId();
		
		
		HashMap<String, Insertion> map = ins.getInsertions();
		Insertion ins1_loaded = map.get(id1);
		Insertion ins2_loaded = map.get(id2);
		assertEquals("Le inserzioni non sono matchate", ins1_loaded.getStatus(), OffertStatus.MATCHED);
		assertEquals("Le inserzioni non sono matchate", ins2_loaded.getStatus(), OffertStatus.SELECTED);
		
		ins1.closeMatching();
		ins2.closeMatching();
		insdb.save(insertions);
		assertEquals("Le inserzioni non sono matchate", ins1.getStatus(), OffertStatus.CLOSED);
		assertEquals("Le inserzioni non sono matchate", ins2.getStatus(), OffertStatus.CLOSED);
		Insertions insB = insdb.load();
		String id1B = ins1.getId();
		String id2B = ins2.getId();
		
		
		HashMap<String, Insertion> mapB = insB.getInsertions();
		Insertion ins1_loadedB = mapB.get(id1B);
		Insertion ins2_loadedB = mapB.get(id2B);
		assertEquals("Le inserzioni non sono matchate", ins1_loadedB.getStatus(), OffertStatus.CLOSED);
		assertEquals("Le inserzioni non sono matchate", ins2_loadedB.getStatus(), OffertStatus.CLOSED);
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
		boolean ok = initialize_all();
		assertEquals("Errore nell'inizializzazione", ok, true);
		return ok;
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
