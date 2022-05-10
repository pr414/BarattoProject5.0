package insertions.offerts;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class InsertionsMatching {
	@Expose
	private List<Meeting> meeting;
	
	public InsertionsMatching() {
		this.setMeeting(new ArrayList<Meeting>());
	}

	public List<Meeting> getMeeting() {
		return meeting;
	}
	public void setMeeting(List<Meeting> meeting) {
		this.meeting = meeting;
	}
}
