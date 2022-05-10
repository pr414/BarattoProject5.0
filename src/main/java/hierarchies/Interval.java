package hierarchies;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.google.gson.annotations.Expose;

public class Interval {
	private static final String PATTERN = "HH:mm";
	
	@Expose
	private LocalTime startAt;
	@Expose
	private LocalTime endAt;
	
	
	//GETTER & SETTER STARTAT
	public LocalTime getStartAt() {
		return startAt;
	}
	/*
	 * PRECONDIZIONI:
	 * startAt != null
	 */
	public boolean setStartAt(String startAt) {
		try {
			return setStartAt(fromString(startAt));
		}catch(IllegalArgumentException e) {
			return false;
		}
	}
	public boolean setStartAt(LocalTime startAt) {
		if(!is_possible(startAt))
			return false;
		this.startAt = startAt;
		return true;
	}
	//---------------------------------------------
	
	
	//GETTER & SETTER ENDAT
	public LocalTime getEndAt() {
		return endAt;
	}
	/*
	 * PRECONDIZIONI:
	 * startAt != null
	 */
	public boolean setEndAt(String endAt) {
		try {
			return setEndAt(fromString(endAt));
		}catch(IllegalArgumentException e) {
			return false;
		}
	}
	/*
	 * PRECONDIZIONI:
	 * endAt > startAt
	 */
	public boolean setEndAt(LocalTime endAt) {
		if(!is_possible(endAt) && endAt.isAfter(this.startAt))
			return false;
		this.endAt = endAt;
		return true;
	}
	//---------------------------------------------
	
	//-------UTILS---------
	/*
	 * PRECONDIZIONI:
	 * date_str != null
	 */
	private LocalTime fromString(String date_str) throws IllegalArgumentException {
		try {
			LocalTime lt = LocalTime.parse(date_str, DateTimeFormatter.ofPattern(PATTERN));
			return lt;
		}catch(DateTimeParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	private String format(LocalTime t) {
		return t.format(DateTimeFormatter.ofPattern(PATTERN));
	}
	
	private boolean is_possible(LocalTime time) {
		if(time.getMinute() == 30 || time.getMinute() == 0)//half
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append(format(this.getStartAt()))
			.append('-')
			.append(format(this.getEndAt()))
		.toString();
	}
}
