package myti;

public class TravelPass {
	private String duration;
	protected String day;
	private int startTime;
	private int endTime;
	private String zone;
	protected double price;
	private String description;// declare only. no assignment. otherwise no value
	private String type = ""; // remains null for all subclasses...how to fix???????????
	private double concessionRate = 0.5;

	private static double twoHourZone1 = 2.50;
	private static double twoHourZones12 = 3.50;
	private static double twoHourZone2 = 1.80;
	private static double allDayZone1 = 4.90;
	private static double allDayZone2 = 4.00;
	private static double allDayZones12 = 6.80;

	public TravelPass(String day, int startTime, String duration, String zone) {
		this.day = day;
		this.startTime = startTime;
		this.duration = duration;
		this.zone = zone;

		switch (duration + "," + zone) {
		case "2h,1":
			this.price = twoHourZone1;
			break;
		case "2h,2":
			this.price = twoHourZone2;
			break;
		case "2h,1+2":
			this.price = getTwoHourZones12();
			break;
		case "24h,1":
			this.price = allDayZone1;
			break;
		case "24h,2":
			this.price = allDayZone2;
			break;
		case "24h,1+2":
			this.price = getAllDayZones12();
			break;
		default:
			break;
		}

		String timeString = "";
		switch (duration) {
		case "2h":
			timeString = "2 Hour";
			endTime = startTime + 200;
			if (endTime > 2400) {
				endTime = startTime + 200 - 2400;
				// TODO what's happening with day? day becomes next day?
			}
			break;
		case "24h":
			timeString = "All Day";
			endTime = 2359;
			break;
		default:
			break;
		}
		description = timeString + " Zone " + zone;
//		getType() returns empty string when called inside the constructor:
//		description = timeString + " Zone " + zone+" (" + getType() + ") " + "Travel Pass ";
	}

	public String getDuration() {
		return this.duration;
	}

	public String getZone() {
		return this.zone;
	}

	public double getPrice() {
		return this.price;
	}

	public static String getPricesToString() {
		String st = "";
		st += "2Hour:Zone1:" + twoHourZone1 + "\n";
		st += "2Hour:Zone2:" + twoHourZone2 + "\n";
		st += "2Hour:Zone12:" + twoHourZones12 + "\n";
		st += "AllDay:Zone1:" + allDayZone1 + "\n";
		st += "AllDay:Zone2:" + allDayZone2 + "\n";
		st += "AllDay:Zone12:" + allDayZones12 + "\n";
		return st;

	}

	public static void setPrice(String duration, String zone, double newPrice) {
		switch (duration + ", " + zone) {
		case "2Hour, Zone1":
			twoHourZone1 = newPrice;
			break;
		case "2Hour, Zone2":
			twoHourZone2 = newPrice;
			break;
		case "2Hour, Zone12":
			twoHourZones12 = newPrice;
			break;
		case "AllDay, Zone1":
			allDayZone1 = newPrice;
			break;
		case "AllDay, Zone2":
			allDayZone2 = newPrice;
			break;
		case "AllDay, Zone12":
			allDayZones12 = newPrice;
			break;
		default:
			break;
		}
	}

	public String getDescription() {
		description += " (" + getType() + ") " + "Travel Pass ";
		return this.description;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public String getDay() {
		return day;
	}

	public static double getTwoHourZones12() {
		return twoHourZones12;
	}

	public static double getTwoHourZone1() {
		return twoHourZone1;
	}

	public static double getTwoHourZone2() {
		return twoHourZone2;
	}

	public static double getAllDayZones12() {
		return allDayZones12;
	}

	public static double getAllDayZones1() {
		return allDayZone1;
	}

	public static double getAllDayZones2() {
		return allDayZone2;
	}

	public double getConcessionRate() {
		return concessionRate;
	}

	public void setConcessionRate(double concessionRate) {
		this.concessionRate = concessionRate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
