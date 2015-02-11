import java.util.ArrayList;

public class Person {
	// Stores default symbolList so can be accessed by GameBoard independently of whether Person
	//// is Player or Enemy.
	public String[] symbolList;
	// Stores an array of ships to be added to during the placeShip stage.
	public ArrayList<Ship> opponentFleet = new ArrayList<Ship>(0);
}
