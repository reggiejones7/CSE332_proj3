import java.awt.geom.Point2D;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class BuildGrid extends RecursiveTask<int[][]> {
	public static final int SEQUENTIAL_CUTOFF = 1000; 
	
	public static ForkJoinPool fjPool = new ForkJoinPool();
	private int low, high;
	private BuildGridData data;
	
	public BuildGrid(int low, int high, BuildGridData data) {
		this.low = low;
		this.high = high;
		this.data = data;
	}
	

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
	
	public int[][] sequentialBuildGrid(int low, int high, BuildGridData data) {
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
					Point2D.Float point = new Point2D.Float(group.longitude, group.latitude);
					if (bucket.insideRectangle(point)) {
						grid[y][x] += group.population;  
					}
				}
			}
		}
		
		return grid;
	}

}
