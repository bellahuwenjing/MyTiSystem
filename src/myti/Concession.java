package myti;

public interface Concession {

	public double getDiscountRate();

//	A User may be an Adult, a Junior or Senior. A MyTi ticket is a FullMyTi if its User is an 
//	Adult, a JuniorMyTi if its User is a Junior, and a SeniorMyTi if its User is a Senior. 
//	JuniorMyTi and SeniorMyTi tickets are also ConcessionTickets with an associated 
//	discountRate--- you mst use a Java interface to implement this requirement. 

//	SeniorMyTi ticket holders travel free on Sundays---they do not have to pay for any journeys.

//	On other days for SeniorMyTI and all week for JuniorMyTi, 
//	discountRate is set at 0.5 (i.e. half-price) and 
//	is always the same for ALL Concession users, 
//	but this must be (theoretically) able to be changed.
}
