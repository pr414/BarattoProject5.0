package hierarchies;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class Category {
	@Expose
	private long id;
	@Expose
	private String name;
	@Expose
	private String description;
	@Expose
	private HashMap<String, Field> fields;
	@Expose
	private Category parent;
	
	
	/*
	 * POSTCONDIZIONI:
	 * this.fields != null, this.children != null
	 */
	public Category() {
		this.setFields(new HashMap<String, Field>());
	}
	
	
	@Override
	public String toString() {
		return toString("");
	}
	/*
	 * PRECONDIZIONI:
	 * this.children != null
	 */
	public String toString(String tab) {
		StringBuilder sb = new StringBuilder()
			.append(tab)
			.append("Nome: ").append(this.getName())
			.append(System.lineSeparator())
			.append(tab)
			.append("Descrizione: ").append(this.getDescription())
			.append(System.lineSeparator())
			.append(tab)
			.append("Campi nativi: ")
			.append(System.lineSeparator());
		
		for(Map.Entry<String, Field> f : this.getFields().entrySet())
			sb.append(f.getValue().toString(tab)).append(System.lineSeparator());
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null)
			return false;
		if(other instanceof Category) {
			Category other_obj = (Category)other;
			if(other_obj.id == this.id && other_obj.name.equals(this.name))
				return true;
		}
		return false;
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public HashMap<String, Field> getFields() {
		return fields;
	}
	public void setFields(HashMap<String, Field> fields) {
		this.fields = fields;
	}
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
}
