/**
 * 
 * @author Reggie Jones
 * Reggie Jones & Tristan Riddell
 * Project 3
 * 
 * useful for constant data thats being used in BuildGrid. 
 * allows us to pass an object instead of all of these fields
 * individually as arguments to BuildGrid constructor
 */

public class BuildGridData {
	private CensusGroup[] input;
	private int xBuckets;
	private int yBuckets;
	//The Rectangle of the (US) map
	private Rectangle map;
	
	/**
	 * Constructor
	 * @param x the number of xBuckets the user specified
	 * @param y number of yBuckets the user specified
	 * @param input the input data
	 * @param map the map of the country
	 */
	public BuildGridData(int x, int y, CensusGroup[] input, Rectangle map) {
		xBuckets = x;
		yBuckets = y;
		this.input = input;
		this.map = map;
	}

	/**
	 * a simple getter
	 * @return xBuckets class field
	 */
	public int getXBuckets() {
		return xBuckets;
	}
	
	/**
	 * a simple getter
	 * @return yBuckets class field
	 */
	public int getYBuckets() {
		return yBuckets;
	}
	
	/**
	 * a simple getter
	 * @return input array class field
	 */
	public CensusGroup[] getInput() {
		return input;
	}
	
	/**
	 * a simple getter
	 * @return map class field, a Rectangle of the country
	 */
	public Rectangle getMap() {
		return map;
	}

}
