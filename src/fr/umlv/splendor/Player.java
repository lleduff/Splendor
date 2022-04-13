package fr.umlv.splendor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringJoiner;

public class Player {
	private final String name;
	private final int age;
	private final HashMap<Color, Integer> tokens;
	private int points;
	private final ArrayList<Card> cards;
	private final ArrayList<Noble> nobles;
	
	
	public Player(String name, int age) {
		Objects.requireNonNull(name);
		if (age <= 0) {
			throw new IllegalArgumentException("Age can't be negative");
		}
		this.age = age;
		this.name = name;
		this.tokens = new HashMap<>();
		this.points = 0;
		this.cards = new ArrayList<>();
		this.nobles = new ArrayList<>();
	}
	
	// getters
	int age() {
		return this.age;
	}
	
	ArrayList<Card> cards() {
		return this.cards;
	}
	
	public int points() {
		return this.points;
	}
	
	public String name() {
		return this.name;
	}	
	
	
	
	void addToken(Color color) {
		Objects.requireNonNull(color);
		tokens.computeIfAbsent(color, key -> 0);
		tokens.put(color, tokens.get(color) + 1);
	}
	
	void removeToken(Color color) {
		Objects.requireNonNull(color);
		if (tokens.containsKey(color)) {
			tokens.put(color, tokens.get(color) - 1);
		} else {
			throw new IllegalArgumentException("Player doesn't have a token of this color");
		}		
	}
	
	void addCard(Card card) {
		Objects.requireNonNull(card);
		this.cards.add(card);
		
		if (!card.reservation()) {
			points += card.points();
		}
	}
	
	void addNoble(Noble noble) {
		Objects.requireNonNull(noble);
		this.nobles.add(noble);
		points += noble.points();
	}
	
	void buyReserved(Card card) {
		Objects.requireNonNull(card);
		if (!card.reservation()) {
			throw new IllegalArgumentException("The card should be reserved");
		}
		this.cards.remove(card);
		card.changeReservationStatus();		//remove then add it to the player hand but not reserved so he win the points
		addCard(card);
		
	}
	
	
	
	
	//return true if the player can buy the card
	//else false
	boolean canbuyCard(Card card) {		//gestion token or a faire
		Objects.requireNonNull(card);
		
		//var goldT = tokens.getOrDefault(Color.GOLD, 0);
		
		for (var color: card.price().keySet()) {			
			var price = card.price().get(color);
			var money = moneyByColor(color);
			if (money < price) {
				/*if (money + goldT >= price) {			//idee pour utiliser les jetons or mais ne marchera que quand buy card marchera (rendre les jetons au banquier)
					goldT -= price - money;
				} else {*/
					return false; 
				//}
				
			}
		}
		
		return true;
	}
	
	HashMap<Color, Integer> totalBonus() {
		var res = new HashMap<Color, Integer>();
		for (var card: cards) {
			res.put(card.bonus(), res.getOrDefault(card.bonus(), 0) + 1);
		}
		return res;
	}
	

	private int moneyByColor(Color color) {
		Objects.requireNonNull(color);
		var res = 0;
		for (var elem: cards) {
			if (elem.bonus().equals(color)) {
				res++;
			}
		}
		res += tokens.getOrDefault(color, 0);
		return res;
	}

	@Override		//a changer
	public String toString() {
		var builder = new StringBuilder();
		var joiner = new StringJoiner("\n");
		
		builder.append(name).append(", vous avez : ").append(points).append(points == 1 ? " point" : " points").append(".\nDans votre main : \n");
		builder.append("Jetons : \n");

		for (var color: tokens.keySet()) {
			joiner.add(color.toString() + " : " + tokens.get(color).toString());
		}
		builder.append(joiner.toString()).append("\n");
		
		builder.append("Cartes : \n");
		for (var card: cards) {
			builder.append(card).append("\n");
		}
		builder.append("\n");
		builder.append("Nobles : ");
		for (var noble: nobles) {
			builder.append(noble).append("\n");
		}
		
		return builder.toString();
		
	}	
}
