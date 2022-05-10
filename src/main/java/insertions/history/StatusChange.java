package insertions.history;

import java.time.LocalDate;
import java.time.LocalTime;

import com.google.gson.annotations.Expose;

import insertions.offerts.OffertStatus;

public class StatusChange {
	@Expose
	private long insertion_id;
	@Expose
	private OffertStatus status_old;
	@Expose
	private OffertStatus status;
	@Expose
	private LocalDate when_date;
	@Expose
	private LocalTime when_time;
	
	public StatusChange() {}
	public StatusChange(long insertion_id,
			OffertStatus status_old, OffertStatus status) {
		this.setInsertion_id(insertion_id);
		this.setStatus(status);
		this.setStatus_old(status_old);
		this.setWhen_date(LocalDate.now());
		this.setWhen_time(LocalTime.now());
	}
	
	public long getInsertion_id() {
		return insertion_id;
	}
	public void setInsertion_id(long insertion_id) {
		this.insertion_id = insertion_id;
	}
	public OffertStatus getStatus_old() {
		return status_old;
	}
	public void setStatus_old(OffertStatus status_old) {
		this.status_old = status_old;
	}
	public OffertStatus getStatus() {
		return status;
	}
	public void setStatus(OffertStatus status) {
		this.status = status;
	}
	public LocalDate getWhen_date() {
		return when_date;
	}
	public void setWhen_date(LocalDate when_date) {
		this.when_date = when_date;
	}
	public LocalTime getWhen_time() {
		return when_time;
	}
	public void setWhen_time(LocalTime when_time) {
		this.when_time = when_time;
	}
}
