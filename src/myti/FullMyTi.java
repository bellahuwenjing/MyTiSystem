package myti;

public class FullMyTi extends TravelPass {
//	private String type;
	private static double FullRate = 1.00;

	public FullMyTi(String day, int startTime, String duration, String zone) {
		super(day, startTime, duration, zone);
		this.setType("Full Fare");
	}

	@Override
	public double getPrice() {
		double actualCost = super.price * FullRate;
		return actualCost;
	}
}
