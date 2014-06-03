/**
 * 
 * @author Reggie Jones
 * Reggie Jones & Tristan Riddell
 * Project 3
 * 
 * useful for constant data thats being used in CombineGrids. 
 * allows us to pass an object instead of all of these fields
 * individually as arguments to CombineGrids constructor
 */
public class CombineGridsData {
	private int[][] grid1;
	private int[][] grid2;
	private int xBuckets;
	private int yBuckets;
	
	/**
	 * constructor
	 * @param xBuckets number of columns user specified
	 * @param yBuckets number of rows user specified
	 * @param grid1 a grid thats getting added
	 * @param grid2 the other grid thats getting added
	 */
	public CombineGridsData(int xBuckets, int yBuckets, int[][] grid1, int[][] grid2) {
		this.xBuckets = xBuckets;
		this.yBuckets = yBuckets;
		this.grid1 = grid1;
		this.grid2 = grid2;
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
	 * @return grid1 class field
	 */
	public int[][] getGrid1() {
		return grid1;
	}
	

	/**
	 * a simple getter
	 * @return grid2 class field
	 */
	public int[][] getGrid2() {
		return grid2;
	}
}
