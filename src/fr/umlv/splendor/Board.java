package fr.umlv.splendor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringJoiner;

public class Board {
	// Cons
	private static final Map<Integer, Integer> INI_NB_TOKEN = Map.of(2, 4, 3, 5, 4, 7);		//key : nbPl v : nbToken
	private static final int DEFAULT_NB_PLAYERS = 2;		//TODO initialize this value correctly (for part 1 it will always be 2)
	private static final Color[] COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK};
	private static final int MIN_TOKENS = 4;
	private static final int LVLMIN = 1;
	private static final int LVLMAX = 3;
	
	// Fields
	private final ArrayList<Player> players;
	private final int nbPlayers;
	private final HashMap<Color, Integer> tokens;
	private final HashMap<Integer, ArrayList<Card>> cardsDecks;	
	private final HashMap<Integer, ArrayList<Card>> onBoardCards;
	private final ArrayList<Noble> nobles;
	private boolean completeGame;
	
	// Constructors
	public Board(int nbPlayers, HashMap<Integer, ArrayList<Card>> cardsDecks, ArrayList<Noble> nobles) {
		Objects.requireNonNull(cardsDecks);
		Objects.requireNonNull(nobles);
		if (nbPlayers < 2 && nbPlayers > 4) {
			throw new IllegalArgumentException("Invalid number of players");
		}
		
		this.players = new ArrayList<>();
		this.onBoardCards = new HashMap<>();
		this.tokens = new HashMap<>();
		
		this.nbPlayers = nbPlayers;
		this.cardsDecks = cardsDecks;
		this.nobles = nobles;
		
		for (var color: COLORS) {
			this.tokens.put(color, INI_NB_TOKEN.get(nbPlayers));
		}
		
		var iterator = cardsDecks.values().iterator();		//shuffle each deck
		while (iterator.hasNext()) {
			var element = iterator.next();
			Collections.shuffle(element);
		}
		for (var lvl: cardsDecks.keySet()) {
			onBoardCards.put(lvl, new ArrayList<Card>());			//visible cards
			for (var j = 0; j < 4; j++) {		//4 cards by level
				pickACard(lvl);
			}
		}
		this.completeGame = true;
	}
	
	
	public Board(int nbPlayers) {			//complete
		this(nbPlayers, groupByLevel(initCardGame()), initNobles());
		Collections.shuffle(nobles);
		for (var i = 0; i < 10 - (nbPlayers + 1); i++) {
			nobles.remove(0);
		}
		tokens.put(Color.GOLD, 5);
		this.completeGame = true;
		
	}
	
	
	public Board() {			//basic
		this(DEFAULT_NB_PLAYERS, groupByLevel(quickInit()), new ArrayList<>());
		this.completeGame = false;
		
	}
	
	
	
	
	
	//getters
	public ArrayList<Player> players() {
		return this.players;
	}
	
	public int nbPlayers() {
		return this.nbPlayers;
	}
	
	
	
	
	
	

	

	
	//-------------------------PLAYER INITIALIZATION------------------------------------------------------
	
	

	
	// Add all players in the ArrayList players field. The order of players is arbitrary
	public void addPlayers(Scanner sc) {
		Objects.requireNonNull(sc);
		
		for (var i = 0; i < this.nbPlayers; i++) {
			System.out.println("J" + (i + 1));
			this.players.add(initPlayer(sc));
		}
	}
	
	
	// Ask informations of a a player, instantiate it and return it.
	private Player initPlayer(Scanner sc) {
		Objects.requireNonNull(sc);
		
		String ageStr;
		int ageInt;
		var validAge = true;
		System.out.print("Quel est votre nom : ");
		var name = sc.next();
		System.out.print("Quel est votre age : ");
		do {
			ageStr = sc.next();			//scanner
			try {
				 ageInt = Integer.parseInt(ageStr);
				 if (ageInt <= 0) {
					 System.out.print("Age invalide. Quel est votre age : ");
					 validAge = false;
			 	 } else {
			 		 validAge = true;
			 	 }
			} catch(NumberFormatException e) {		//parseInt throw this exception if ageStr is not a number
				System.out.print("Age invalide. Quel est votre age : ");
				validAge = false;
				ageInt = 0;		//bc ageInt must be initialized in all possible ways
			}
		} while (!validAge);		//age is valid if it is a positive number
		
		System.out.println();
		
		return new Player(name, ageInt);
	}
	
	
	//--------------------------CARDS-------------------------------------------------
	
	
	// take a list of cards in parameter and return a hashMap with for each level a list of cards of this level
	// used in the constructor to make each deck of card
	private static HashMap<Integer, ArrayList<Card>> groupByLevel(List<Card> list) {
		Objects.requireNonNull(list);
		
		var map = new HashMap<Integer, ArrayList<Card>>();
		for (var elem: list) {
			map.computeIfAbsent(elem.level(), key -> new ArrayList<Card>()).add(elem);			
		}
		return map;
	}
	

	
	//take the level of the card we want to add to the board in parameter
	//pick a card from the array list of the level corresponding and add it to the visibleCards
	private void pickACard(int level) {
		if (level < LVLMIN || level > LVLMAX) {
			throw new IllegalArgumentException("level value is invalid");
		}
		
		var pickedDeck = cardsDecks.get(level);
		if (pickedDeck == null) {
			return;		//a changer pour exception
		}
		var pickedCard = pickedDeck.remove(pickedDeck.size() - 1);
		onBoardCards.get(level).add(pickedCard);
	}
	
	
	
	//ask the player which card of the board he wants to buy
	//return the card chosen of null if he cancel
	private Card chooseCard(Scanner sc, Player player) {
		Objects.requireNonNull(sc);
		
		System.out.println("Niveau de la carte ? (q pour quitter)" + (completeGame ? " (0 pour carte réservée)" : ""));			//TODO ca va pas pour choisir une carte a resrver?
		var lvl = sc.next();
		while (!(lvl.equals("1") || lvl.equals("2") || lvl.equals("3") || lvl.equals("q") || lvl.equals("0"))) {
			System.out.println("Mauvaise saisie ! Recommencez");
			lvl = sc.next();
		}
		if (lvl.equals("q")) {
			return null;
		}
		if (lvl.equals("0")) {						//TODO gerer le 0 si pas complete game
			System.out.println("Quelle carte ?");		//TODO rajouter les test ici et mode pour quitter /!\
			var chosen = sc.next();
			
			return player.cards().get(Integer.parseInt(chosen) - 1);
			
		} else {
		
			System.out.println("Quelle carte ? (1 2 3 ou 4) (q pour quitter)");
			var chosen = sc.next();
			while(!(chosen.equals("1") || chosen.equals("2") || chosen.equals("3") || chosen.equals("4") || chosen.equals("q"))) {
				System.out.println("Mauvaise saisie ! Recommencez");
				chosen = sc.next();
			}
			if (chosen.equals("q")) {
				return null;			//TODO peut etre a changer pour retourner au choix du niveau 
			}
			//the chosenth card from the lvlth arrayList in the onBoardCards HashMap
			return this.onBoardCards.get(Integer.parseInt(lvl)).get(Integer.parseInt(chosen) - 1);
		}
	}
		
	
	
	
	//return true if the player has bought the card
	//else return false
	private boolean buyCard(Player player, Card card) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(card);
										//TODO gerer ici la reservation qui empeche d'acheter sur le plateau
		if (player.canbuyCard(card)) {
			
			
			if (card.reservation()) {		//TODO achat possible si dans la main du joueru en gros
				player.buyReserved(card);
			} else {
				player.addCard(card);
				onBoardCards.get(card.level()).remove(card);
				pickACard(card.level());
			}

			for (var color: card.price().keySet()) {
				for(var i = 0; i < card.price().get(color); i++) {
					addTokenToBank(player, color);
				}
			}
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * change the status of a card from not reserved to reserved, add it to the player hand but he don't earn the points until he buy it. If it is possible the player take a gold token
	 * @param player : the player who is going to bookCard
	 * @param card : the card which is going to be reserved
	 * @throws IllegalArgumentException if the card is already reserved
	 */
	private void bookCard(Player player, Card card) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(card);
		
		if (card.reservation()) {
			throw new IllegalArgumentException("To book a card, the card should not be already reserved");
		}
		
		card.changeReservationStatus();
		onBoardCards.get(card.level()).remove(card);		//retire du plateau comme dans buy card		
		pickACard(card.level());
		player.addCard(card);
		
		if (tokens.get(Color.GOLD) != 0) {
			System.out.println("Vous prenez un jeton or.");
			removeTokenFromBank(player, Color.GOLD);
		} else {
			System.out.println("Plus de jetons Or disponibles dans la banque !");
		}
		
	}
	
	
	
	//--------------------------NOBLES---------------------------------------------------
	
	//return an ArrayList of all buyable nobles for the Player player
	private ArrayList<Noble> buyableNobles(Player player) {
		var res = new ArrayList<Noble>();
		for (var n: nobles) {
			if (n.isbuyable(player)) {
				res.add(n);
			}
		}
		return res;
	}
	
	
	
	
	private void receiveNoble(Player player, Scanner sc) {
		var visits = buyableNobles(player);
		if (!visits.isEmpty()) {
			if (visits.size() == 1) {
				var v = visits.get(0);
				System.out.println("Un noble vient vous rendre visite ! ");
				System.out.println("Vous recevez " + v.name() + " ! +" + v.points());
				moveNoble(v, player);
				
			} else {
				System.out.println("Des nobles viennent vous rendre visite ! choisissez celui qui vous recevez parmis :");
				for (var v: visits) {
					System.out.println(visits.indexOf(v)+1 + " : " + v);
				}
				
				String received;
				int nreceived;
				boolean valid;
				do {						//parse the index of the noble requested
					received = sc.next();
					try {
						nreceived = Integer.parseInt(received);
						 if (nreceived <= 0 && nreceived > visits.size()) {
							 System.out.print("Saisie invalide, veuillez réessayer : ");
							 valid = false;
					 	 } else {
					 		 valid = true;
					 	 }
					} catch(NumberFormatException e) {
						System.out.print("Saisie invalide, veuillez réessayer : ");
						valid = false;
						nreceived = 0;
					}
				} while (!valid);
				nreceived--;					
				var v = visits.get(nreceived);				//jusqua la
				
				System.out.println("Vous recevez " + v.name() + " ! +" + v.points());
				moveNoble(v, player);
				
			}
		}
	}
	
	
	
	
	
	
	
	
	//---------------------------TURN --------------------------------------------------	
	
	
	// ask an action to the player and begin it
	// it allows the player to cancel his decision
	public void turn(Player player, Scanner sc) {
		Objects.requireNonNull(sc);
		Objects.requireNonNull(player);
		
		System.out.println(this);
		System.out.println();
		System.out.println(player);
		System.out.println("Choisissez une action (tapez 1 2 ou 3) ");
		System.out.println("1 : prendre 3 jetons pierre précieuse de couleur différente");
		System.out.println("2 : prendre 2 jetons pierre précieuse de la même couleur");
		System.out.println("3 : acheter une cater développement face visible");		//TODO reservation (4th action)
		if (completeGame) {
			System.out.println("4 : réserver une carte développement et prendre un jeton or (joker)");
		}
		
		var chosen = sc.next();
		
		switch(chosen) {
		
			case "1" -> {
				if (!(differentColor(player, sc))) {
					turn(player, sc);
					return;
				}
			}
			
			case "2" -> {		// call turn only if the user press q and cancel his choice
				if (!(sameColor(player, sc))) {		// method which do the action, return false if the user quit
					turn(player, sc);		// call the function recursively if quit during the action of the player
					return;
				}
			}
			
			case "3" -> {
				var card = chooseCard(sc, player);
				if (card == null) {
					turn(player, sc);
					return;
				}
				
				while(!buyCard(player, card)) {
					System.out.println("Vous n'avez pas assez pour acheter cett carte !");
					System.out.println();
					card = chooseCard(sc, player);
					if (card == null) {
						turn(player, sc);
						return;
					}
				}
				
			}
			
			
			
			default -> {
				if (completeGame && chosen.equals("4")) {			//reservation
					var card = chooseCard(sc, player);
					bookCard(player, card);
					
					
				} else {
					System.out.println("Mauvaise saisie, veuillez réessayer");
					turn(player, sc);
					return;
				}
			}
		}
		receiveNoble(player, sc);		
	}
	
	
	
	
	
	
	
	
		
	
	
	//-----------------------1ST ACTION--------------------------------------

	
	private boolean differentColor(Player player, Scanner sc) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(sc);
		
		var chosen = new ArrayList<Color>();
		Color tmp;
		
		for (var i = 0; i < 3; i++) {
			System.out.print(i + 1);
			System.out.println((i == 1) ? " ère " : " ème " + "couleur");
			tmp = askColor(sc);
			if (tmp == null) {
				return false;
			}
			
 			while(chosen.contains(tmp) || (tokens.get(tmp) == 0)) {		// la couleur est deja choisie ou plus dans la bank
				System.out.println("Vous ne pouvez pas sélectionner cette couleur");
				tmp = askColor(sc);
			}
			if (tmp == null) {
				return false;
			}
			chosen.add(tmp);
		}
		remove3Tokens(player, chosen);
		return true;
	}
	
	
	//remove from bank and in the player hand 3 token of 3 three color
	//should be called only if it is possible with the bank
	private void remove3Tokens(Player p, List<Color> colors) {
		Objects.requireNonNull(p);
		Objects.requireNonNull(colors);
		
		for (var el: colors) {
			removeTokenFromBank(p, el);
		}
	}
	
	
	
	
	
	//------------------------2ND ACTION-----------------------------------------------
	
	
	
	// return true if the tokens have been removed. else remove false : the user cancel his decision
	private boolean sameColor(Player player, Scanner sc) {
		Color color;
		do {
			color = askColor(sc);
			if (color == null) {
				return false;
			}
		} while (!checkAndRemove2Tokens(player, color));
		return true;
	}
	
	
	
	
	
	
	// remove 2 token of a color from the bank ONLY IF there is at least 4 token of this color in the bank
	// return true if it has be done, else false if it is impossible
	// param player : the player who is playing
	// param color : the color he has chosen
	private boolean checkAndRemove2Tokens(Player player, Color color) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(color);
		
		if (this.tokens.get(color) >= MIN_TOKENS) {
			for (var i = 0; i < 2; i++) {
				removeTokenFromBank(player, color);
			}
			return true;
		} else {
			System.out.println("Vous ne pouvez pas sélectionner cette couleur");
			return false;
		}
	}
	
	
	
	
	
	
	
	
	
	//-------------------------DIPLAY-------------------------------------------------------------------
	
	
	
	// display the bank at format :
	// COLOR : NUMBER
	private String showBank() {
		var joiner = new StringJoiner("\n");
		joiner.add("$$ BANK $$\n");
		
		for (var color: COLORS) {
			joiner.add(color + " : " + tokens.get(color));
		}
		if (completeGame) {
			joiner.add(Color.GOLD + " : " + tokens.get(Color.GOLD));
		}
		return joiner.toString();
	}
	
	
	private String showCards() {
		var builder = new StringBuilder();
		
		builder.append("-- CARDS --\n\n");
		builder.append("Nobles : ");
		for (var n: nobles) {
			builder.append(n);
		}
		builder.append("\n");
		for (var lvl: this.cardsDecks.keySet()) {
			builder.append("Level " + lvl + " : ");
			builder.append(this.cardsDecks.get(lvl).size() + " cards in deck. ");
			for (var card: this.onBoardCards.get(lvl)) {
				builder.append(card);
			}
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	
	
	@Override
	public String toString() {
		var joiner = new StringJoiner("\n");
		joiner.add("\n");
		joiner.add(showBank());
		joiner.add("\n\n");
		joiner.add(showCards());
		joiner.add("\n");
		
		return joiner.toString();
	}
	
	
	
	
	
	//-------------------------UTILITARIES FUNCTIONS---------------------------------------------------------
	
	
	
	// return the youngest player. He will begin the game
	public Player youngest() {
		Player res = players.get(0);
		for (var player: players) {
			if (player.age() < res.age())
				res = player;
		}
		return res;
	}
	
	
	
	// remove one token from the board and put it in the player hand
	// should be called only if checked if its possible
	private void removeTokenFromBank(Player player, Color color) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(color);
		
		var nb = this.tokens.get(color);
		
		if (nb != 0) {
			tokens.put(color, nb - 1);
			player.addToken(color);
		} else {
			throw new IllegalArgumentException("the bank doesn't have a token of this color");
		}
	}
	
	
	
	//put in tokens number of token the player had payed for a card 
	//remove a token from player's hand and put it into the bank
	// should be called only if checked if its possible		<--	SUREMENT A CHANGER 
	public void addTokenToBank(Player player, Color color) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(color);
		
		player.removeToken(color);
		tokens.put(color, tokens.get(color) + 1);
	}
	
	
	private void moveNoble(Noble noble, Player player) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(noble);
		
		player.addNoble(noble);
		nobles.remove(noble);
	}
	
	
	
	
	
	// return the color chosen by the user or null if he want to cancel his choice
	private Color askColor(Scanner sc) {	
		Objects.requireNonNull(sc);
		
		String chosen;
		Color res;		// idee untiliser une constante dico couleur/string pour simplifier cette fonction
		
		
		System.out.println("De quelle couleur ? (appuyer sur q pour annuler)");
		System.out.println("Choix : Green, Black, White, Red, Blue");
		chosen = sc.next(); 									//TODO ADD YELLOW HERRE
			
		while(!(chosen.equals("Red") || chosen.equals("Green") || chosen.equals("Black") || chosen.equals("White")|| chosen.equals("Blue") || chosen.equals("q"))) {
			System.out.println("Couleur non reconnue ! Réessayez");
			chosen = sc.next();
		}
		
		switch(chosen) {
			case "Red" ->  res = Color.RED;
			case "Blue" -> res = Color.BLUE;
			case "Green" -> res = Color.GREEN;
			case "Black" -> res = Color.BLACK;
			case "White" -> res = Color.WHITE;
			case "q" -> res = null;
			default -> throw new AssertionError("You should'nt be here");
		}
		return res;
	}	
	
	
	
	
	private static ArrayList<Noble> initNobles(){
		var nobles = new ArrayList<Noble>();
	    nobles.add(new Noble("Catherine de Medici", 3, new HashMap<Color, Integer>(Map.of(Color.GREEN, 3, Color.BLUE, 3, Color.RED, 3))));
	    nobles.add(new Noble("Elisabeth Of Austria", 3, new HashMap<Color, Integer>(Map.of(Color.BLACK, 3, Color.BLUE, 3, Color.WHITE, 3))));
	    nobles.add(new Noble("Isabella I Of Castile", 3, new HashMap<Color, Integer>(Map.of(Color.BLACK, 4, Color.WHITE, 4))));
	    nobles.add(new Noble("Niccolo Machiavelli", 3, new HashMap<Color, Integer>(Map.of(Color.BLUE, 4, Color.WHITE, 4))));
	    nobles.add(new Noble("Suleiman The Magnificent", 3, new HashMap<Color, Integer>(Map.of(Color.BLUE, 4, Color.GREEN, 4))));
	    nobles.add(new Noble("Canne Of Brittany", 3, new HashMap<Color, Integer>(Map.of(Color.GREEN, 3, Color.BLUE, 3, Color.WHITE, 3))));
	    nobles.add(new Noble("Charles V", 3, new HashMap<Color, Integer>(Map.of(Color.BLACK, 3, Color.RED, 3, Color.WHITE, 3))));
	    nobles.add(new Noble("Francis I Of France", 3, new HashMap<Color, Integer>(Map.of(Color.BLACK, 3, Color.RED, 3, Color.GREEN, 3))));
	    nobles.add(new Noble("Henry VII", 3, new HashMap<Color, Integer>(Map.of(Color.BLACK, 4, Color.RED, 4))));
	    nobles.add(new Noble("Mary Stuart", 3, new HashMap<Color, Integer>(Map.of(Color.RED, 4, Color.GREEN, 4))));
	    return nobles;
	}	
	
	
	private static ArrayList<Card> quickInit() {
		var tmp = new ArrayList<Card>();

		for (var color: COLORS) {
			for (var j = 0; j < 8; j++) {		//8 cartes niv 1 de chaque couleur
				tmp.add(new Card(1, 1, color));		//TODO changer ici quand plusieurs niveaux de cartes
			}
		}
		return tmp;
	}
	
	
	
	private static ArrayList<Card> initCardGame(){
		var cards = new ArrayList<Card>();
	    /* toutes les cartes de niveau 1 */
	    /*cartes noires */
	    cards.add(new Card(1, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.BLUE, 1, Color.GREEN, 1, Color.RED, 1))));
	    cards.add(new Card(1, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.BLUE, 2, Color.GREEN, 1, Color.RED, 1))));
	    cards.add(new Card(1, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.WHITE, 2, Color.BLUE, 2, Color.RED, 1))));
	    cards.add(new Card(1, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.GREEN, 1, Color.RED, 3, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.GREEN, 2, Color.RED, 1))));
	    cards.add(new Card(1, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.WHITE, 2, Color.GREEN, 2))));
	    cards.add(new Card(1, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.GREEN, 3))));
	    cards.add(new Card(1, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.BLUE, 4))));

	    /* cartes blue */
	    cards.add(new Card(1, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.GREEN, 1, Color.RED, 1, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.GREEN, 1, Color.RED, 2, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.GREEN, 2, Color.RED, 2))));
	    cards.add(new Card(1, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 1, Color.GREEN, 3, Color.RED, 1))));
	    cards.add(new Card(1, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.BLACK, 2, Color.WHITE, 1))));
	    cards.add(new Card(1, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.GREEN, 2, Color.BLACK, 2))));
	    cards.add(new Card(1, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.BLACK, 3))));
	    cards.add(new Card(1, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.RED, 4))));

	    /* cartes white */
	    cards.add(new Card(1, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 1, Color.GREEN, 1, Color.RED, 1, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 1, Color.GREEN, 2, Color.RED, 1, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 2, Color.GREEN, 2, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.WHITE, 3, Color.BLUE, 1, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.RED, 2, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 2, Color.BLACK, 2))));
	    cards.add(new Card(1, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 3))));
	    cards.add(new Card(1, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.GREEN, 4))));

	    /* green card */
	    cards.add(new Card(1, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.BLUE, 1, Color.BLACK, 1, Color.RED, 1))));
	    cards.add(new Card(1, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.BLUE, 1, Color.BLACK, 2, Color.RED, 1))));
	    cards.add(new Card(1, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.BLACK, 2, Color.BLUE, 1, Color.RED, 2))));
	    cards.add(new Card(1, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.BLUE, 3, Color.GREEN, 1))));
	    cards.add(new Card(1, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.WHITE, 2, Color.BLUE, 1))));
	    cards.add(new Card(1, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.BLUE, 2, Color.RED, 2))));
	    cards.add(new Card(1, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.RED, 3))));
	    cards.add(new Card(1, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.BLACK, 4))));

	    /* red card */
	    cards.add(new Card(1, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.BLUE, 1, Color.GREEN, 1, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 2, Color.BLUE, 1, Color.GREEN, 1, Color.BLACK, 1))));
	    cards.add(new Card(1, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 2, Color.BLACK, 2, Color.GREEN, 1))));
	    cards.add(new Card(1, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1, Color.BLACK, 3, Color.RED, 1))));
	    cards.add(new Card(1, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.BLUE, 2, Color.GREEN, 1))));
	    cards.add(new Card(1, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 2, Color.RED, 2))));
	    cards.add(new Card(1, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 3))));
	    cards.add(new Card(1, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 4))));

	    /* toutes les cartes de niveau 2 */

	    /* black card */

	    cards.add(new Card(2, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.WHITE, 3, Color.BLUE, 2, Color.GREEN, 2))));
	    cards.add(new Card(2, 1, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.WHITE, 3,Color.GREEN, 3, Color.BLACK, 2))));
	    cards.add(new Card(2, 2, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.GREEN, 4, Color.RED, 2, Color.BLUE, 1))));
	    cards.add(new Card(2, 2, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.GREEN, 5,Color.RED, 3))));
	    cards.add(new Card(2, 2, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.WHITE, 5))));
	    cards.add(new Card(2, 3, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.BLACK, 6))));

	    /* blue card */

	    cards.add(new Card(2, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.RED, 3, Color.BLUE, 2, Color.GREEN, 2))));
	    cards.add(new Card(2, 1, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.BLACK, 3,Color.GREEN, 3, Color.BLUE, 2))));
	    cards.add(new Card(2, 2, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 3, Color.WHITE, 5))));
	    cards.add(new Card(2, 2, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.WHITE, 2,Color.RED, 1, Color.BLACK, 4))));
	    cards.add(new Card(2, 2, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 5))));
	    cards.add(new Card(2, 3, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 6))));

	    /* white card */

	    cards.add(new Card(2, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.RED, 2, Color.BLACK, 2, Color.GREEN, 3))));
	    cards.add(new Card(2, 1, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.RED, 3,Color.WHITE, 2, Color.BLUE, 3))));
	    cards.add(new Card(2, 2, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLACK, 3, Color.RED, 5))));
	    cards.add(new Card(2, 2, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLACK, 2,Color.GREEN, 1, Color.RED, 4))));
	    cards.add(new Card(2, 2, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.RED, 5))));
	    cards.add(new Card(2, 3, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.WHITE, 6))));

	    /* green card */

	    cards.add(new Card(2, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.WHITE, 3, Color.RED, 3, Color.GREEN, 2))));
	    cards.add(new Card(2, 1, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.BLACK, 2,Color.WHITE, 2, Color.BLUE, 3))));
	    cards.add(new Card(2, 2, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.GREEN, 3, Color.BLUE, 5))));
	    cards.add(new Card(2, 2, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.WHITE, 4,Color.BLUE, 2, Color.BLACK, 1))));
	    cards.add(new Card(2, 2, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.GREEN, 5))));
	    cards.add(new Card(2, 3, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.GREEN, 6))));
	    
	    /* red card */

	    cards.add(new Card(2, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 2, Color.RED, 2, Color.BLACK, 3))));
	    cards.add(new Card(2, 1, Color.RED, new HashMap<Color, Integer>(Map.of(Color.RED, 2,Color.BLUE, 3, Color.BLACK, 3))));
	    cards.add(new Card(2, 2, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 3, Color.BLACK, 5))));
	    cards.add(new Card(2, 2, Color.RED, new HashMap<Color, Integer>(Map.of(Color.WHITE, 1,Color.BLUE, 4, Color.GREEN, 2))));
	    cards.add(new Card(2, 2, Color.RED, new HashMap<Color, Integer>(Map.of(Color.BLACK, 5))));
	    cards.add(new Card(2, 3, Color.RED, new HashMap<Color, Integer>(Map.of(Color.RED, 6))));


	    /* toutes les cartes de niveau 3 */

	    /* black card */

	    cards.add(new Card(3, 3, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.WHITE, 3, Color.BLUE, 3, Color.GREEN, 5, Color.RED, 3))));
	    cards.add(new Card(3, 4, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.RED, 7))));
	    cards.add(new Card(3, 4, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.GREEN, 3, Color.RED, 6, Color.BLACK, 3))));
	    cards.add(new Card(3, 5, Color.BLACK, new HashMap<Color, Integer>(Map.of(Color.BLACK, 3,Color.RED, 7))));

	    /* blue card */

	    cards.add(new Card(3, 3, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.WHITE, 3, Color.BLACK, 5, Color.GREEN, 3, Color.RED, 3))));
	    cards.add(new Card(3, 4, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.WHITE, 7))));
	    cards.add(new Card(3, 4, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 3, Color.WHITE, 6, Color.BLACK, 3))));
	    cards.add(new Card(3, 5, Color.BLUE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 3,Color.WHITE, 7))));

	    /* white card */

	    cards.add(new Card(3, 3, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLUE, 3, Color.RED, 5, Color.BLACK, 3, Color.GREEN, 3))));
	    cards.add(new Card(3, 4, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLACK, 7))));
	    cards.add(new Card(3, 4, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLACK, 6, Color.WHITE, 3, Color.RED, 3))));
	    cards.add(new Card(3, 5, Color.WHITE, new HashMap<Color, Integer>(Map.of(Color.BLACK, 7,Color.WHITE, 3))));

	    /* green card */

	    cards.add(new Card(3, 3, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.BLUE, 3, Color.WHITE, 5, Color.RED, 3, Color.BLACK, 3))));
	    cards.add(new Card(3, 4, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.BLUE, 7))));
	    cards.add(new Card(3, 4, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.BLUE, 6, Color.WHITE, 3, Color.GREEN, 3))));
	    cards.add(new Card(3, 5, Color.GREEN, new HashMap<Color, Integer>(Map.of(Color.BLUE, 7,Color.GREEN, 3))));

	    /* red card */

	    cards.add(new Card(3, 3, Color.RED, new HashMap<Color, Integer>(Map.of(Color.BLUE, 5, Color.WHITE, 3, Color.GREEN, 3, Color.BLACK, 3))));
	    cards.add(new Card(3, 4, Color.RED, new HashMap<Color, Integer>(Map.of(Color.GREEN, 7))));
	    cards.add(new Card(3, 4, Color.RED, new HashMap<Color, Integer>(Map.of(Color.BLUE, 3, Color.RED, 3, Color.GREEN, 6))));
	    cards.add(new Card(3, 5, Color.RED, new HashMap<Color, Integer>(Map.of(Color.GREEN, 7,Color.RED, 3))));
	    return cards;
	}
}