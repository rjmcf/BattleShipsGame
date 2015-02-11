import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DestroyingEnemy extends EnemyTurnTaker{
	private DestroyingEnemy() {};
	
	// DestroyingEnemy is Singleton
	public static DestroyingEnemy intInstance;
	public static DestroyingEnemy getInstance() {
		if (intInstance == null) {
			intInstance = new DestroyingEnemy();
		}		
		return intInstance;
	}
	
	// list of positions that will be randomly chosen between to shoot at.
	ArrayList<Position> possiblePositions = new ArrayList<Position>(0);
	// list of positions that the AI is attempting to fill out into a ship.
	ArrayList<Position> hitPositions = new ArrayList<Position>(0);
	// list of positions that are being held while the hitPositions are being concentrated on.
	ArrayList<Position> manyShips = new ArrayList<Position>(0);
	// when the AI is deciding between many ships, this variable is true. When it has chosen and is
	//// focusing on one ship, this is false.
	boolean attackingMany = false;
	// These are used when finding the ends of a row of positions.
	ArrayList<Integer> xPositions = new ArrayList<Integer>(0);
	ArrayList<Integer> yPositions = new ArrayList<Integer>(0);
	
	GameBoard playerBoard;
	Random chance = new Random();
	
	public void acceptHitPos(Position hitPos) {
		// Called when the state of the Enemy changes from Searching to Destroying.
		possiblePositions.clear();
		hitPositions.clear();
		manyShips.clear();
		hitPositions.add(hitPos);
		attackingMany = false;
	}

	@Override
	public boolean takeTurn(GameBoard playerBoard, ArrayList<Ship> opponentFleet) {
		// returns true if still destroying, false otherwise.
		if (this.playerBoard == null) {
			this.playerBoard = playerBoard;
		}
		
		// Populates the list of possible firing positions according to the state of the board from
		//// last turn.
		setUpPossiblePositions();
		// Attacks one of the possible positions, and returns the ship that was hit if one was.
		Ship theShip = attack(opponentFleet);
		if (theShip == null) {
			// theShip == null implies MISS, so we are still destroying.
			return true;
		}
		else {
			// A ship has been hit
			for (Position pos : theShip.getPositions()) {
				if (pos.getState() != ShipState.HIT) {
					// If any position in the ship remains unhit, then we are still destroying.
					return true;
				}
			}
			// All positions in ship have been hit. So all positions and ship are sunk.
			for (Position pos : theShip.getPositions()) {
				pos.setState(ShipState.SUNK);
			}
			theShip.setState(ShipState.SUNK);
			System.out.println("I sank your ship!");
			// getRemainigShips discards any positions from manyShips that were just SUNK.
			manyShips = getRemainingManyShips();
			// getRemainingHits discards any positions from hitPositions that were just SUNK.
			if (getRemainingHits().size() == 0) {
				// If no hits are left, then we have SUNK exactly one ship.
				if (manyShips.size()==0) {
					// If there are no other ships we've discovered, then we go back to Searching.
					return false;
				}
				else {
					// If there were other ships, next turn we will have to decide which to pursue.
					attackingMany = true;
					return true;
				}
			}
			else {
				// If there are remaining hits, then we sank between one and two ships, and now have
				//// many ships to deal with.
				attackingMany = true;
				manyShips.addAll(getRemainingHits());
				attackingMany = true;
				return true;
			}
		}
	}
	
	private Ship attack(ArrayList<Ship> opponentFleet) {
		// Selects a random position from the possibilities and fires at it.
		Position attackPos = possiblePositions.get(chance.nextInt(possiblePositions.size()));
		// isHit returns the Ship that was hit if one was.
		Ship theShip = isHit(attackPos, opponentFleet);
		if (theShip == null) {
			// If theShip is null, then we have MISSed.
			attackPos.setState(ShipState.MISS);
			System.out.println("I missed!");
			return null;
		}
		else {
			// otherwise we have a HIT, which we add to hitPositions to use to calculate the ends
			//// of the line next turn.
			attackPos.setState(ShipState.HIT);
			hitPositions.add(attackPos);
			System.out.println("I hit your ship!");
			return theShip;
		}
	}
	
	private Ship isHit(Position hitPos, ArrayList<Ship> opponentFleet) {
		for (Ship ship : opponentFleet) {
			// Check if position is in any of the ships in the fleet.
			if (ship.isPosInShip(hitPos)) {
				// as position has been hit, no longer a possible firing option.
				possiblePositions.remove(hitPos);
				return ship;
			}
		}
		return null;
	}
	
	private void setUpPossiblePositions() {
		// chooses how to populate the possiblePositions list.
		if (manyShips.isEmpty()) {
			// If we have only discovered one ship go here.
			if (hitPositions.size() == 1) {
				// We have only one ship and one position on that ship hit so far.
				oneShipOnePositionFix();
				return;
			}
			else {
				// We have one ship, but several positions in a line on that ship (as far as we know
				//// for now).
				oneShipManyPositionsFix();
				return;
			}
		}
		else {
			// We have discovered several ships.
			if (attackingMany) {
				// We need to choose between which ships to attack.
				whenAttackingManyFix();
				return;
			}
			else {
				// Although we have discovered many ships, we are focusing on destroying just one,
				//// so we can reuse the previous function.
				oneShipManyPositionsFix();
			}
		}
	}
	
	private void oneShipOnePositionFix() {
		possiblePositions.clear();
		// there is only one position in this list, and it's the starting point of the ship.
		Position thePos = hitPositions.get(0);
		// returns the 4 positions to NSEW directions of thePos.
		ArrayList<Position> surrPos = playerBoard.returnSurrounding(thePos);
		for (Position pos : surrPos) {
			// checking that each position returned is an appropriate target. At least one must be,
			//// because there are no 1-piece ships.
			if (pos.getState() == ShipState.EMPTY || pos.getState() == ShipState.SHIP) {
				possiblePositions.add(pos);
			}
		}
	}
	
	private void oneShipManyPositionsFix() {
		// Assuming all positions are in straight line.
		xPositions.clear();
		yPositions.clear();
		possiblePositions.clear();
		
		// populate the two value lists.
		for (int i = 0; i < hitPositions.size(); i++) {
			xPositions.add(hitPositions.get(i).getX());
			yPositions.add(hitPositions.get(i).getY());
		}
		// positions are in a horizontal line 
		if (yPositions.get(0) == yPositions.get(1)) {
			int maxX = Collections.max(xPositions);
			int minX = Collections.min(xPositions);
			// we find the max and min x value, and our possible locations extend just beyond the 
			//// end of our current line, as long as they are valid positions.
			addToPossLocs(yPositions.get(0), maxX + 1);
			addToPossLocs(yPositions.get(0), minX - 1);
		}
		// positions are in a vertical line.
		else if (xPositions.get(0) == xPositions.get(1)) {
			int maxY = Collections.max(yPositions);
			int minY = Collections.min(yPositions);
			// as above.
			addToPossLocs(maxY + 1, xPositions.get(0));
			addToPossLocs(minY - 1, xPositions.get(0));
		}
		else {
			// Should not occur, so prints to show error.
			for (Position pos : hitPositions){
				System.out.println("("+pos.getX()+", "+pos.getY()+"), ");
			}
		}
		if (possiblePositions.size() == 0) {
			// If there are no possiblePositions after this, then we have hit not one but many 
			//// ships.
			manyShips.addAll(hitPositions);
			whenAttackingManyFix();
		}
	}

	private void whenAttackingManyFix() {
		// Selects which of the many ships we focus on first.
		attackingMany = false;
		hitPositions.clear();
		Position focusPos = manyShips.get(chance.nextInt(manyShips.size()));
		while (!isViablePosition(focusPos)) {
			// If the position is locked in by HIT or SUNK or MISSed positions then there is no 
			//// selecting it.
			focusPos = manyShips.get(chance.nextInt(manyShips.size()));
		}
		// We now take this Position as the starting point of our ship, with a restricted range of
		//// where we can fire around it.
		hitPositions.add(focusPos);
		oneShipOnePositionFix();
	}
	
	private boolean isViablePosition(Position thePos) {	
		// thePos is only a viable attacking position if at least one of the surrounding 4 have not
		//// already been attacked. 
		for (Position pos : playerBoard.returnSurrounding(thePos)) {
			if (pos.getState() == ShipState.EMPTY || pos.getState() == ShipState.SHIP) {
				return true;
			}
		}
		return false;
	}
	
	private void addToPossLocs(int row, int col) {
		// checks that the argument position is within th board and is not already in the list.
		if (row<0 || row>=playerBoard.getSize() || col<0 || col>=playerBoard.getSize()) {
			return;
		}
		Position newPos = playerBoard.getPos(row, col);
		if (possiblePositions.contains(newPos)) {
			return;
		}
		if (newPos.getState() == ShipState.EMPTY || newPos.getState() == ShipState.SHIP) {
			possiblePositions.add(newPos);
		}
	}
	
	private ArrayList<Position> getRemainingHits() {
		// removes all Positions that are SUNK from the hitPositions list.
		ArrayList<Position> remainingHits = new ArrayList<Position>(0);
		for (Position pos : hitPositions) {
			if (pos.getState() != ShipState.SUNK) {
				remainingHits.add(pos);
			}
		}
		return remainingHits;
	}
	
	private ArrayList<Position> getRemainingManyShips() {
		// removes all positions that were SUNK from the manyShips list.
		ArrayList<Position> remainingManyShips = new ArrayList<Position>(0);
		for (Position pos : manyShips) {
			if (pos.getState() != ShipState.SUNK) {
				remainingManyShips.add(pos);
			}
		}
		
		return remainingManyShips;
	}
}
