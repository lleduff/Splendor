package fr.umlv.splendor;

import java.util.HashMap;
import java.util.Objects;

public class Card {
	private final int level;
	private final int points;
	private final Color bonus;
	private final HashMap<Color, Integer> price;
	private boolean reservation;
	
	
	//Constructors
	public Card(int level, int points, Color bonus) {			//for basic version
		this(level, points, bonus, new HashMap<>());
		price.put(bonus, 3); 
	}
	
	public Card(int level, int points, Color bonus, HashMap<Color, Integer> price) {			//for complete version
		Objects.requireNonNull(bonus);
		Objects.requireNonNull(price);
		if (level < 1 || level > 3) {
			throw new IllegalArgumentException("The level of the card must be between 1 and 3");
		}
		if (points < 1) {
			throw new IllegalArgumentException("The card must give at least one point");
		}
		this.price = price;
		this.level = level;
		this.points = points;
		this.bonus = bonus;
		this.reservation = false; 		
	}
	
	//getters
	HashMap<Color, Integer> price() {
		return this.price;
	}

	int level() {
		return this.level;
	}
	
	int points() {
		return this.points;
	}
	
	Color bonus() {
		return this.bonus;
	}
	
	boolean reservation() {
		return this.reservation;
	}
	
	void changeReservationStatus() {
		reservation = !reservation;
	}

	@Override
	public String toString() {
		return "[" + points + " points, bonus " + bonus + ", price : " + price + (reservation ? "RESERVEE" : "");
	}
	
	
}
