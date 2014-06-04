import java.awt.geom.Point2D;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 
 * @author Reggie Jones
 * Reggie Jones & Tristan Riddell
 * 
 * BuildGrid build a 2 dimensional grid (in parallel) of a size x * y of
 * the dimensions given by the user. From an input array inside of a BuildGridData
 * object, BuildGrid will iterate over that input array and fill out the grid accordingly
 * by adding the population of each CensusGroup in the input array to the 
 * appropriate location in the grid.
 *
 */

public class BuildGrid extends RecursiveTask<int[][]> {
	public static final int SEQUENTIAL_CUTOFF = 1000; 
	
	public static ForkJoinPool fjPool = new ForkJoinPool();
	private int low, high;
	private BuildGridData data;
	
	/**
	 * constructor
	 * @param low the lowest bound
	 * @param high the highest bound
	 * @param data a BuildGridData object with appropriate items
	 */
	public BuildGrid(int low, int high, BuildGridData data) {
		this.low = low;
		this.high = high;
		this.data = data;
	}
	
	/**
	 * Does the work of computing the grid in parallel
	 * @return int[][] of size x * y of the grid with total populations
	 * 			in each index for that
	 */
	@Override
	protected int[][] compute() {
		if (high - low > SEQUENTIAL_CUTOFF) {
			//parallel case- parallellizing on cd.data array
			BuildGrid left = new BuildGrid(low, (high + low) / 2, data);
			BuildGrid right = new BuildGrid((high + low) / 2, high, data);
			left.fork();
			int[][] rightGrid = right.compute();
			int[][] leftGrid = left.join();

			int x = data.getXBuckets();
			int y = data.getYBuckets();
			CombineGridsData cgd = new CombineGridsData(x, y, leftGrid, rightGrid);
			CombineGrids combined = new CombineGrids(0, x * y, cgd);
			int[][] grid = fjPool.invoke(combined);

			return grid;
		} else {
			return sequentialBuildGrid(low, high, data);
		}

	}
	
	/**
	 * sequential build a grid that holds the total population for that grid position.
	 * @param low lower index to start iterating over the input array
	 * @param high index to stop iterating over the input array
	 * @param data holds the input array and other data needed to build the grid
	 * @return a grid where each element holds the total population for that grid position
	 */
	public static int[][] sequentialBuildGrid(int low, int high, BuildGridData data) {
		//This seems kind of hacky, but I was pulling my hair out trying to test this function
		//and was miserably failing because it was using the mercatorConversion. 
		//Pulling the real work of the function out into an overloaded function was 
		//my way of avoiding the mercatorConversion while not breaking all the clients 
		//to this function
		return sequentialBuildGrid(false, low, high, data);
	}
	
	/**
	 * This function was overloaded strictly for testing purposes.
	 * sequential build a grid that holds the total population for that grid position.
	 * @param test boolean of whether calling this function for testing purposes
	 * @param low lower index to start iterating over the input array
	 * @param high index to stop iterating over the input array
	 * @param data holds the input array and other data needed to build the grid
	 * @return a grid where each element holds the total population for that grid position
	 */
	public static int[][] sequentialBuildGrid(boolean test, int low, int high, BuildGridData data) {
		int yBuckets = data.getYBuckets();
		int xBuckets = data.getXBuckets();
		int[][] grid = new int[yBuckets][xBuckets];
		
		for (int i = low; i < high; i++) {
			for (int y = 0; y < yBuckets; y++) {
				for (int x = 0; x < xBuckets; x++) {
					CensusGroup[] input = data.getInput();
					CensusGroup group = input[i];
					int col = x + 1;
					int row = yBuckets - y; // b/c our grid is flipped from how user views it
					Rectangle bucket = Rectangle.makeRectangle(col, row, col, row, 
												data.getMap(), xBuckets, yBuckets);
					
					Point2D.Float point;
					if (test) {
						point = new Point2D.Float(group.longitude, group.realLatitude);
					} else {
						point = new Point2D.Float(group.longitude, group.latitude);
					}
					if (bucket.insideRectangle(point)) {
						grid[y][x] += group.population;  
					}
				}
			}
		}

		return grid;
	}

}
