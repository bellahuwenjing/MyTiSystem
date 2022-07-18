package myti;

public class JuniorMyTi extends TravelPass implements Concession {
	private double rate;
//	private String type;
	private static double juniorRate;

	public JuniorMyTi(String day, int startTime, String duration, String zone) {
		super(day, startTime, duration, zone);
		this.setType("Concession");
		juniorRate = super.getConcessionRate();
	}

	@Override
	public double getDiscountRate() {
		return rate = juniorRate;
	}

	@Override
	public double getPrice() {
		double actualCost = super.price * juniorRate;
		return actualCost;
	}
}
