package menu.insertions.offerts;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import account.Account;
import db.InsertionsDB;
import db.InsertionsMatchingDB;
import insertions.Insertions;
import insertions.offerts.InsertionsMatching;
import insertions.offerts.Meeting;
import insertions.offerts.OffertStatus;
import menu.LogoutInterrupt;
import menu.MenuBase;
import menu.io.MenuIO;
import utils.APNUtils;

public class OffertsManagementMenu extends MenuBase<Boolean> {

	public OffertsManagementMenu(MenuIO io) {
		super(io);
	}

	/**
	 * Stampa a schermo le differenti opzioni seguite dal meeting toString
	 * @param meeting
	 * @return
	 */
	private String meetingAndOptionsToString(Meeting meeting) {
		StringBuilder sb = new StringBuilder();
		OffertStatus from_s = meeting.getFrom_insertion().getStatus();
		OffertStatus to_s = meeting.getTo_insertion().getStatus();
		if(from_s.equals(OffertStatus.TRADING) &&
				to_s.equals(OffertStatus.TRADING)) {
			 //-----------CASO 1---------------------
			//può accettare o riproporre
			//**************in base al valore di speaker*******************
			sb.append("per accettare o per proporre un accordo diverso dal precedente:");
			sb.append(meeting.toString());
		} else if (from_s.equals(OffertStatus.MATCHED) &&
				to_s.equals(OffertStatus.SELECTED)) {
			 //-----------CASO 2---------------------
			//può rispondere alla proposta per la prima volta
			sb.append("per proporre un nuovo accordo da parte di ");
			sb.append(meeting.getFrom_insertion().getUsername());
		}
		sb.append(System.lineSeparator());
		return sb.toString();
	}
	
	@Override
	public Boolean start() throws LogoutInterrupt, IOException {
		//prendo da db i matching (già associati con le inserzioni).
		InsertionsMatchingDB db_matching = new InsertionsMatchingDB();
		InsertionsMatching ins_match = db_matching.load();
		//da iterare
		List<Meeting> meeting_list = ins_match.getMeeting();
		Meeting selected = menuSelectOptionFromList(
			meeting_list,
			(meeting)-> meetingAndOptionsToString(meeting),
			(meeting)->{
				String to_user = meeting.getTo_insertion().getUsername();
				String from_user = meeting.getFrom_insertion().getUsername();
				OffertStatus status =  meeting.getFrom_insertion().getStatus();
				String current_user = Account.getUserName();
				return (to_user.equals(current_user) || from_user.equals(current_user))
							&& meeting.canISpeak(Account.getUserName())
							&& !status.equals(OffertStatus.CLOSED);
			},
			"Premi:",
			"per tornare indietro"
		);
		if(selected == null) //back
			return true;
		OffertStatus from_s = selected.getFrom_insertion().getStatus();
		OffertStatus to_s = selected.getTo_insertion().getStatus();
		if(from_s.equals(OffertStatus.MATCHED) &&
				to_s.equals(OffertStatus.SELECTED)) {
			 //-----------CASO 1---------------------
			//può rispondere alla proposta per la prima volta
			//proponendo una nuova (la prima) offerta
			newMeetingOffer(selected);
			db_matching.save(ins_match);
			io.println("Proposto con successo!");
		}else if(from_s.equals(OffertStatus.TRADING) &&
				to_s.equals(OffertStatus.TRADING)) {
			 //-----------CASO 2---------------------
			//può accettare o riproporre
			if(yes_no_request("Vuoi accettare?")) {
				//Se sì accetta l'inserzione
				acceptInsertion(selected);
				db_matching.save(ins_match);
				io.println("Accettato con successo!");
			}
			else {
				//altrimenti propone una nuova offerta
				newMeetingOffer(selected);
				db_matching.save(ins_match);
				io.println("Riproposto con successo!");
			}
		}
		return true;
	}

	/**
	 * Metodo per accettare un'offerta.
	 * @param my_insertion
	 * @throws IOException
	 */
	private void acceptInsertion(Meeting meeting) throws IOException {
		//Non devo interagire nel database delle offerte.
		//carico le inserzioni
		InsertionsDB db = new InsertionsDB();
		Insertions insertions = db.load();
		//chiudo inserzione from
		String from_id = meeting.getFrom_insertion_id();
		insertions.getInsertions().get(from_id).closeMatching();
		//chiudo inserzione to
		String to_id = meeting.getTo_insertion_id();
		insertions.getInsertions().get(to_id).closeMatching();
		db.save(insertions);
		io.println("Accettato correttamente!");
	}
	
	
	/**
	 * Nuova offerta oppure riproposta
	 * @param my_insertion
	 * @throws IOException 
	 */
	private void newMeetingOffer(Meeting meeting) throws IOException {
		io.println("Nuova proposta");
		//private String location;
		if(meeting.getLocation() != null) {
			if(yes_no_request("Vuoi modificare il luogo?")) {
				io.println("Inserisci luogo:");
				String location = io.readString();
				meeting.setLocation(location);
			}
		}else {
			io.println("Inserisci luogo:");
			String location = io.readString();
			meeting.setLocation(location);
		}
		//private LocalDate date;
		while(true) {
			if(meeting.getLocation() != null) {
				if(yes_no_request("Vuoi modificare la data?")) {
					io.println("Inserisci la data:("+APNUtils.DATE_FORMAT+")");
					String date = io.readString();
					LocalDate ldate = APNUtils.getDateFromString(date);
					if(ldate==null) {
						io.println("Non hai rispettato i vincoli "+ APNUtils.DATE_FORMAT);
						continue;
					}
					meeting.setDate(ldate);
				}
			}else {
				io.println("Inserisci la data:("+APNUtils.DATE_FORMAT+")");
				String date = io.readString();
				LocalDate ldate = APNUtils.getDateFromString(date);
				if(ldate==null) {
					io.println("Non hai rispettato i vincoli "+ APNUtils.DATE_FORMAT);
					continue;
				}
				meeting.setDate(ldate);
			}
			break;
		}
		//ora
		while(true) {
			if(meeting.getLocation() != null) {
				if(yes_no_request("Vuoi modificare l'ora?")) {
					io.println("Inserisci l'ora:("+APNUtils.TIME_FORMAT+")");
					String time = io.readString();
					LocalTime ltime = APNUtils.getTimeFromString(time);
					if(ltime==null) {
						io.println("Non hai rispettato i vincoli "+ APNUtils.TIME_FORMAT);
						continue;
					}
					meeting.setTime(ltime);
				}
			}else {
				io.println("Inserisci l'ora:("+APNUtils.TIME_FORMAT+")");
				String time = io.readString();
				LocalTime ltime = APNUtils.getTimeFromString(time);
				if(ltime==null) {
					io.println("Non hai rispettato i vincoli "+APNUtils.TIME_FORMAT);
					continue;
				}
				meeting.setTime(ltime);
			}
			break;
		}
		meeting.switchSpeaker();
		
		//salvo gli stati delle inserzioni
		InsertionsDB db = new InsertionsDB();
		Insertions insertions = db.load();
		//chiudo inserzione from
		String from_id = meeting.getFrom_insertion_id();
		insertions.getInsertions().get(from_id).in_trading();
		//chiudo inserzione to
		String to_id = meeting.getTo_insertion_id();
		insertions.getInsertions().get(to_id).in_trading();
		db.save(insertions);
	}
}
