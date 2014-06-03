import java.awt.geom.Point2D;
import java.util.concurrent.locks.Lock;


public class LockBuildGrid extends java.lang.Thread {
	Lock[][] lockGrid;
	int[][] grid;
	BuildGridData data;
	int lo;
	int hi;
	
	
	public LockBuildGrid(Lock[][] lockGrid, int[][] grid, BuildGridData data, int lo, int hi) {
		this.lockGrid = lockGrid;
		this.grid = grid;
		this.data = data;
		this.lo = lo;
		this.hi = hi;
	}
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
