//useful for constant data thats being used in BuildGrid. 
//allows us to pass an object instead of all of these
//individually as arguments to BuildGrid constructor
public class BuildGridData {
	private CensusGroup[] input;
	private int xBuckets;
	private int yBuckets;
	private Rectangle map;
	
	public BuildGridData(int x, int y, CensusGroup[] input, Rectangle map) {
		xBuckets = x;
		yBuckets = y;
		this.input = input;
		this.map = map;
	}
	
	public int getXBuckets() {
		return xBuckets;
	}
	
	public int getYBuckets() {
		return yBuckets;
	}
	
	public CensusGroup[] getInput() {
		return input;
	}
	
	public Rectangle getMap() {
		return map;
	}

}
