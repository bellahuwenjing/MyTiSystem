package myti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MyTiSystemWithoutExceptions {
	static Scanner scanner = new Scanner(System.in);
	private static User currentUser;

	static Map<String, User> userList = new HashMap<String, User>();

	static ArrayList<Station> stationList = new ArrayList<>();

	public static User getUser() {
		User tempUser = null;
		// Must check if userList.size()==0? Otherwise infinite loop
		if (userList.size() > 0) {
			System.out.println("Enter user id: ");
			String userID = scanner.next().toLowerCase();
			tempUser = userList.get(userID);
			if (tempUser != null) {
				return tempUser;
			} else {
				System.out.println("The above id does not exist. ");
				return getUser(); // NOTE HERE!!!
			}
		} else {
			System.out.println("There are NO existing users stored in the system. ");
			System.out.println("Press 7 to add a new user. ");
			return tempUser;
		}

	}

//		See examples: https://www.geeksforgeeks.org/how-to-iterate-hashmap-in-java/	
	public static User getUser1() {
		System.out.println("Enter user id: ");
		String userID = scanner.next().toLowerCase();
		User validUser = null;

		for (Map.Entry<String, User> set : userList.entrySet()) {
			if (set.getKey().equals(userID)) {
				validUser = set.getValue();
				break;
			}
		}
		return validUser;
	}

//		https://stackoverflow.com/questions/30026824/modifying-local-variable-from-inside-lambda
	public static User getUser0() {
		System.out.println("Enter user id: ");
		String userID = scanner.next().toLowerCase();
//			User validUser = null;
		final User validUser[] = new User[] { new User("", "", "", "") };
		userList.forEach((key, value) -> {
			if (key.equals(userID)) {
				// Solutions to "local variable defined in an enclosing scope must be final or
				// effectively final"
				validUser[0] = value;
				System.out.println("Found user" + value.getID());
			}
		});
		return validUser[0];
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

	// PROBLEMS: without nested if statements, it DOES NOT LOOP WITH MULTIPLE
	// INVALID INPUTS
//		public static int getAmount(double balance) {
//			int validAmount = 0;
//			int amount = getInt("How much do you want to add:");
//			while (amount <= 0) {
//				amount = getInt("You must enter a positive number:");
//			}
//			if (amount + balance > 100) {
//				System.out.println("Sorry, the max amount of credit allowed is $100.00");
//				amount = getInt("Enter a valid amount:");
//			} else {
//				if (amount % 5 != 0) {
//					System.out.println("Sorry, you can only add multiples of $5.00");
//					amount = getInt("Enter a valid amount:");
//				} else {
//					validAmount = amount;
//				}
//			}
//			return validAmount;
//		}

//		public static void rechargeMyTiCard() {
//			User user = getUser();
//			double balance = user.getCard().getCredit();
//			int amount = getAmount(balance);
//			user.getCard().topUp(amount);
//			System.out.println("User " + user.getID() + "'s credit = $" + user.getCard().getCredit() + "\r\n");
//		}

	public static void rechargeMyTiCard() {
		try {
			User user = getUser();
			double amount = getValidAmount(user);
			recharge(user, amount);
		} catch (Exception e) {
			// TODO: user not found exception
		}
	}

	public static void recharge(User user, double amount) {

		user.getCard().topUp(amount);
		System.out.printf("Successfully added %.2f to card\n", amount);
		showCredit(user);
	}

	public static double getValidAmount(User user) {
		double balance = user.getCard().getCredit();
		double amount = getDouble("How much credit do you want to add: ");
		double validAmount = 0;
		if (amount < 0) {
			System.out.println("Positive amount only.");
			getValidAmount(user);
		} else if ((balance + amount) > 100) {
			System.out.println("That takes you over the credit limit. Please enter a smaller amount.");
			getValidAmount(user);
		} else if (amount % 5 != 0) {
			System.out.println("Charge amounts must be in multiples of 5.");
			getValidAmount(user);
		} else {
			validAmount = amount;
//				return amount;//WHY DO I HAVE TO STORE IT IN ANOTHER VARIABLE???
		}
		return validAmount;
	}

	public static Station getStation(String msg) {
		System.out.println(msg);
		String stName = scanner.next().toLowerCase();
		Station validSt = null;
		for (Station station : stationList) {
			if (station.getStationName().toLowerCase().equals(stName)) {
				validSt = station;
				break;
			}
		}
		return validSt;
	}

	public static void buyJourney() {
		currentUser = getUser();
		while (currentUser == null) {
			System.out.println("Invalid user id. Re-enter: ");
			currentUser = getUser();
		}

		Station fromSt = getStation("From which station: ");
		while (fromSt == null) {
			fromSt = getStation("Invalid station name. Re-enter: ");
		}
		Station toSt = getStation("To which station: ");
		while (toSt == null) {
			toSt = getStation("Invalid station name. Re-enter: ");
		}
		System.out.println("What day: ");
		String day = scanner.next().toLowerCase();
		System.out.println("Departure time: ");
		int startTime = scanner.nextInt();
		System.out.println("Arrival time: ");
		int endTime = scanner.nextInt();
		Journey j = currentUser.askForJourney(fromSt, toSt, day, startTime, endTime);

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

		boolean isSuccess = findCheapestOption(currentUser, day, startTime, zone);
		if (isSuccess) {
			currentUser.addJourney(j);
			fromSt.fromStCountIncrement();
			toSt.toStCountIncrement();
		}
	}

	public static boolean findCheapestOption(User currentUser, String day, int startTime, String zone) {
		ArrayList<TravelPass> preTickets = getPreviousTicket(currentUser, day);
		double amountPaid = getAmountPaid(preTickets);
		int size = preTickets.size();
		boolean isSuccess = false;
		double extraCost = 0;

		if (size == 0) {
			isSuccess = buyTravelPass(currentUser, day, startTime, "2h", zone, 0);
		} else if (size == 1) {
			TravelPass lastTicket = preTickets.get(0);
			String lastZone = lastTicket.getZone();

			boolean exceedingEndTime = checkDayTime(lastTicket, day, startTime);
//				System.out.println("past end time? " + exceedingEndTime);
//				System.out.println("old zone: " + lastZone + "; new zone: " + zone);

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
					isSuccess = buyTravelPass(currentUser, day, startTime, "2h", zone, 0);
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
			System.out.println("user already has" + size + " ticket(s) for today");
//				TravelPass test_2h = new TravelPass(day, startTime, "2h", zone);
//				double price_2h = test_2h.getPrice();
//				System.out.println("all-day is cheaper than " + (amountPaid + price_2h));
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
//			System.out.println("already paid " + total);
		return total;
	}

	public static boolean updateTravelPass(User user, String day, int startTime, String duration, String zone,
			double amountPaid) {
		boolean isSuccess = false;
		ArrayList<TravelPass> preTickets = getPreviousTicket(user, day);
		int size = preTickets.size();
		TravelPass lastT = preTickets.get(size - 1);
		if (duration.equals("2h")) {
			user.removeTicket(lastT);
			isSuccess = buyTravelPass(user, lastT.getDay(), lastT.getStartTime(), duration, zone, amountPaid);
		} else {
			for (int i = 0; i < size; i++) {
				user.removeTicket(preTickets.get(i));// Must remove ALL preTickets(not the very last one)
			}
			isSuccess = buyTravelPass(user, day, startTime, duration, zone, amountPaid);
		}
		return isSuccess;
	}

	public static boolean buyTravelPass(User user, String day, int startTime, String duration, String zone,
			double amountPaid) {
		boolean isSuccess = false;
		String userType = user.getType();
		System.out.println(userType);
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
			System.out.println("You don't have enough credit.");
		}
		showCredit(user);
//			System.out.println(user.getTList());
		return isSuccess;
	}

	public static void showCredit(User user) {
		double balance = user.getCard().getCredit();
		System.out.print("Credit remaining for " + user.getID() + ": $");
		System.out.printf("%5.2f\n", balance);
	}

	public static void addUser() {
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
			System.out.println("User Id already exists.");
			addUser();
		}
	}

	// EFFECTIVELY USING .GET()
	public static void addUser0() {
		boolean duplicate = false;
//			User existingUser = null;
		String userID = "Test";
		System.out.println("Enter user id: ");
		do {
			userID = scanner.next().toLowerCase();
			for (String key : userList.keySet()) {
				if (key.equals(userID)) {
					duplicate = true;
					System.out.println("User ID already exists. Re-enter: ");
				}
			}
		} while (duplicate);
//				userList.forEach((key, value) -> {
//					if (key.equals(userID)) {
//						duplicate = true; // PROBLEM WITH LAMBDA:Local variable duplicate defined in an enclosing scope
//											// must be final or effectively final
////						existingUser = value;
//						System.out.println("User ID already exists. Re-enter: ");
//					}
//			});

//			} while (existingUser!=null);        

		System.out.println("Enter user name: ");
		String name = scanner.next();
		System.out.println("Enter user type: ");
		String type = scanner.next();
		System.out.println("Enter user email: ");
		String email = scanner.next();
		User newUser = new User(userID, name, type, email);
		userList.put(userID, newUser);
		System.out.println("Existing users: ");
		userList.forEach((key, value) -> System.out.println(value.getID()));
	}

	public static int getMenuChoice() {
		boolean invalidInput = true;
		int option = 0;
		System.out.println("Menu Options:\r\n" + "1. Buy a Journey for a User\r\n"
				+ "2. Recharge a MyTi card for a User\r\n" + "3. Show remaining credit for a User\r\n"
				+ "4. Print User Reports\r\n" + "5. Update pricing\r\n" + "6. Show Station statistics\r\n"
				+ "7. Add a new User\r\n" + "8. Quit\r\n");
		do {
			System.out.println("Enter a menu option (integer only): ");
			try {
//		Must use parseInt() to skip the incorrect value otherwise it's infinite loop
				option = Integer.parseInt(scanner.next());
				invalidInput = false;
			} catch (Exception e) {
				System.out.println("Invalid option. Try again. ");
			}
		} while (invalidInput);
		return option;
	}

	public static void printUserReports() {
		userList.forEach((key, value) -> {
			System.out.println("Journeys taken by " + key + ": ");
			value.printJourneys();
		});
	}

	private static void showStationStats() {
//		Print usage statistics for all Stations: i.e., how many journeys started at each Station, 
//		how many finished at each Station;
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
		User testUser = new User("bh", "Adult", "Bella Hu", "bhu@gmail.com");
		userList.put("bh", testUser);
		userList.put("lc", new User("lc", "Senior", "Lawrence Cavedon", "lawrence.cavedon@rmit.edu.au"));
		userList.put("vm", new User("vm", "Adult", "Vu Mai", "vuhuy.mai@rmit.edu.au"));
		userList.put("gh", new User("gh", "Junior", "Gwen Hu-Wilson", "gwen@gmail.com"));

		stationList.add(new Station("Central", 1));
		stationList.add(new Station("Flagstaff", 1));
		stationList.add(new Station("Richmond", 1));
		stationList.add(new Station("Preston", 1));
		stationList.add(new Station("Lilydale", 2));
		stationList.add(new Station("Epping", 2));

		int option = getMenuChoice();
		while (option != 8) {
			switch (option) {
			case 1:
				buyJourney();
				break;
			case 2:
				rechargeMyTiCard();
				break;
			case 3:
				showCredit(getUser());
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
				addUser();
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
}
