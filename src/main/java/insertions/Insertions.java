package insertions;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

import hierarchies.Category;
import insertions.offerts.OffertStatus;

public class Insertions {
	@Expose
	private HashMap<String,Insertion> insertions;
	
	public Insertions() {
		this.setInsertions(new HashMap<String,Insertion>());
	}
	
	public HashMap<String,Insertion> getInsertions() {
		return insertions;
	}
	public HashMap<String,Insertion> getInsertions(OffertStatus status, Category category){
		HashMap<String,Insertion> ins = new HashMap<String,Insertion>();
		for(Map.Entry<String,Insertion> entry : this.getInsertions().entrySet()) {
			Insertion i = entry.getValue();
			if(i.getCategory_id() == category.getId()) {
				if(status != null)
					if(!i.getStatus().equals(status))
						continue;
				ins.put(i.getId(),i);
			}	
		}
		return ins;
	}
	private void setInsertions(HashMap<String,Insertion> insertions) {
		this.insertions = insertions;
	}
}
