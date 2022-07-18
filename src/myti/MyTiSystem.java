package myti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MyTiSystem {
	static Scanner scanner = new Scanner(System.in);
	private static User currentUser;
	private static Map<String, User> userList = new HashMap<String, User>();
	private static ArrayList<Station> stationList = new ArrayList<>();

	public static User getUser() throws InvalidUser {
		User tempUser = null;
		// Must check if userList.size()==0? Otherwise infinite loop
		if (userList.size() > 0) {
			System.out.println("Enter user id: ");
			String userID = scanner.next().toLowerCase();
			tempUser = userList.get(userID);
			if (tempUser != null) {
				return tempUser;
			} else {
				throw new InvalidUser("The above id does not exist.\r\n");
			}
		} else {
			System.out.println("There are NO existing users stored in the system. ");
			System.out.println("Press 7 to add a new user. ");
			return tempUser;
		}
	}

	public static User loopGetUser() {
		try {
			return getUser();
		} catch (InvalidUser e) {
			System.err.print(e.getMessage());
			return loopGetUser();
		}
	}

	public static int getInt(String message) {
		System.out.println(message);
		while (!scanner.hasNextInt()) {
			System.out.println("Invalid input:" + scanner.next());
			System.out.println("You must enter a number:");
		}
		return scanner.nextInt();
	}

	public static double getDouble(String message) {
		System.out.println(message);
		while (!scanner.hasNextDouble()) {
			System.out.println("Invalid input:" + scanner.next());
			System.out.println("You must enter a number:");
		}
		return scanner.nextDouble();
	}

	public static void loopRecharge() {
		try {
			User user = getUser();
			double amount = getValidAmount(user);
			recharge(user, amount);
		} catch (InvalidUser e) {
			System.err.print(e.getMessage());
			loopRecharge();
		} catch (InvalidAmount e) {
			System.err.print(e.getMessage());
			loopRecharge();
		}
	}

	public static void recharge(User user, double amount) {

		user.getCard().topUp(amount);
		System.out.printf("Successfully added %.2f to card\n", amount);
		showCredit(user);
	}

	public static double getValidAmount(User user) throws InvalidAmount {
		double balance = user.getCard().getCredit();
		double amount = getDouble("How much credit do you want to add: ");
		double validAmount = 0;
		if (amount < 0) {
			throw new InvalidAmount("Positive amount only.\r\n");
		} else if ((balance + amount) > 100) {
			throw new InvalidAmount("That takes you over the credit limit. Please enter a smaller amount.\r\n");
		} else if (amount % 5 != 0) {
			throw new InvalidAmount("Charge amounts must be in multiples of 5.\r\n");
		} else {
			validAmount = amount;
//			return amount;//WHY DO I HAVE TO STORE IT IN ANOTHER VARIABLE???
		}
		return validAmount;
	}

	public static Station getStation(String msg) throws InvalidStation {
		System.out.println(msg);
		String stName = scanner.next().toLowerCase();
		Station validSt = null;
		for (Station station : stationList) {
			if (station.getStationName().toLowerCase().equals(stName)) {
				validSt = station;
				break;
			}
		}
		if (validSt == null) {
			throw new InvalidStation("Invalid station name.");
		}
		return validSt;
	}

	public static Station loopGetStation(String msg) {
		try {
			return getStation(msg);
		} catch (InvalidStation e) {
			System.err.println(e.getMessage());
			return loopGetStation(msg);
		}
	}

	public static void buyJourney() {
		currentUser = loopGetUser();
		Station fromSt = loopGetStation("From which station: ");
		Station toSt = loopGetStation("To which station: ");
		System.out.println("What day: ");
		String day = scanner.next().toLowerCase();
		int startTime = loopGetTime("Departure time: ");
		int endTime = loopGetTime("Arrival time: ");
		Journey j = currentUser.askForJourney(fromSt, toSt, day, startTime, endTime);
		String zone = getZone(fromSt, toSt);
		boolean isSuccess = findCheapestOption(currentUser, day, startTime, zone);
		if (isSuccess) {
			currentUser.addJourney(j);
		}
	}

	public static String getZone(Station fromSt, Station toSt) {
		String zone = "";
		if (fromSt.getZone() == 1 && toSt.getZone() == 1) {
			zone = "1";
		}
		if (fromSt.getZone() == 1 && toSt.getZone() == 2) {
			zone = "1+2";
		}
		if (fromSt.getZone() == 2 && toSt.getZone() == 2) {
			zone = "2";
		}
		if (fromSt.getZone() == 2 && toSt.getZone() == 1) {
			zone = "1+2";
		}
		return zone;
	}

	private static int getTime(String msg) throws InvalidTime {
		int time = getInt(msg);
		if (time > 2359 || time < 0) {
			throw new InvalidTime("Use a “24 hour” time representation, e.g. 800 for 8am or 2359 for 11.59pm");
		}
		return time;
	}

	private static int loopGetTime(String msg) {
		try {
			return getTime(msg);
		} catch (InvalidTime e) {
			System.err.println(e.getMessage());
			return loopGetTime(msg);
		}
	}

	public static boolean findCheapestOption(User currentUser, String day, int startTime, String zone) {
		ArrayList<TravelPass> preTickets = getPreviousTicket(currentUser, day);
		double amountPaid = getAmountPaid(preTickets);
		int size = preTickets.size();
		boolean isSuccess = false;
		double extraCost = 0;

		if (size == 0) {
			try {
				isSuccess = buyTravelPass(currentUser, day, startTime, "2h", zone, 0);
			} catch (InsufficientCredit e) {
				System.err.println(e.getMessage());
			}
		} else if (size == 1) {
			TravelPass lastTicket = preTickets.get(0);
			String lastZone = lastTicket.getZone();

			boolean exceedingEndTime = checkDayTime(lastTicket, day, startTime);

			if (lastTicket.getDuration().equals("24h")) {
				if (lastZone.contains(zone)) {
					System.out.println("Current All Day Pass is valid until 23.59.");
					isSuccess = true;
				} else {
					extraCost = Math.round((TravelPass.getAllDayZones12() - amountPaid) * 100.0) / 100.0;
					System.out.println("Zone " + lastZone + " Travel Pass upgraded to All Day Zone 1+2 Pass for "
							+ currentUser.getID() + " for $" + extraCost);
					isSuccess = updateTravelPass(currentUser, day, startTime, "24h", "1+2", amountPaid);
				}
			} else {
				if (exceedingEndTime && !lastZone.equals(zone)) {
					System.out.println("Additional 2 Hour Travel Pass is cheaper");
					try {
						isSuccess = buyTravelPass(currentUser, day, startTime, "2h", zone, 0);
					} catch (InsufficientCredit e) {
						System.err.println(e.getMessage());
					}
				}
				if (exceedingEndTime && lastZone.equals(zone)) {
					double allDayPrice = 0;
					switch (zone) {
					case "1":
						allDayPrice = TravelPass.getAllDayZones1();
						break;
					case "2":
						allDayPrice = TravelPass.getAllDayZones2();
						break;
					case "1+2":
						allDayPrice = TravelPass.getAllDayZones12();
						break;
					default:
						break;
					}
					extraCost = Math.round((allDayPrice - amountPaid) * 100.0) / 100.0;
					System.out.println("upgrade to all-day pass for zone " + zone + "for " + currentUser.getID()
							+ " for $" + extraCost);
					isSuccess = updateTravelPass(currentUser, day, startTime, "24h", zone, amountPaid);
				}
				if (!exceedingEndTime) {
					if (lastZone.contains(zone)) {
						System.out.println("Current 2 Hour Travel Pass is still valid.");
						isSuccess = true;
					} else {
						extraCost = Math.round((TravelPass.getTwoHourZones12() - amountPaid) * 100.0) / 100.0;
						System.out.println("Zone " + lastZone + " Travel Pass upgraded to 2 Hour Zone 1+2 Pass for "
								+ currentUser.getID() + " for $" + extraCost);
						isSuccess = updateTravelPass(currentUser, day, startTime, "2h", "1+2", amountPaid);
					}
				}
			}
		} else { // size == 2 (in theory there are at most 2)
			System.out.println("user already has " + size + " ticket(s) for today");
			extraCost = Math.round((TravelPass.getAllDayZones12() - amountPaid) * 100.0) / 100.0;
			System.out.println(
					"2 Hour Travel Pass upgraded to All Day Pass for " + currentUser.getID() + " for $" + extraCost);
			isSuccess = updateTravelPass(currentUser, day, startTime, "24h", "1+2", amountPaid);
		}
		return isSuccess;
	}

	public static ArrayList<TravelPass> getPreviousTicket(User user, String day) {
		ArrayList<TravelPass> preTickets = new ArrayList<>();

		for (TravelPass ticket : user.getTList()) {
			if (ticket.getDay().equals(day)) {
				preTickets.add(ticket);
			}
		}
		return preTickets;
	}

	public static boolean checkDayTime(TravelPass preTicket, String day, int startTime) {
		boolean exceedingEndTime = false;
		if (preTicket.getEndTime() <= startTime) {
			exceedingEndTime = true;
		}
		return exceedingEndTime;
	};

	public static double getAmountPaid(ArrayList<TravelPass> preTickets) {
		double total = 0;
		for (TravelPass ticket : preTickets) {
			total += ticket.getPrice();
		}
//		System.out.println("already paid " + total);
		return total;
	}

	public static boolean updateTravelPass(User user, String day, int startTime, String duration, String zone,
			double amountPaid) {
		boolean isSuccess = false;
		ArrayList<TravelPass> preTickets = getPreviousTicket(user, day);
		int size = preTickets.size();
		TravelPass lastT = preTickets.get(size - 1);
		try {
			if (duration.equals("2h")) {
				user.removeTicket(lastT);
				isSuccess = buyTravelPass(user, lastT.getDay(), lastT.getStartTime(), duration, zone, amountPaid);
			} else {
				for (int i = 0; i < size; i++) {
					user.removeTicket(preTickets.get(i));// Must remove ALL preTickets(not the very last one)
				}
				isSuccess = buyTravelPass(user, day, startTime, duration, zone, amountPaid);
			}

		} catch (InsufficientCredit e) {
			System.err.println(e.getMessage());
		}
		return isSuccess;
	}

	public static boolean buyTravelPass(User user, String day, int startTime, String duration, String zone,
			double amountPaid) throws InsufficientCredit {
		boolean isSuccess = false;
		String userType = user.getType();
		TravelPass ticket = null;

		switch (userType.toLowerCase()) {
		case "adult":
			ticket = new FullMyTi(day, startTime, duration, zone);
			break;
		case "senior":
			ticket = new SeniorMyTi(day, startTime, duration, zone);
			break;
		case "junior":
			ticket = new JuniorMyTi(day, startTime, duration, zone);
			break;
		default:
			break;
		}

		double price = ticket.getPrice();
		double balance = user.getCard().getCredit();

		if (balance >= (price - amountPaid)) {
			balance = user.getCard().deduct(price - amountPaid);
			System.out.println(ticket.getDescription() + "purchased for " + user.getID() + " for $" + price + "\r\n"
					+ "Valid until " + ticket.getEndTime());
			user.addTicket(ticket);
			isSuccess = true;
		} else {
			throw new InsufficientCredit("Purchase unsuccessful: You don't have enough credit.");
		}
		showCredit(user);
		return isSuccess;
	}

	public static void showCredit(User user) {
		double balance = user.getCard().getCredit();
		System.out.print("Credit remaining for " + user.getID() + ": $");
		System.out.printf("%5.2f\n", balance);
	}

	public static void addUser() throws InvalidUser {
		System.out.println("Enter user id: ");
		String userID = scanner.next().toLowerCase();
		User tempUser = userList.get(userID);
		if (tempUser == null) {
			System.out.println("Enter user name: ");
			String name = scanner.next();
			System.out.println("Enter user type: ");
			String type = scanner.next();
			System.out.println("Enter user email: ");
			String email = scanner.next();
			User newUser = new User(userID, type, name, email);
			userList.put(userID, newUser);
			System.out.println("Existing users: ");
			userList.forEach((key, value) -> System.out.println(value.getID()));
		} else {
			throw new InvalidUser("User ID already exists.\r\n");
		}
	}

	public static void loopAddUser() {
		try {
			addUser();
		} catch (InvalidUser e) {
			System.err.print(e.getMessage());
			loopAddUser();
		}
	}

	public static int getMenuChoice() {
		System.out.println("Menu Options:\r\n" + "1. Buy a Journey for a User\r\n"
				+ "2. Recharge a MyTi card for a User\r\n" + "3. Show remaining credit for a User\r\n"
				+ "4. Print User Reports\r\n" + "5. Update pricing\r\n" + "6. Show Station statistics\r\n"
				+ "7. Add a new User\r\n" + "8. Quit");
		int option = getInt("Enter a menu option: ");
		return option;
	}

	public static void printUserReports() {
		userList.forEach((key, value) -> {
			System.out.println("Journeys taken by " + key + ": ");
			value.printJourneys();
		});
	}

	private static void showStationStats() {
		for (Station st : stationList) {
			System.out.println(st.getFromStCount() + " journeys started from " + st.getStationName());
			System.out.println(st.getToStCount() + " journeys finished from " + st.getStationName());
		}
	}

	private static void updatePricing() {
		System.out.println("Enter duration: 2h for 2 hour, 24h for all day");
		String duration = scanner.next();
		System.out.println("Enter zone: 1 for zone 1, 2 for zone 2, 1+2 for zone 1+2");
		String zone = scanner.next();
		double newPrice = getDouble("Enter new price: ");
		TravelPass.setPrice(duration, zone, newPrice);
	}

	public static void main(String[] args) {
		getUserList();
		getStationList();
		int option = getMenuChoice();
		while (option != 8) {
			switch (option) {
			case 1:
				buyJourney();
				break;
			case 2:
				loopRecharge();
				break;
			case 3:
//				showCredit(getUser());//Unhandled exception type InvalidUser
				showCredit(loopGetUser());
				break;
			case 4:
				printUserReports();
				break;
			case 5:
				updatePricing();
				break;
			case 6:
				showStationStats();
				break;
			case 7:
				loopAddUser();
				break;
			case 8:
				System.out.println("Goodbye!");
				System.exit(8);
				break;
			default:
				System.out.println("Please enter a valid choice.");
				break;
			}
			option = getMenuChoice();
		}
	}

	// userList would be empty when imported by GUI if populating list
	// with data is placed simply inside the main()
	public static Map<String, User> getUserList() {
		User testUser = new User("bh", "Adult", "Bella Hu", "bhu@gmail.com");
		userList.put("bh", testUser);
		userList.put("lc", new User("lc", "Senior", "Lawrence Cavedon", "lawrence.cavedon@rmit.edu.au"));
		userList.put("vm", new User("vm", "Adult", "Vu Mai", "vuhuy.mai@rmit.edu.au"));
		userList.put("gh", new User("gh", "Junior", "Gwen Hu-Wilson", "gwen@gmail.com"));
		return userList;
	}

	public static ArrayList<Station> getStationList() {
		stationList.add(new Station("Central", 1));
		stationList.add(new Station("Flagstaff", 1));
		stationList.add(new Station("Richmond", 1));
		stationList.add(new Station("Preston", 1));
		stationList.add(new Station("Lilydale", 2));
		stationList.add(new Station("Epping", 2));

		return stationList;
	}
}
