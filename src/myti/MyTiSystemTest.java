package myti;
//e.g. test the method that calculates the cost of a Travel Pass between 2 given 
//stations at a specified time for a given User (given what other Journeys that User has taken 
//that day).

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MyTiSystemTest {
	private User currentUser;
	private final double DIFFERENCE_THRESHOLD = 0.0000000001;

	@Before
	public void setUp() throws Exception {
		currentUser = new User("lw", "Adult", "Luke Wilson", "luke@gmail.com");
		MyTiSystem.getUserList().put("lw", currentUser);
		MyTiSystem.recharge(currentUser, 20);
	}

	@Test
	public void testFindCheapestOption1() {
		boolean isSuccess = MyTiSystem.findCheapestOption(currentUser, "mon", 800, "1");
		assertTrue(isSuccess);
	}

	@Test
	public void testFindCheapestOption2() {
		// Assume a user asks for a journey from central to richmond starting at 8am
		MyTiSystem.findCheapestOption(currentUser, "tue", 800, "1");
		double balance1 = currentUser.getCard().getCredit();
		// The same user asks for another journey from richmond back to central starting
		// at 9am
		MyTiSystem.findCheapestOption(currentUser, "tue", 900, "1");
		double balance2 = currentUser.getCard().getCredit();
		assertEquals(balance1, balance2, DIFFERENCE_THRESHOLD);
	}

	@Test
	public void testFindCheapestOption3() {
		// First journey from central to richmond starting at 8am
		MyTiSystem.findCheapestOption(currentUser, "tue", 800, "1");
		double balance1 = currentUser.getCard().getCredit();
		// Another journey from richmond to epping starting at 9am
		MyTiSystem.findCheapestOption(currentUser, "tue", 900, "2");
		double expectedBalance = 16.5;// 2h z1 upgraded to 2h z1+2
		double balance = currentUser.getCard().getCredit();
		assertEquals(balance, expectedBalance, DIFFERENCE_THRESHOLD);
	}

	@Test
	public void testFindCheapestOption4() {
		// First journey from central to epping starting at 8am
		MyTiSystem.findCheapestOption(currentUser, "wed", 800, "1+2");
		double balance1 = currentUser.getCard().getCredit();
		// Another journey from epping to lilydale starting at 10am
		MyTiSystem.findCheapestOption(currentUser, "wed", 1000, "2");
		double expectedBalance = 14; // 3.5+2.5(additional 2h z2 ticket)
		double balance = currentUser.getCard().getCredit();
		assertEquals(balance, expectedBalance, DIFFERENCE_THRESHOLD);
	}

//	@Test // RUN THIS TEST ONLY AFTER sorting out a function to deal with TICKETS BOUGHT BEFORE MIDNIGHT
//	public void testFindCheapestOption5() {
//		// Assume a user asks for a journey from central to richmond starting at 11.30pm
//		MyTiSystem.findCheapestOption(currentUser, "tue", 2330, "1");
//		double balance1 = currentUser.getCard().getCredit();
//		// The same user asks for another journey from richmond back to central starting
//		// at 1am the very next day
//		MyTiSystem.findCheapestOption(currentUser, "wed", 100, "1");
//		double balance2 = currentUser.getCard().getCredit();
//		assertEquals(balance1, balance2, DIFFERENCE_THRESHOLD);
//	}

}
