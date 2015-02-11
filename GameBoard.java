import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class GameBoard {
	
	// Stores a reference to what "owns" the Board, so that their version of the symbolList can be 
	//// implemented.
	private Person caller;
	// Stores 2D array of size nxn of Positions.
	// array[rows][columns]
	private Position[][] mBoard;
	// Stores array of letters required for referencing squares.
	private String[] totalLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"};
	private String[] gameLetters;
	// Stores a list of the sizes of the ships for the random board generator in Enemy.
	private int[] shipSizes = {5, 4, 3, 3, 2};
	private int boardSize;
	
	public int getSize() { return boardSize; }
	public int[] getShipSizes() { return shipSizes; }
	public String[] getLetters() { return gameLetters; }
	public void setCaller(Person p) { caller = p; }
	public Position getPos(int row, int col) { return mBoard[row][col]; }
	
	public GameBoard(int n) {
		mBoard = new Position[n][n];
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				// Each element of the array is initialized to a new position that stores the x and 
				//// y coordinates and is EMPTY initially.
				Position newPos = new Position(col, row);
				mBoard[row][col] = newPos;
			}
		}
		boardSize = n;
		gameLetters = Arrays.copyOfRange(totalLetters, 0, n);
	}
	
	// Displays the board, implementing different logic depending on if it's the enemy's or the 
	//// player's	.
	public void displayBoard() {
		// Boiler plate code to make it all look nice.
		System.out.print("  ");
		for (int i = 0; i < boardSize; i++ )
		System.out.print(" "+gameLetters[i]+" ");
		
		System.out.print("\n");
		
		for (int i = 0; i < boardSize; i++) {
			if (i < 9) {
				System.out.print((i+1) + "  ");
			}
			else {
				System.out.print((i+1) + " ");
			}
			for (int j = 0; j < boardSize; j++) {
				// This line looks up the value of the state of the position and finds the symbol 
				//// that represents in the correct caller's symbolList.
				System.out.print(caller.symbolList[mBoard[i][j].getState().value] + "  ");
			}
			System.out.print("\n");
		}
	}
	
	public void displayFinalBoard(Player caller) {
		// Display board to show player what they missed at end.
		System.out.print("  ");
		for (int i = 0; i < boardSize; i++ )
		System.out.print(" "+gameLetters[i]+" ");
		
		System.out.print("\n");
		
		for (int i = 0; i < boardSize; i++) {
			if (i < 9) {
				System.out.print((i+1) + "  ");
			}
			else {
				System.out.print((i+1) + " ");
			}
			for (int j = 0; j < boardSize; j++) {
				// This line looks up the value of the state of the position and finds the symbol 
				//// that represents in the correct caller's symbolList.
				System.out.print(caller.finalSymbolList[mBoard[i][j].getState().value] + "  ");
			}
			System.out.print("\n");
		}
	}
	
	public ArrayList<Position> returnSurrounding(Position pos) {
		// returns a list of at most 4 positions at the cardinal points of the argument, as long
		//// as they are on the board.
		int posX = pos.getX(); int posY = pos.getY();
		ArrayList<Position> surrounding = new ArrayList<Position>(0);
		if (posX - 1 >= 0) {
			surrounding.add(mBoard[posY][posX-1]);
		}
		if (posX + 1 < boardSize) {
			surrounding.add(mBoard[posY][posX+1]);
		}
		if (posY - 1 >= 0) {
			surrounding.add(mBoard[posY-1][posX]);
		}
		if (posY + 1 < boardSize) {
			surrounding.add(mBoard[posY+1][posX]);
		}
		return surrounding;
	}
	
	public void placeShip(int length, int row1, int col1, int row2, int col2) throws Exception {
		// Row is row-1 because the array is 0-indexed, but the board itself starts at 1.
		Position pos1 = mBoard[row1-1][col1];
		Position pos2 = mBoard[row2-1][col2];
		
		// Various conditions for acceptable ship. 
		// 1). Does one of the coordinates stay the same?
		// 2). Is the second coordinate the right distance away from the first?
		// 3). Is every position that will be made a ship currently EMPTY?
		// 4). Make the ship and add it to the fleet.
		
		// 1).
		if (pos1.getX() == pos2.getX()) { 
			// 2).
			if (pos1.getY() == pos2.getY() + length){
				// 3).
				for (int i = pos2.getY(); i <= pos1.getY(); i++) {
					if (mBoard[i][pos1.getX()].getState() == ShipState.SHIP){
						throw new Exception();
					}
				}
				// 4).
				HashSet<Position> newShip = new HashSet<Position>();
				for (int i = pos2.getY(); i <= pos1.getY(); i++) {
					mBoard[i][pos1.getX()].setState(ShipState.SHIP);
					newShip.add(mBoard[i][pos1.getX()]);
				}
				caller.opponentFleet.add(new Ship(newShip));
			}
			// 2).
			else if (pos1.getY() == pos2.getY() - length){
				// 3).
				for (int i = pos1.getY(); i <= pos2.getY(); i++) {
					if (mBoard[i][pos1.getX()].getState() == ShipState.SHIP){
						throw new Exception();
					}
				}
				// 4).
				HashSet<Position> newShip = new HashSet<Position>();
				for (int i = pos1.getY(); i <= pos2.getY(); i++) {				
					mBoard[i][pos1.getX()].setState(ShipState.SHIP);
					newShip.add(mBoard[i][pos1.getX()]);
				}
				caller.opponentFleet.add(new Ship(newShip));
			}
			else { throw new Exception(); }
		}
		// 1).
		else if (pos1.getY() == pos2.getY()) { 
			// 2).
			if (pos1.getX() == pos2.getX() + length){
				// 3).
				for (int i = pos2.getX(); i <= pos1.getX(); i++) {
					if (mBoard[pos1.getY()][i].getState() == ShipState.SHIP){
						throw new Exception();
					}
				}
				// 4).
				HashSet<Position> newShip = new HashSet<Position>();
				for (int i = pos2.getX(); i <= pos1.getX(); i++) {
					mBoard[pos1.getY()][i].setState(ShipState.SHIP);
					newShip.add(mBoard[pos1.getY()][i]);
				}
				caller.opponentFleet.add(new Ship(newShip));
			}
			// 2).
			else if (pos1.getX() == pos2.getX() - length){
				// 3).
				for (int i = pos1.getX(); i <= pos2.getX(); i++) {
					if (mBoard[pos1.getY()][i].getState() == ShipState.SHIP){
						throw new Exception();
					}
				}
				// 4).
				HashSet<Position> newShip = new HashSet<Position>();
				for (int i = pos1.getX(); i <= pos2.getX(); i++) {
					mBoard[pos1.getY()][i].setState(ShipState.SHIP);
					newShip.add(mBoard[pos1.getY()][i]);
				}
				caller.opponentFleet.add(new Ship(newShip));
			}
			else { throw new Exception(); }
		}
		else { throw new Exception(); }
	}
}
