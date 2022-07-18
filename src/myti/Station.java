package myti;

public class Station {
	private String stationName;
	private int zone;
	private int fromStCount = 0;
	private int toStCount = 0;

	public Station(String stationName, int zone) {
		this.stationName = stationName;
		this.zone = zone;
	}

	@Override
	public String toString() {
		return stationName + ", " + zone;
	}

	public String getStationName() {
		return this.stationName;
	}

	public int getZone() {
		return this.zone;
	}

	public int getFromStCount() {
		return fromStCount;
	}

	public void fromStCountIncrement() {
		this.fromStCount += 1;
	}

	public int getToStCount() {
		return toStCount;
	}

	public void toStCountIncrement() {
		this.toStCount += 1;
	}

}
