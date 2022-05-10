package ambient;

import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class IdManager {
	public static final int ROOT_ID = 1;
	private static final long SLLL = 32l;
	private static final long MASK = ((long)Integer.MAX_VALUE) << SLLL;
	
	@Expose
	private int max_hierarchy;
	
	@Expose
	private HashMap<Integer,Integer> map;
	//1° id hierarchy - 2° category dell'hierarchy
	
	public IdManager() {
		this.map = new HashMap<Integer,Integer>();
	}

	public long nextHierarchyId() {
		int max = ++max_hierarchy;
		long sll = ((long)max) << SLLL;
		map.put(max, 0);
		return sll;
	}
	
	public long nextCategoryId(long id) {
		int hierarchy_id = (int)(id >> SLLL);
		int newa = map.get(hierarchy_id);
		int max = newa + 1;
		map.put(hierarchy_id, max);
		return ((long)max)|id;
	}
	
	/**
	 * Restituisce l'istanza IdManager al primo avvio ( senza id ).
	 * @return
	 */
	static IdManager initialIdManager() {
		IdManager idmanag = new IdManager();
		idmanag.max_hierarchy = 0;
		idmanag.map = new HashMap<Integer,Integer>();
		return idmanag;
	}
	
	
	public static final int getCategoryShortId(long id) {
		//prende solo i primi 32 bit
		return (int)id;
	}
	public static final int getHierarchyShortId(long id) {
		//sposta di 32 bit e prende il valore tagliando gli ultimi 32 da sx
		return (int)(id >> SLLL);
	}
	
	public static boolean isMineCompetence(long hierachy_id, long category_id)
	{
		return hierachy_id == (category_id & MASK);
		/*
		 * Esempi a 8 bit 4 gerarchia 4 categoria.
		 * 
		 * Mask 8 bit è valore massimo 4 bit [1111]
		 * 				convertito in long [0000 1111] (+4 bit in questo caso) 
		 * 				e shiftato (shift left logical) di 4 posizioni
		 * Mask 8 bit  : 1111 0000
		 * ----------------------------------------
		 * Esempio 1)
		 * Gerarchia 1 : 0010 0000
		 * 				   2    -
		 * Category  1 : 0011 0010
		 * 				   3    2
		 * 
		 * masked_category_id = category and mask
		 * 		0011 0010
		 * AND	1111 0000
		 * =	0011 0000
		 * return 0011 0000 == 0010 0000 [false]
		 * ----------------------------------------
		 * Esempio 2)
		 * Gerarchia 2 : 1001 0000
		 * 				   9    -
		 * Category  2 : 1001 1010
		 * 				   9    10
		 * 
		 * masked_category_id = category and mask
		 * 		1001 1010
		 * AND	1111 0000
		 * =	1001 0000
		 * return 1001 0000 == 1001 0000 [true]
		 */
	}
}
