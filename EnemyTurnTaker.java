import java.util.ArrayList;

public abstract class EnemyTurnTaker {
	// playerBoard passed so that the class can fire at it, opponent fleet passed so that ships can
	//// be destroyed.
	public abstract boolean takeTurn(GameBoard playerBoard, ArrayList<Ship> opponentFleet);
}
