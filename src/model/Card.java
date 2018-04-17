package model;

public class Card {
	private static final String[] CARDNAMES = {"Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King"};
	private static final int[] CARDVALUES = {-1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
	public int cardNumber;
	
	public Card(int cardNumber) {
		this.cardNumber = cardNumber;
		
	}
	
	public int getValue() {
		return CARDVALUES[cardNumber];
	}
	
	public String toString() {
		return CARDNAMES[cardNumber];
	}
}
