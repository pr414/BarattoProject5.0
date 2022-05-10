package insertions;

import com.google.gson.annotations.Expose;

import hierarchies.Field;

public class EvaluatedField {
	@Expose
	private String fieldName;
	@Expose
	private String value; //descrizione del campo
	
	public EvaluatedField() {}
	public EvaluatedField(Field f) {
		this.setFieldName(f.getName());
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
