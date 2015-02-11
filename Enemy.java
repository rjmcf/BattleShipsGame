import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


public class Enemy extends Person{
	
	// The GameBoard that the enemy shoots on is stored in Player.
	// Enemy implements the Strategy Pattern, employing different strategies depending on
	//// whether it is Searching for a ship (shooting randomly) or Destroying one (has found a 
	//// ship and is systematically destroying it.
	
	// Enemy has a GameBoard that contains its ships and shows where the player has shot.
	private GameBoard playerBoard;	
	public GameBoard getPlayerBoard() { return playerBoard; }
	// Implementation of strategy.
	private EnemyTurnTaker turnTaker;		
	
	
	public Enemy(GameBoard newBoard) {
		playerBoard = newBoard;
		// symbolList documents symbols applied to positions that have the states:
		//// EMPTY, SHIP, MISS, HIT, SUNK. Note that the enemy's SHIP is the same as EMPTY, so the 
		//// player can not see where the enemy's ships are.
		symbolList = new String[]{"~", "/", "o", "X", "#"};
		// turnTaker is initialised as Searching
		turnTaker = SearchingEnemy.getInstance();
	}
	
	public void displayPlayerBoard() { 
		playerBoard.displayBoard();
	}
			
	public boolean placePlayerShip(int length) {
		// Code to place the ship for the player. Lots of buffer reading and boilerplate.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String locLetterAndNum = br.readLine();
			String locLetter = locLetterAndNum.substring(0, 1);
			int column1 = Arrays.asList(playerBoard.getLetters()).indexOf(locLetter);
			// number may have more than 1 digit.
			int row1 = Integer.parseInt(locLetterAndNum.substring(1,locLetterAndNum.length()));
			System.out.println("Where should the other end be?");
			locLetterAndNum = br.readLine();
			locLetter = locLetterAndNum.substring(0, 1);
			int column2 = Arrays.asList(playerBoard.getLetters()).indexOf(locLetter);
			int row2 = Integer.parseInt(locLetterAndNum.substring(1,locLetterAndNum.length()));
			// Sorting out row considerations occurs in GameBoard.
			playerBoard.placeShip(length - 1, row1, column1, row2, column2);
			return true;			
		}
		catch (IOException e) {
			System.out.println("Something went wrong!");
			System.exit(1);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Array index out of Bounds!");
		}
		catch (Exception e) {
			System.out.println("Make sure you type in the correct format of the positions, and that there is the right number of squares between your start and end point! Also make sure you're not overlapping any of your other ships!");
		}
		
		return false;
	}
		
	public boolean takeTurn() { 
		// Shows player state of their own board before and after attack.
		System.out.println("It's my turn now!");
		displayPlayerBoard();
		Game.sleep(1.5);
		System.out.println("Taking aim...");
		Game.sleep(2);
		// turnTaker implements strategy based on last go. Value of isDestroying determines if state
		//// will be searching or destroying next turn.
		boolean isDestroying = turnTaker.takeTurn(playerBoard, opponentFleet);
		if (isDestroying) {
			turnTaker = DestroyingEnemy.getInstance();
		}
		else {
			turnTaker = SearchingEnemy.getInstance();
		}
		// true if all player ships sunk, false otherwise.
		boolean didFinish = checkForFinished();
		displayPlayerBoard();
		Game.sleep(2);
		return didFinish;
	}
	
	public boolean checkForFinished() {
		for (Ship ship : opponentFleet) {
			if (ship.getState() == ShipState.SHIP) {
				return false;
			}
		}
		return true;
	}
}
