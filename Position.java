public class Position {
	// Positions are always passed around by reference so if the state is changed somewhere it is 
	////changed everywhere.
	
	// Define an x and a y value that corresponds to position on game board.
	// Max and min values will be controlled by GameBoard
	private int mX;
	private int mY;
	// Each position can be "EMPTY", "SHIP", "MISS", "HIT" or "SUNK".
	private ShipState mState;
	
	public int getX() { return mX; }
	public int getY() { return mY; }
	public ShipState getState() { return mState; }
	public void setState(ShipState newState) { mState = newState; }
	
	public Position(int initX, int initY) {
		mX = initX;
		mY = initY;
		mState = ShipState.EMPTY;
	}
}