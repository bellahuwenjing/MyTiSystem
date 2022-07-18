package myti;

public class MyTiCard {
	private double credit;

	public MyTiCard() {
		this.credit = 0;
	}

	public double getCredit() {
		return credit;
	}

	public double deduct(double amount) {
		return this.credit -= amount;
	}

	public double topUp(double amount) {
		return this.credit += amount;
	}

}
