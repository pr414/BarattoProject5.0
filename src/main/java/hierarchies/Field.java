package hierarchies;

import com.google.gson.annotations.Expose;

public class Field {
	@Expose
	private String name;
	@Expose
	private boolean mandatory;
	
	public Field() {}
	/*
	 * PRECONDIZIONI:
	 * name != null
	 */
	public Field(String name, boolean mandatory) {
		this.setName(name);
		this.setMandatory(mandatory);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Field)
			if(this.getName().equals(((Field)obj).getName()))
				return true;
		return false;
	}
	
	/*
	 * PRECONDIZIONI:
	 * tab != null
	 */
	public String toString(String tab) {
		StringBuilder sb = new StringBuilder();
		sb.append(tab).append("> ").append(this.getName()).append(" ");
		if(this.mandatory)
			sb.append("<obbligatorio>");
		return sb.toString();
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
