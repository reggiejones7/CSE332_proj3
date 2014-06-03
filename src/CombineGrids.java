import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class CombineGrids extends RecursiveTask<int[][]> {
	public static final int SEQUENTIAL_CUTOFF = 1000;
	
	private int low, high;
	private CombineGridsData data;
	
	//pre-condition:client should ensure grid1 and grid2 have the exact 
	// same dimensions and be rectangular (i.e. M x N)
	public CombineGrids(int low, int high, CombineGridsData data) {
		this.low = low;
		this.high = high;
		this.data = data;
	}
	
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
			try {
				combinedGrid[row][col] = leftGrid[row][col] + rightGrid[row][col];
			} catch (ArrayIndexOutOfBoundsException e) {
				/*System.out.println(i % innerArraySize +
						"\nrow:" + row + 
						"col:"+  col + "\n" + 
						leftGrid.length + "  " + rightGrid[0].length);
				*/

				for (int[] row1 : rightGrid) 
				    System.out.println(java.util.Arrays.toString(row1));
				
				System.exit(1);
			}
		}
		return combinedGrid;
	}
	
}
