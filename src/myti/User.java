package myti;

import java.util.ArrayList;

public class User {
	private String id;
	private String name;
	private String type;
	private String email;
	private MyTiCard card;

	private ArrayList<Journey> journeyList = new ArrayList<>();
	private ArrayList<TravelPass> ticketList = new ArrayList<>();

	private int travelCount = 0;

	public User(String id, String type, String name, String email) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.email = email;
		card = new MyTiCard();
	}

	public MyTiCard getCard() {
		return this.card;
	}

	@Override
	public String toString() {
		return id + ", " + name;
	}

	public String getID() {
		return this.id;
	}

	public String getType() {
		return this.type;
	}

	public ArrayList<TravelPass> getTList() {
		return this.ticketList;
	}

	public void addTicket(TravelPass t) {
		this.ticketList.add(t);
		setTravelCount(getTravelCount() + 1);
	}

	public void removeTicket(TravelPass t) {
		this.ticketList.remove(t);
	}

	public ArrayList<Journey> getJList() {
		return this.journeyList;
	}

	public void addJourney(Journey j) {
		this.journeyList.add(j);
	}

	public Journey askForJourney(Station startStation, Station endStation, String day, int startTime, int endTime) {
		Journey journey = new Journey(startStation, endStation, day, startTime, endTime);
		return journey;
	}

	public void printJourneys() {
		for (Journey journey : getJourneyList()) {
			System.out.println(journey.getDescription());
		}
	}

	public void printTickets() {
		for (TravelPass t : ticketList) {
			System.out.println(t.getDescription());
		}
	}

	public int getTravelCount() {
		return travelCount;
	}

	public void setTravelCount(int travelCount) {
		this.travelCount = travelCount;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public ArrayList<Journey> getJourneyList() {
		return journeyList;
	}

}
