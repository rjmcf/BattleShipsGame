import java.util.HashSet;

public class Ship {
	// Define a ship as an Set of positions, each with a state.
	private HashSet<Position> mPositions;
	// Ship stores state of either SHIP or SUNK.
	private ShipState mShipState;
	public ShipState getState() { return mShipState; }
	public void setState(ShipState newState) { mShipState = newState; }
	
	// Also returns ship's length.
	public int getLength() { return mPositions.size(); }
	// Store of the positions the ship covers.
	public HashSet<Position> getPositions() { return mPositions; }
	
	public Ship(HashSet<Position> initPositions) {
		mPositions = initPositions;
		mShipState = ShipState.SHIP;
	}
	
	// Each position of the ship can be hit and the ship can be sunk.
	public boolean isPosInShip(Position thePos) {
		// HashSet is chosen because checking for member is O(1).
		if (mPositions.contains(thePos)) 
			{ return true; }
		else
			{ return false; }
	}
}
