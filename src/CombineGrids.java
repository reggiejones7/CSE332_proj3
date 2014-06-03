import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 
 * @author Reggie Jones
 * Reggie Jones & Tristan Riddel
 * 
 * CombineGrids adds the contents of two int[][] grids together at each
 * index of the grid. Client should use low and high arguments in the constructor
 * in a way that low should be the index of the first element of the grid and 
 * high should be the last index of the grid (i.e. as if it were only 1 long array).
 * (This is intentional as it will ensure that if a user gives a lopsided column and
 * row amount such as 1 100 or 100 1 that combining the two grids in parallel keeps 
 * the advantages of parallel computation)
 *
 */

public class CombineGrids extends RecursiveTask<int[][]> {
	public static final int SEQUENTIAL_CUTOFF = 1000;
	
	private int low, high;
	private CombineGridsData data;
	
	/**
	 * Constructor
	 * pre-condition:client should ensure grid1 and grid2 have the exact 
	 * same dimensions as eachother and be rectangular (i.e. M x N)
	 * @param low the low boundary for doing parralel computation on the grid
	 * @param high the high boundary for doing parralel computation on the grid
	 * @param data the CombineGridsData object that holds the constant data
	 */
	public CombineGrids(int low, int high, CombineGridsData data) {
		this.low = low;
		this.high = high;
		this.data = data;
	}
	
	/**
	 * Combine 2 grids in parallel. Simply adding the two elements at each
	 * index together.
	 * @return int[][] a new grid of the 2 grids being combined (added together)
	 */
	@Override
	protected int[][] compute() {
		//parrallelizing on the grid as if the grid were 1 long array 
		//instead of an array of arrays.
		int[][] leftGrid, rightGrid;
		if (high - low > SEQUENTIAL_CUTOFF) {
			CombineGrids left = new CombineGrids(low, (high + low) / 2, data);
			CombineGrids right = new CombineGrids((high + low) / 2, high, data);
			left.fork();
			rightGrid = right.compute();
			leftGrid = left.join();
		} else {
			leftGrid = data.getGrid1();
			rightGrid = data.getGrid2();
		}

		int innerArraySize = data.getXBuckets();
		int[][] combinedGrid = new int[data.getYBuckets()][data.getXBuckets()];
		for (int i = low; i < high; i++) {
			int row = i / innerArraySize;
			int col = (i % innerArraySize);
			if (col == 0) { 	//last element in innerarray
				col = innerArraySize - 1;
			} else {
				col--;
			}
			combinedGrid[row][col] = leftGrid[row][col] + rightGrid[row][col];
		}
		return combinedGrid;
	}
	
}
