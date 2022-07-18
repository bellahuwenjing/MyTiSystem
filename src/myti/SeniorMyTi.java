package myti;
public class SeniorMyTi extends TravelPass implements Concession {
	private double rate;
//	private String type;
	private static double seniorSatRate = 0;
	private static double seniorRate;

	public SeniorMyTi(String day, int startTime, String duration, String zone) {
		super(day, startTime, duration, zone);
		this.setType("Concession");
		seniorRate = super.getConcessionRate();
	}

	@Override
	public double getDiscountRate() {
		if (this.day.toLowerCase().contains("sun")) {
			rate = seniorSatRate;
		} else {
			rate = seniorRate;
		}
		return rate;
	}

	@Override
	public double getPrice() {
		double actualCost = super.price * getDiscountRate();
		return actualCost;
	}

}
