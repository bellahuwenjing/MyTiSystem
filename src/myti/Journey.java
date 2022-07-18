package myti;

public class Journey {
	private Station startSt;
	private Station endSt;
	private String day;
	private int startTime;
	private int endTime;

	public Journey(Station startSt, Station endSt, String day, int startTime, int endTime) {
		this.startSt = startSt;
		this.endSt = endSt;
		this.day = day;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Station getStartSt() {
		return startSt;
	}

	public Station getEndSt() {
		return endSt;
	}

	public String getDay() {
		return day;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public String getDescription() {
		String description = "Journey travelling from " + startSt.getStationName() + " at " + startTime + " to "
				+ endSt.getStationName() + " at " + endTime + " on " + day;
		return description;
	}

}
