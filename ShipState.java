public enum ShipState {
	// The values allow us to reference their corresponding symbols in an array as an index.
	EMPTY(0), SHIP(1), MISS(2), HIT(3), SUNK(4);
	
	int value;
	ShipState(int v){
		value = v;
	}
	public int getValue() {
		return value;
	}
}
