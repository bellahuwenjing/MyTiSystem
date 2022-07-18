package fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import myti.Journey;
import myti.Station;
import myti.TravelPass;
import myti.User;

public class ReadFile {
	public static HashMap<String, User> userList = new HashMap<String, User>();
	public static ObservableList<User> listViewData = FXCollections.observableArrayList();
	public static ArrayList<Journey> journeyList = new ArrayList<>();
	public static ArrayList<Station> stationList = new ArrayList<>();

	public static ArrayList<String> readJourneys(String filename) throws FileNotFoundException {
		ArrayList<String> jList = new ArrayList<>();
		Scanner scanIn = new Scanner(new File(filename));
		while (scanIn.hasNextLine()) {
			String s = scanIn.nextLine();
			if (s.length() >= 9 && s.substring(0, 9).equals("#journeys")) {
//				System.out.println("Reading data for journeys");
				journeyList.clear();
				boolean startReading = true;
				while (startReading && scanIn.hasNextLine()) {
					String currentLine = scanIn.nextLine();
					if (currentLine.length() == 0 || currentLine.substring(0, 1).equals("#")) {
						startReading = false;
					} else {
						String[] splitStr = currentLine.split(":");
						String id = splitStr[0];
						String day = splitStr[1];
						String fromSt = splitStr[2];
						int startTime = Integer.parseInt(splitStr[3]);
						String toSt = splitStr[4];
						int endTime = Integer.parseInt(splitStr[5]);
						String journey = "" + id + " travelled on " + day + " from " + fromSt + " at " + startTime
								+ " to " + toSt + " at " + endTime + ".\n";
						jList.add(journey);
						User currentUser = userList.get(id);
						if (currentUser != null) {
							Station startSt = findStation(fromSt);
							Station endSt = findStation(toSt);
							Journey j = currentUser.askForJourney(startSt, endSt, day, startTime, endTime);
							currentUser.addJourney(j);
							journeyList.add(j);// same as currentUser.journeyList?
						}
					}

				}
			}
		}
//TODO SORTING BY USER ID		jList.sort(null);
		jList.sort(Comparator.naturalOrder());
		return jList;
	}

	public static ArrayList<Station> readStations() {
		stationList.clear();
		stationList.add(new Station("Central", 1));
		stationList.add(new Station("Flagstaff", 1));
		stationList.add(new Station("Richmond", 1));
		stationList.add(new Station("Preston", 1));
		stationList.add(new Station("Epping", 2));
		stationList.add(new Station("Mernda", 2));
		return stationList;
	}

	public static Station findStation(String name) {
		if (stationList.isEmpty()) {
			readStations();
		}
		for (Station station : stationList) {
			if (station.getStationName().equals(name)) {
				return station;
			}
		}
		return null;
	}

	public static HashMap<String, User> readUsers(String filename) throws FileNotFoundException {
		userList.clear(); // TODO how to avoid doubling up????
//		System.out.println(userList.isEmpty());
		Scanner scanIn = new Scanner(new File(filename));
		String[] splitStr = null;
		while (scanIn.hasNextLine()) {
			String s = scanIn.nextLine();
			if (s.substring(0, 6).equals("#users")) {
				System.out.println("Reading user data");
				boolean startReading = true;
				while (startReading && scanIn.hasNextLine()) {
					s = scanIn.nextLine();
					if (s.length() == 0 || s.substring(0, 1).equals("#")) {
						startReading = false;
					} else {
						splitStr = s.split(":");
						if (splitStr.length == 5) {
							String id = splitStr[0];
							String type = splitStr[1];
							String name = splitStr[2];
							String email = splitStr[3];
							String initCreditStr = splitStr[4];
							double initCredit = Double.parseDouble(initCreditStr);
							User user = new User(id, type, name, email);
							user.getCard().topUp(initCredit);
							userList.put(id, user);
						}
					}
				}
			}

			if (s.substring(0, 7).equals("#prices")) {
//				System.out.println("Reading data for prices");
				boolean startReading = true;
				while (startReading && scanIn.hasNextLine()) {
					s = scanIn.nextLine();
					if (s.length() == 0 || s.substring(0, 1).equals("#")) {
						startReading = false;
					} else {
						splitStr = s.split(":");
						if (splitStr.length == 3) {
							String duration = splitStr[0];
							String zone = splitStr[1];
							double price = Double.parseDouble(splitStr[2]);
							TravelPass.setPrice(duration, zone, price);
						}
					}
				}
			}
			return userList;
		}
		return null;
	}

	public static ObservableList<User> loadUsers(Map<String, User> userList, ListView<User> users) {
		users.getItems().clear();// fix the problem of doubling up
		listViewData.clear();
		userList.forEach((key, value) -> {
			listViewData.add(value);
		});
		users.setItems(listViewData); // each user object has a toString() method
		return listViewData;
	}

	public static void saveFile() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));
			out.write("#users \n");

			userList.forEach((key, value) -> {
				String str = "" + key + ":" + value.getType() + ":" + value.getName() + ":" + value.getEmail() + ":"
						+ value.getCard().getCredit() + "\n";
				try {
					out.write(str);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
//			System.out.println("User data saved.");

			HashMap<String, Journey> jMap = new HashMap<String, Journey>();
			out.write("#journeys \n");
			userList.forEach((key, value) -> {
				String curID = value.getID();
				ArrayList<Journey> jList = value.getJourneyList();
				for (Journey j : jList) {
					String str = "" + curID + ":" + j.getDay() + ":" + j.getStartSt().getStationName() + ":"
							+ j.getStartTime() + ":" + j.getEndSt().getStationName() + ":" + j.getEndTime() + "\n";
					jMap.put(str, j);
				}

			});
			jMap.forEach((key, value) -> {
				try {
					out.write(key);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
//			System.out.println("Journey data saved.");

//			out.write("#stations \n");
//
//			for(Station station : stationList )
//			{
//				String str = "" + curID + ":" + j.getDay() + ":" + j.getStartSt().getStationName() 
//						+ ":" + j.getStartTime() + ":" + j.getEndSt().getStationName() + ":" + j.getEndTime() + "\n";
//				try {
//					out.write(str);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}

			out.write("#prices \n");

			String str = TravelPass.getPricesToString();
			try {
				out.write(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Data saved");

			out.close();
		} catch (IOException e) {
			System.out.println("Exception Occurred" + e);
		}
	}

}