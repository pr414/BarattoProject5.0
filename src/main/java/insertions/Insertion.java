package insertions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.annotations.Expose;

import db.InsertionsMatchingDB;
import db.StatusHistoryDB;
import hierarchies.Category;
import insertions.history.StatusHistory;
import insertions.offerts.InsertionsMatching;
import insertions.offerts.Meeting;
import insertions.offerts.OffertStatus;

public class Insertion {
	@Expose
	private String id;
	@Expose
	private String username;
	@Expose
	private long category_id;
	private Category category_reference;
	@Expose
	private List<EvaluatedField> evaluated_fields;
	@Expose
	private OffertStatus status;
	
	public Insertion() throws IOException {
		this.evaluated_fields = new ArrayList<EvaluatedField>();
	}
	
	public void openOffert() throws IOException {
		this.setStatus(OffertStatus.OPEN);
	}
	public void retireOffert() throws IOException {
		this.setStatus(OffertStatus.RETIRED);
	}
	
	public void matchOffert(Insertion selected) throws IOException {
		/*
		 * Prima cosa aggiungo I meeting settando unicamente from e to id
		 * delle inserzioni.
		 */
		InsertionsMatchingDB db_match = new InsertionsMatchingDB();
		InsertionsMatching im = db_match.load();
		Meeting meeting = new Meeting();
		meeting.setFrom_insertion_id(this.getId());
		meeting.setTo_insertion_id(selected.getId());
		//l'accettore dovrà dire le condizioni di scambio
		meeting.setAcceptorAsSpeaker();
		im.getMeeting().add(meeting);
		db_match.save(im);//salvo tutto
		
		/*
		 * Ora setto i nuovi stati delle due inserzioni.
		 * NB: da salvare necessariamente. Delego il compito
		 * a chi ha chiamato questo metodo (metchOffert).
		 */
		this.setStatus(OffertStatus.MATCHED);
		selected.setStatus(OffertStatus.SELECTED);
	}
	

	public void closeMatching() throws IOException {
		this.setStatus(OffertStatus.CLOSED);
	}
	
	public void in_trading() throws IOException {
		this.setStatus(OffertStatus.TRADING);
	}
	
	
	
	public OffertStatus getStatus() {
		return status;
	}
	public void setStatus(OffertStatus newstatus) throws IOException {
		if(this.status != null)
			if(this.status.equals(newstatus))
				return;
		StatusHistoryDB db = new StatusHistoryDB();
		StatusHistory history = db.load();
		history.newChange(this.getCategory_id(), this.status, newstatus);
		db.save(history);
		this.status = newstatus;
	}
	public void setCategory_id(long category_id) {
		this.category_id = category_id;
	}
	public List<EvaluatedField> getEvaluated_fields() {
		return evaluated_fields;
	}
	public void setEvaluated_fields(List<EvaluatedField> evaluated_fields) {
		this.evaluated_fields = evaluated_fields;
	}
	public Category getCategory_reference() {
		return category_reference;
	}
	public void setCategory_reference(Category category_reference) {
		this.category_reference = category_reference;
		this.category_id = category_reference.getId();
	}
	public long getCategory_id() {
		return this.category_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Offerta categoria ")
		.append(this.category_reference.getName())
		.append(System.lineSeparator())
		.append("Status: ").append(this.getStatus().name())
		.append(System.lineSeparator());
		for(int i = 0; i < this.evaluated_fields.size(); i++) {
			toStringField(sb, this.evaluated_fields.get(i));
			if(i+1 < this.evaluated_fields.size())
				sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
	
	private void toStringField(StringBuilder sb, EvaluatedField field) {
		sb.append("> ").append(field.getFieldName())
		.append(" = ");
		if(field.getValue() == null)
			sb.append("Nessun valore.");
		else
			sb.append(field.getValue());
	}

	public static Insertion newInsertion(HashMap<String,Insertion> insertions) throws IOException {
		while(true) {
			String id = UUID.randomUUID().toString();
			for(Map.Entry<String, Insertion> entry : insertions.entrySet()){
				if(entry.getValue().getId().equals(id))
					continue;
			}//id univoco
			Insertion insertion = new Insertion();
			insertion.setId(id);
			insertion.openOffert();
			return insertion;
		}
	}
}
