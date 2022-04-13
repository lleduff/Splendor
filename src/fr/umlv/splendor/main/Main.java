package fr.umlv.splendor.main;

import java.util.Objects;
import java.util.Scanner;

import fr.umlv.splendor.*;

public class Main {
	//const
	private final static int POINTS_TO_VICTORY = 15;
	
	
	// deal with the game and each player turn until one of them has 15 points
	// then players who have'nt played the last turn can play
	// return the player with the most points
	private static Player game(Board board, Scanner sc) {
		Objects.requireNonNull(board);
		Objects.requireNonNull(sc);
		
	    int cpt = board.players().indexOf(board.youngest());		//int between 0 and nbPlayer - 1, the index of the first player
	    Player current = board.youngest();			//player at index cpt
	    boolean end = false;
	    Player winner = board.youngest();
	   
	    while (!end) {	    	
	    	current = board.players().get(cpt % board.nbPlayers());
	        board.turn(current, sc); 		// we launch each player turn, the modulo is used to keep the index under 4
	        
	        if (current.points() >= POINTS_TO_VICTORY) {
	        	cpt++;
	            while((cpt % board.nbPlayers()) != board.players().indexOf(board.youngest())) { // to finish the current turn
		        	current = board.players().get(cpt % board.nbPlayers());
	            	board.turn(current, sc);		// we continue the same turn system until it is the youngest's turn again
	            	cpt++;
	            }
	            end = true;		//the main while stop
	        }
	        
	        cpt++;		//useless in the last iteration
	    }
	    for (var player: board.players()) {		//to find the player with the most points
	    	if (player.points() > winner.points()) {
	    		winner = player;
	    	}
	    }
	    return winner;
	    
	}
	
	

	public static void main(String[] args) {
		
		var sc = new Scanner(System.in);
		String nbPlayers = null;
		
		System.out.println("Jeu simplifié ou complet ? (1 ou 2)");
		var complete = sc.next();   		//gestion d'erreur a faire
		
		
		if (complete.equals("2")) {
			System.out.println("Combien de joueurs vont jouer ?");		// initialization of the board (ask number of players)
			nbPlayers = sc.next();
			while (Integer.parseInt(nbPlayers) < 2 && Integer.parseInt(nbPlayers) > 4) {
				System.out.println("Une partie se joue avec entre 2 et 4 joueurs");
				System.out.println("Combien de joueurs vont jouer ?");
				nbPlayers = sc.next();
			}
		}
		
		Board b;
		if (Integer.parseInt(complete) == 1)
			b = new Board();				//2 joueurs
		else {
			b = new Board(Integer.parseInt(nbPlayers));		//2 3 ou 4
		}
		
		b.addPlayers(sc);
		
		System.out.println("Début de la partie !");
		System.out.println();
		var winner = game(b, sc);		//the main loop
		
		System.out.println("Bravo " + winner.name() + " vous avez gagné avec " + winner.points() + " !");
		sc.close();
		System.out.println(new Board(3));
	}

}
