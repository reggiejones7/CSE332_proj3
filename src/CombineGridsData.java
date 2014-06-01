
public class CombineGridsData {
	private int[][] grid1;
	private int[][] grid2;
	private int xBuckets;
	private int yBuckets;
	
	public CombineGridsData(int xBuckets, int yBuckets, int[][] grid1, int[][] grid2) {
		this.xBuckets = xBuckets;
		this.yBuckets = yBuckets;
		this.grid1 = grid1;
		this.grid2 = grid2;
	}
	
	public int getXBuckets() {
		return xBuckets;
	}
	
	public int getYBuckets() {
		return yBuckets;
	}
	
	public int[][] getGrid1() {
		return grid1;
	}
	
	public int[][] getGrid2() {
		return grid2;
	}
}
