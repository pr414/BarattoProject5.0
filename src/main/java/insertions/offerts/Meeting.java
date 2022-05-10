package insertions.offerts;

import java.time.LocalDate;
import java.time.LocalTime;

import com.google.gson.annotations.Expose;

import insertions.Insertion;

public class Meeting {
	private static final boolean SELECTOR = false;
	private static final boolean ACCEPTOR = true;
	
	@Expose
	private String location;
	@Expose
	private LocalDate date;
	@Expose
	private LocalTime time;
	@Expose
	private String from_insertion_id;
	private Insertion from_insertion;
	@Expose
	private String to_insertion_id;// to selezionato da from
	private Insertion to_insertion;
	@Expose
	private boolean speaker;//0 from 1 to
	
	public void switchSpeaker() {
		this.speaker = !this.speaker;
	}
	public boolean canISpeak(String username) {
		boolean i_am_selector = from_insertion.getUsername().equals(username);
		if(i_am_selector)
			return this.selectorCanSpeak();
		else
			return this.acceptorCanSpeak();
	}
	public boolean selectorCanSpeak() {
		return speaker == SELECTOR;
	}
	public boolean acceptorCanSpeak() {
		return speaker == ACCEPTOR;
	}
	public void setSelectorAsSpeaker() {
		this.speaker = SELECTOR;
	}
	public void setAcceptorAsSpeaker() {
		this.speaker = ACCEPTOR;
	}
	public String getFrom_insertion_id() {
		return from_insertion_id;
	}
	public void setFrom_insertion_id(String from_insertion_id) {
		this.from_insertion_id = from_insertion_id;
	}
	public Insertion getFrom_insertion() {
		return from_insertion;
	}
	public void setFrom_insertion(Insertion from_insertion) {
		this.from_insertion = from_insertion;
	}
	public String getTo_insertion_id() {
		return to_insertion_id;
	}
	public void setTo_insertion_id(String to_insertion_id) {
		this.to_insertion_id = to_insertion_id;
	}
	public Insertion getTo_insertion() {
		return to_insertion;
	}
	public void setTo_insertion(Insertion to_insertion) {
		this.to_insertion = to_insertion;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append("Location: ")
				.append(this.getLocation())
				.append(System.lineSeparator())
				.append("Date: ")
				.append(this.getDate())
				.append(System.lineSeparator())
				.append("Time: ")
				.append(this.getTime())
				.toString();
	}
}