package insertions.history;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import insertions.offerts.OffertStatus;

public class StatusHistory {
	@Expose
	private List<StatusChange> history;
	
	public StatusHistory() {
		this.history = new ArrayList<StatusChange>();
	}
	
	public void newChange(long insertion_id, OffertStatus status_old, OffertStatus status) {
		this.history.add(new StatusChange(insertion_id,status_old,status));
	}
}
