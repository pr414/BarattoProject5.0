package hierarchies;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import ambient.IdManager;

public class Hierarchies{
	
	@Expose
	private List<Hierarchy> hierarchies;
	
	public Hierarchies() {
		setHierarchies(new ArrayList<Hierarchy>());
	}
	
	public Category getCategoryFromId(long category_id) {
		for(Hierarchy h : hierarchies)
			if(IdManager.isMineCompetence(h.getId(), category_id))
				return h.getCategories().get(IdManager.getCategoryShortId(category_id));
		return null;
	}

	public List<Hierarchy> getHierarchies() {
		return hierarchies;
	}
	public void setHierarchies(List<Hierarchy> hierarchies) {
		this.hierarchies = hierarchies;
	}
}