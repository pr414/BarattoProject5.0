package menu.insertions.offerts;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

import account.Account;
import db.HierarchiesDB;
import db.InsertionsDB;
import db.InsertionsMatchingDB;
import hierarchies.Category;
import hierarchies.Hierarchies;
import hierarchies.Hierarchy;
import hierarchies.Interval;
import insertions.Insertion;
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
		Insertion from_i = meeting.getFrom_insertion();
		OffertStatus from_s = from_i.getStatus();
		Insertion to_i = meeting.getTo_insertion();
		OffertStatus to_s = to_i.getStatus();
		if(from_s.equals(OffertStatus.TRADING) &&
				to_s.equals(OffertStatus.TRADING)) {
			 //-----------CASO 1---------------------
			//può accettare o riproporre
			//**************in base al valore di speaker*******************
			sb.append("Per accettare o per proporre un accordo diverso dal precedente:");
			sb.append(System.lineSeparator());
			sb.append("Categoria: ");
			sb.append(from_i.getCategory_reference().getName());
			sb.append(System.lineSeparator());
			sb.append(meeting.toString());
		} else if (from_s.equals(OffertStatus.MATCHED) &&
				to_s.equals(OffertStatus.SELECTED)) {
			 //-----------CASO 2---------------------
			//può rispondere alla proposta per la prima volta
			sb.append("Per proporre un nuovo accordo a ");
			sb.append(meeting.getFrom_insertion().getUsername());
			sb.append(System.lineSeparator());
			sb.append("Categoria: ");
			sb.append(from_i.getCategory_reference().getName());
			sb.append(System.lineSeparator());
			sb.append(meeting.toString());
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

	private DayOfWeek[] get_dow_from_list(List<String> giorni) {
		DayOfWeek[] dow = new DayOfWeek[giorni.size()];
		for(int i = 0; i < dow.length; i++)
			dow[i] = get_dow_from_string(giorni.get(i));
		return dow;
	}
	private DayOfWeek get_dow_from_string(String giorno) {
		switch(giorno) {
			case "lunedì":
				return DayOfWeek.MONDAY;
			case "martedì":
				return DayOfWeek.TUESDAY;
			case "mercoledì":
				return DayOfWeek.WEDNESDAY;
			case "giovedì":
				return DayOfWeek.THURSDAY;
			case "venerdì":
				return DayOfWeek.FRIDAY;
			case "sabato":
				return DayOfWeek.SATURDAY;
			case "domenica":
				return DayOfWeek.SUNDAY;
		}
		return null;
	}
	
	/**
	 * Nuova offerta oppure riproposta
	 * @param my_insertion
	 * @throws IOException 
	 */
	private void newMeetingOffer(Meeting meeting) throws IOException {
		Category cat = meeting.getFrom_insertion().getCategory_reference();
		HierarchiesDB hierarchiesdb = new HierarchiesDB();
		Hierarchies hierarchies_cls = hierarchiesdb.load();
		Hierarchy hierarchy = hierarchies_cls.getHierarchy(cat.getId());
		io.println("Nuova proposta");
		//private String location;
		if(meeting.getLocation() != null) {
			if(yes_no_request("Vuoi modificare il luogo?")) {
				while(true) {
					io.println("Inserisci un luogo tra i seguenti:");
					StringBuilder sb_luogo = new StringBuilder();
					List<String> luoghi = hierarchy.getLuoghi();
					for(int i = 0; i < luoghi.size(); i++) {
						sb_luogo.append(luoghi.get(i));
						if(i+1 < luoghi.size())
							sb_luogo.append(", ");
					}
					io.println(sb_luogo.toString());
					String location = io.readString();
					if(luoghi.contains(location)) {
						meeting.setLocation(location);
						break;
					}else {
						io.println("Hai inserito un luogo non valido.");
					}
				}
			}
		}else {
			while(true) {
				io.println("Inserisci un luogo tra i seguenti:");
				StringBuilder sb_luogo = new StringBuilder();
				List<String> luoghi = hierarchy.getLuoghi();
				for(int i = 0; i < luoghi.size(); i++) {
					sb_luogo.append(luoghi.get(i));
					if(i+1 < luoghi.size())
						sb_luogo.append(", ");
				}
				io.println(sb_luogo.toString());
				String location = io.readString();
				if(luoghi.contains(location)) {
					meeting.setLocation(location);
					break;
				}else {
					io.println("Hai inserito un luogo non valido.");
				}
			}
		}
		//private LocalDate date;
		while(true) {
			if(meeting.getDate() != null) {
				if(yes_no_request("Vuoi modificare la data?")) {
					List<String> giorni = hierarchy.getGiorni();
					DayOfWeek[] possible_days = get_dow_from_list(giorni);
					while(true) {
						io.println("Inserisci la data ("+APNUtils.DATE_FORMAT+")");
						io.println("Deve corrispondere a uno dei seguenti giorni:");
						StringBuilder sb_days = new StringBuilder();
						for(int i = 0; i < giorni.size(); i++) {
							sb_days.append(giorni.get(i));
							if(i+1<giorni.size())
								sb_days.append(", ");
						}
						io.println(sb_days.toString());
						String date = io.readString();
						LocalDate ldate = APNUtils.getDateFromString(date);
						if(ldate==null) {
							io.println("Non hai rispettato i vincoli "+ APNUtils.DATE_FORMAT);
							continue;
						}
						boolean correct = false;
						for(int i = 0; i < giorni.size(); i++) {
							if(possible_days[i] == ldate.getDayOfWeek()) {
								meeting.setDate(ldate);
								correct = true;
								break;
							}
						}
						if(correct)
							break;
						io.println("Non hai inserito una data corretta.");
					}
				}
			}else {
				List<String> giorni = hierarchy.getGiorni();
				DayOfWeek[] possible_days = get_dow_from_list(giorni);
				while(true) {
					io.println("Inserisci la data ("+APNUtils.DATE_FORMAT+")");
					io.println("Deve corrispondere a uno dei seguenti giorni:");
					StringBuilder sb_days = new StringBuilder();
					for(int i = 0; i < giorni.size(); i++) {
						sb_days.append(giorni.get(i));
						if(i+1<giorni.size())
							sb_days.append(", ");
					}
					io.println(sb_days.toString());
					String date = io.readString();
					LocalDate ldate = APNUtils.getDateFromString(date);
					if(ldate==null) {
						io.println("Non hai rispettato i vincoli "+ APNUtils.DATE_FORMAT);
						continue;
					}
					boolean correct = false;
					for(int i = 0; i < giorni.size(); i++) {
						if(possible_days[i] == ldate.getDayOfWeek()) {
							meeting.setDate(ldate);
							correct = true;
							break;
						}
					}
					if(correct)
						break;
					io.println("Non hai inserito una data corretta.");
				}
			}
			break;
		}
		//ora
		while(true) {
			if(meeting.getTime() != null) {
				if(yes_no_request("Vuoi modificare l'ora?")) {
					while(true) {
						io.println("Inserisci l'ora ("+APNUtils.TIME_FORMAT+") interna alle seguenti fasce:");
						StringBuilder sb_time = new StringBuilder();
						List<Interval> intervalli = hierarchy.getIntervalli();
						for(int i = 0; i < intervalli.size(); i++) {
							Interval interval = intervalli.get(i);
							sb_time.append("[");
							sb_time.append(interval.toString());
							sb_time.append("]");
							if(i+1<intervalli.size())
								sb_time.append(", ");
						}
						io.println(sb_time.toString());
						String time = io.readString();
						LocalTime ltime = APNUtils.getTimeFromString(time);
						if(ltime==null) {
							io.println("Non hai rispettato i vincoli "+ APNUtils.TIME_FORMAT);
							continue;
						}
						boolean ok = false;
						//Controllo validità
						for(Interval intervallo : intervalli) {
							if(ltime.isAfter(intervallo.getStartAt()) &&
								ltime.isBefore(intervallo.getEndAt())) {
								meeting.setTime(ltime);
								ok = true;
								break;
							}
						}
						if(ok)
							break;
						io.println("Hai inserito un orario non permesso.");
					}
				}
			}else {
				while(true) {
					io.println("Inserisci l'ora ("+APNUtils.TIME_FORMAT+") interna alle seguenti fasce:");
					StringBuilder sb_time = new StringBuilder();
					List<Interval> intervalli = hierarchy.getIntervalli();
					for(int i = 0; i < intervalli.size(); i++) {
						Interval interval = intervalli.get(i);
						sb_time.append("[");
						sb_time.append(interval.toString());
						sb_time.append("]");
						if(i+1<intervalli.size())
							sb_time.append(", ");
					}
					io.println(sb_time.toString());
					String time = io.readString();
					LocalTime ltime = APNUtils.getTimeFromString(time);
					if(ltime==null) {
						io.println("Non hai rispettato i vincoli "+ APNUtils.TIME_FORMAT);
						continue;
					}
					boolean ok = false;
					//Controllo validità
					for(Interval intervallo : intervalli) {
						if(ltime.isAfter(intervallo.getStartAt()) &&
							ltime.isBefore(intervallo.getEndAt())) {
							meeting.setTime(ltime);
							ok = true;
							break;
						}
					}
					if(ok)
						break;
					io.println("Hai inserito un orario non permesso.");
				}
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
