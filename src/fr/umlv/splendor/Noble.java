package fr.umlv.splendor;

import java.util.HashMap;
import java.util.Objects;

public class Noble {
	private final String name;
	private final int points;
	private final HashMap<Color, Integer> price;
	
	public Noble(String name, int points, HashMap<Color, Integer> price) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(price);
		if (points <= 0) {
			throw new IllegalArgumentException("Points value is invalid");
		}
		
		this.name = name;
		this.points = points;
		this.price = price;
	}
	
	
	//return true if the player in parameter can receive it
	//else return false
	boolean isbuyable(Player player) {
		var bonus = player.totalBonus();
		for (var color: price.keySet()) {
			if (bonus.getOrDefault(color, 0) < price.get(color)) {
				return false;
			}
		}
		return true;
	}
	
	String name() {
		return this.name;
	}
	
	int points() {
		return this.points;
	}

	@Override
	public String toString() {
		return name + ", " + points + " points. Prix : " + price ;
	}
	
	
	
}