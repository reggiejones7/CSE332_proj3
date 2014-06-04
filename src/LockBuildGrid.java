import java.awt.geom.Point2D;
import java.util.concurrent.locks.Lock;

/**
 * Tristan Riddell
 *  
 * LockBuildGrid creates a 2d array of ints representing a grid of rectangles.
 * The int value represents the population living within that particular rectangle
 * based on census data from CenPop2010.txt
 * 
 * This class uses a sequential algorithm, however it is called by multiple
 * threads simultaneously in order to accomplish the task in parallel.
 */
public class LockBuildGrid extends java.lang.Thread {
	Lock[][] lockGrid;
	int[][] grid;
	BuildGridData data;
	int lo;
	int hi;
	
	/**
	 * contructs a ParallelQuery and instantiates fields
	 * @param lo the lowest bound of the array
	 * @param hi the hightest bound of the array
	 * @param lockGrid 2d array of lock objects used to synchronize multiple threads
	 * @param grid 2d array of ints with populations to be created
	 * @param data a BuildGridData object holding necessary values such as 
	 * the input data from CenPop2010, and the number of rows and columns in the map.
	 */
	public LockBuildGrid(Lock[][] lockGrid, int[][] grid, BuildGridData data, int lo, int hi) {
		this.lockGrid = lockGrid;
		this.grid = grid;
		this.data = data;
		this.lo = lo;
		this.hi = hi;
	}
	
	/**
	 * given a lower and upper bound to the array of data,
	 * parses through the data in that section and adds the population
	 * the the appropriate grid squares.
	 */
	@Override 
	public void run() {
		
		int yBuckets = data.getYBuckets();
		int xBuckets = data.getXBuckets();
		
		for (int i = lo; i < hi; i++) {
			for (int y = 0; y < yBuckets; y++) {
				for (int x = 0; x < xBuckets; x++) {
					CensusGroup[] input = data.getInput();
					CensusGroup group = input[i];
					int col = x + 1;
					int row = yBuckets - y; // b/c our grid is flipped from how user views it
					Rectangle bucket = Rectangle.makeRectangle(col, row, col, row, 
												data.getMap(), xBuckets, yBuckets);
					Point2D.Float point = new Point2D.Float(group.longitude, group.latitude);
					if (bucket.insideRectangle(point)) {
						lockGrid[y][x].lock();
						grid[y][x] += group.population;  
						lockGrid[y][x].unlock();
					}
				}
			}
		}
		
	}
}
