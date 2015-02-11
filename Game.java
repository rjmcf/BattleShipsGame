import java.io.*;

public class Game {
	// Also contains functions for starting and ending the game.
	
	// Check variable for whether end game conditions met.
	private static boolean finished = false;
	// References to Persons.
	static Player thePlayer;
	static Enemy theEnemy;
	static boolean playerTurn = true;
	static boolean willContinue = true;

	// Game contains the main function and is thus the entry point to the game.
	public static void main(String[] args) {
		// Start game by giving text welcoming player.
		welcomePlayer();
		while (willContinue) {
			// Get required board size from player.
			int n = getBoardSize();
			while (n == -1) {
				// n = -1 is a fail state that prevents call stack stacking up.
				n = getBoardSize();
			}
			// Initialise players, and give their boards references to themselves.
			initPlayers(n);
			// Give player options to place their ships.
			initPlayerGameBoard();
			// Until games finished, keep looping and taking turns.
			while (finished == false) {
				gameLoop();
			}
			// loop over so game is finished.
			endGame();
		}		
	}
	
	private static void welcomePlayer() {
		System.out.println("Welcome to Robin's Battleships!");
		// delay for 1.5 seconds.
		sleep(2.0);
		System.out.println("What Size board would you like? The minimum size is 7 and the maximum is 20.");
	}
	
	private static int getBoardSize() {
		// Read value from console.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String strN = br.readLine();
			int n = Integer.parseInt(strN);
			// Check number given is sound.
			if (n>=7 && n <=20) {
				return n;
			}
		}
		catch (NumberFormatException e) {
			System.out.println("Please type the number as a digit, with no spaces.");
			return getBoardSize();
		}
		catch (IOException e) {
			System.out.println("Something went wrong!");
			System.exit(1);
		}
		System.out.println("Try again!");
		// Fail response starts function again, but without recursion.
		return -1;		
	}
	
	private static void initPlayers(int n) {
		// Have to set caller after initialization, else caller is null.
		thePlayer = new Player(new GameBoard(n));
		theEnemy = new Enemy(new GameBoard(n));
		// caller lets the GameBoard class know which Person object to set the fleet for etc.
		thePlayer.getEnemyBoard().setCaller(thePlayer);		
		theEnemy.getPlayerBoard().setCaller(theEnemy);
		// Populate the Enemy's Board with ships
		boolean boardPopulated = false;
		while (!boardPopulated) {
			try {
				thePlayer.populateBoard();
				boardPopulated = true;
			}
			catch (Exception e) {
				thePlayer.opponentFleet.clear();
			}
		}
	}
	
	private static void initPlayerGameBoard() {
		// For each ship, give a prompt and then call placeShip until the player responds correctly.
		theEnemy.displayPlayerBoard();
		boolean shipPlaced = false;
		while (shipPlaced == false) {
			System.out.println("Let's place your first ship! Type the location that you want the end of your 5 piece ship to be, \nletter first, then number.");
			shipPlaced = theEnemy.placePlayerShip(5);
		}
		theEnemy.displayPlayerBoard();
		shipPlaced = false;
		while (shipPlaced == false) {
			System.out.println("Now it's time for your 4 piece ship. Where should it start?");
			shipPlaced = theEnemy.placePlayerShip(4);
		}
		theEnemy.displayPlayerBoard();
		shipPlaced = false;
		while (shipPlaced == false) {
			System.out.println("You've got two 3 piece ships in your fleet! Where should the first one go?");
			shipPlaced = theEnemy.placePlayerShip(3);
		}
		theEnemy.displayPlayerBoard();
		shipPlaced = false;
		while (shipPlaced == false) {
			System.out.println("Place your second 3 piece ship now. Careful not to overlap!");
			shipPlaced = theEnemy.placePlayerShip(3);
		}
		theEnemy.displayPlayerBoard();
		shipPlaced = false;
		while (shipPlaced == false) {
			System.out.println("Last one! Where is your 2 piece ship going to go?");
			shipPlaced = theEnemy.placePlayerShip(2);
		}
		theEnemy.displayPlayerBoard();
		System.out.println("Let's begin!");
		sleep(2);
		System.out.println("Good luck! You'll need it against me...");
		sleep(2);
	}
	
	private static void gameLoop() {
		// playerTurn allows switching between player having a turn and enemy having a turn. 
		if (playerTurn == true) {
			try {
				finished = thePlayer.takeTurn();
				sleep(2);
			}
			catch (Exception e) {
				System.out.println("Make sure you're typing a valid location with no spaces!");
				return;
			}
			if (!finished) {
				// If finished, then we want a record of who played last!
				playerTurn = false;
			}
		}
		else {
			// theEnemy.takeTurn calls the takeTurn method of the relevant strategy class. 
			// theEnemy.takeTurn returns true if the game is finished.
			// EnemyTurnTaker.takeTurn returns true if theEnemy should be employing its destroy 
			//// strategy.
			finished = theEnemy.takeTurn();
			if (!finished) {
				playerTurn = true;
			}
		}
	}
	
	public static void sleep(double n) {
		try {
			// Delay for n seconds.
			Thread.sleep((long) n*1000);
		}
		// required exception handler.
		catch(InterruptedException e){
			Thread.currentThread().interrupt();
		}
	}
	
	private static void endGame() {
		if (playerTurn) {
			// playerTurn implies player took turn that finished game, ie player won.
			System.out.println("You won! I'm impressed!");
		}
		else {
			System.out.println("I won! Perhaps next time...");
			sleep(2);
			System.out.println("Here's what you missed of mine!");
			thePlayer.showFinalEnemyBoard();
			sleep(3);
		}
		
		System.out.println("Do you want to play me again? Type 'Y' for 'yes' or anything else for 'no'.");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String toContinue = br.readLine();
			if (toContinue.toUpperCase().equals("Y")) {
				willContinue = true;
			}
			else {
				willContinue = false;
			}
		}
		catch (IOException e) {
			System.out.println("Something went wrong!");
			System.exit(1);
		}
	}	
}
