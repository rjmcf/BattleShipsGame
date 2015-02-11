import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class Player extends Person {
	
	// Player has a GameBoard that contains the enemy's ships and shows where he can shoot.
	// The GameBoard that the player owns is stored in Enemy.
	private GameBoard enemyBoard;
	public GameBoard getEnemyBoard() { return enemyBoard; }
	
	public String[] finalSymbolList = new String[]{"~", "\\", "o", "X", "#"};
		
	public Player(GameBoard newBoard) {
		enemyBoard = newBoard;
		// symbolList documents symbols applied to positions that have the states:
		//// EMPTY, SHIP, MISS, HIT, SUNK.
		symbolList = new String[]{"~", "~", "o", "X", "#"};
	}
	
	public void showEnemyBoard() {
		enemyBoard.displayBoard();
	}
	// Variant on above to show player what they missed.
	public void showFinalEnemyBoard() {
		enemyBoard.displayFinalBoard(this);
	}
	
	public boolean takeTurn() throws Exception {
		System.out.println("Your go!");
		// Show the player what they're aiming at.
		showEnemyBoard();
		// Take input of where they're aiming.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String locLetterAndNum = br.readLine();
			String locLetter = locLetterAndNum.substring(0, 1);
			// Check they typed it right. ie letter is part of board.
			if (!Arrays.asList(enemyBoard.getLetters()).contains(locLetter)) {
				throw new Exception();
			}
			int col = Arrays.asList(enemyBoard.getLetters()).indexOf(locLetter);
			// number may be more than one digit long.
			int row = Integer.parseInt(locLetterAndNum.substring(1,locLetterAndNum.length()));
			// Another format check. 
			if (row < 0 || row > enemyBoard.getSize()) {
				throw new Exception();
			}
			// enemy returns state of ship thats been hit, or MISS if none have been.
			ShipState result = testForHit(row-1, col);
			// return false if game hasn't ended, true if it has.
			// enemyBoard is always shown again so player can see outcome.
			switch (result) {
			case MISS: {
				System.out.println("You missed me!");
				showEnemyBoard();
				return false;
				}
			case HIT: {
				System.out.println("You hit one of my ships!");
				showEnemyBoard();
				return false;
				}
			case SUNK: {
				System.out.println("You hit one of my ships!");
				System.out.println("You sank my ship!");
				for (Ship ship : opponentFleet){
					if (ship.getState() == ShipState.SHIP) {
						// Any ship still alive means game is still going!
						showEnemyBoard();
						return false;
					}
				}
				// No ship unsunk means game over!
				showEnemyBoard();
				return true;
				}
			// Default case never reached, but still required.
			default: {
				return false;
				}
			}
		}
		catch (IOException e) {
			System.out.println("Something went wrong with the reading!");
		}
		// Exception used to throw back up and run the function again, but not have many function 
		//// calls on the call stack.
		catch (Exception e) {
			throw new Exception();
		}
		return false;
	}
	
	public ShipState testForHit(int row, int col) {
		// Give variable containing the position that was aimed for.
		Position hitPos = enemyBoard.getPos(row, col);
		// Check if this position is covered by any ships that are still not sunk.
		for (Ship ship : opponentFleet) {
			if (ship.isPosInShip(hitPos) && ship.getState() != ShipState.SUNK) {
				// Change state of position to hit.
				hitPos.setState(ShipState.HIT);
				for (Position pos : ship.getPositions()) {
					// Check for any positions not hit.
					if (pos.getState() != ShipState.HIT) {
						return ShipState.HIT;
					}
				}
				// No position is not hit, so ship is sunk.
				ship.setState(ShipState.SUNK);
				for (Position pos : ship.getPositions()) {
					// So all positions are now sunk.
					pos.setState(ShipState.SUNK);
				}
				return ShipState.SUNK;
			}
			// If ship already sunk, don't change position state but report a MISS.
			if (ship.isPosInShip(hitPos) && ship.getState() == ShipState.SUNK) {
				return ShipState.MISS;
			}
		}
		// No ship contains position, so that's a miss.
		hitPos.setState(ShipState.MISS);
		return ShipState.MISS;
	}
	
	public void populateBoard() throws Exception{
		// Create random behavior with Random class.
		Random chance = new Random();
		// Define variables that change based on the length of the ship being fitted.
		int length;
		// If a ship fails to be placed 10 times, then restart populating the board.
		int failsLeft;
		// Tracks when the ship is placed to break loops.
		boolean shipPlaced;
		// Loop 5 times, one for each ship.
		for (int i = 0; i < 5; i ++) {
			// Set initial conditions for each loop.
			failsLeft = 10;
			shipPlaced = false;
			length = enemyBoard.getShipSizes()[i];
			while (shipPlaced == false && failsLeft > 0) {
				// Try catch because enemyBoard.placeShip can fail.
				try {
					// Randomly choose the position of one end of the ship.
					int row1 = chance.nextInt(enemyBoard.getSize());
					int col1 = chance.nextInt(enemyBoard.getSize());
					// Randomly choose whether the ship will be vertical or horizontal.
					if (chance.nextInt(2) == 0) {
						// horizontal
						int row2 = row1;
						int col2 = col1 + length - 1;
						// if ship exceeds bounds of board going one way, send it the other.
						if (col2 >= enemyBoard.getSize()) {
							col2 = col1 - (length - 1);
						}
						// length is length - 1 because a 5 piece takes up the first square and 4 others.
						// rows are row + 1 to conform to other requirements when taking numbers from board.
						enemyBoard.placeShip(length - 1, row1+1, col1, row2+1, col2);
					}
					else {
						// vertical
						int row2 = row1 + length - 1;
						int col2 = col1;
						// if ship exceeds bounds of board going one way, send it the other.
						if (row2 >= enemyBoard.getSize()) {
							row2 = row1 - (length - 1);
						}
						enemyBoard.placeShip(length - 1, row1+1, col1, row2+1, col2);
					}
					shipPlaced = true;
				}
				catch (Exception e) {
					failsLeft --;
				}
			}				
			if (failsLeft == 0) {
				throw new Exception();
			}			
		}
	}
}
