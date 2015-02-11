import java.util.ArrayList;
import java.util.Random;

public class SearchingEnemy extends EnemyTurnTaker{
	
	// Stores DestroyingEnemy instance so that the location of the hit position can be passed on.
	private DestroyingEnemy destInstance;
	private SearchingEnemy() { destInstance = DestroyingEnemy.getInstance(); }
	
	private Random chance = new Random();
	
	// SearchingEnemy is Singleton
	public static SearchingEnemy intInstance;
	public static SearchingEnemy getInstance() {
		if (intInstance == null) {
			intInstance = new SearchingEnemy();
		}		
		return intInstance;
	}

	@Override
	public boolean takeTurn(GameBoard playerBoard, ArrayList<Ship> opponentFleet) {
		// return true if destroying, false otherwise.
		
		// Populate an ArrayList of all the places that have not let been attacked. Can't do this 
		//// more efficiently (I think) because DestroyingEnemy also attacks ships.
		ArrayList<Position> possibleLocations = new ArrayList<Position>(0);
		for (int i = 0; i<playerBoard.getSize(); i++) {
			for (int j = 0; j<playerBoard.getSize(); j++) {
				Position newPos = playerBoard.getPos(j, i);
				if (newPos.getState() == ShipState.EMPTY || newPos.getState() == ShipState.SHIP) {
					possibleLocations.add(newPos);
				}
			}
		}
		
		// Randomly choose one of the positions in possibleLocations to fire at.
		int randIndex = chance.nextInt(possibleLocations.size());
		Position hitPos = possibleLocations.get(randIndex);
		if (isHit(hitPos, opponentFleet)) {
			// If there is a ship at this location, hit it.
			hitPos.setState(ShipState.HIT);
			destInstance.acceptHitPos(hitPos);
			System.out.println("I hit your ship!");
			// Enemy is now Destroying.
			return true;
		}		
		
		else{
			hitPos.setState(ShipState.MISS);
			System.out.println("I missed!");
			return false;
		}	
	}
	
	private boolean isHit(Position hitPos, ArrayList<Ship> opponentFleet) {
		for (Ship ship : opponentFleet) {
			// Check if position is in any of the ships in the fleet.
			if (ship.isPosInShip(hitPos)) {
				return true;
			}
		}
		return false;
	}
}
