import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Reggie Jones
 * 
 * this is an experiment for #7 on the readme. The outputs that the
 * graphs were made out of are gridSizeExperimentv4Output.txt gridSizeExperimentv5Output.txt
 * 
 * Note: manually change version number on line ~25 to run different versions
 *
 */

public class GridSizeExperiment {
	public static final int WARM_UP = 5;
	public static final int TESTS = 10;
	public static final int LOCK_THREADS = 4;
	public static ForkJoinPool fjPool = new ForkJoinPool();
	
	public static void main(String[] args) throws InterruptedException {
		//build the grid first
		CensusData cd = PopulationQuery.parse("CenPop2010.txt");
		FindCorners findC = new FindCorners(0, cd.data_size, cd.data, cd.data_size);
		Rectangle r = fjPool.invoke(findC);
		Rectangle map = r;
		
		int size = 10;
		int version = 4;
		System.out.println("======Version " + version + "=======");
		while (size <= 100) {
			BuildGridData data = new BuildGridData(size, size, cd.data, map);
			BuildGrid bg = new BuildGrid(0, cd.data_size, data);
			int[][] grid = null;
			
			long totalTime = 0;
			for (int j = 0; j < (WARM_UP + TESTS); j++) {
				//build the grid
				long startTime = System.currentTimeMillis();
				if (version == 4) {
					grid = fjPool.invoke(bg);
				} else {
					//v5
					int[][] tempGrid = new int[size][size];
					Lock[][] lockGrid = new Lock[size][size];
					for (int lockY = 0; lockY < size; lockY++) {
						for (int lockX = 0; lockX < size; lockX++) {
							lockGrid[lockY][lockX] = new ReentrantLock();
						}
					}
					LockBuildGrid[] lb = new LockBuildGrid[LOCK_THREADS];
					for (int i = 0; i < LOCK_THREADS; i++) {
						lb[i] = new LockBuildGrid(lockGrid, tempGrid, data, 
								(i*cd.data_size)/LOCK_THREADS, ((i+1) * cd.data_size) / LOCK_THREADS);
						lb[i].start();
					}
					for (int i = 0; i < LOCK_THREADS; i++) {
						lb[i].join();
					}
					grid = tempGrid;

				}
				long endTime = System.currentTimeMillis();
				long theTime = endTime - startTime;
				
				if (version == 4) {
					//can't check against jvm warm up because the only time
					//it actually gives for version 4 is the first time
					//and then everyone after that is time 0-1
					//milisecond (assuming from caching)
					totalTime += theTime;
				} else {
					//v5
					if (j >= WARM_UP) {
						totalTime += theTime;
					}
				}
			}
			//dont average time for version 4 since all but the first will be ~0
			long aveTime;
			if (version == 5) {
				aveTime = totalTime / (long) TESTS;
			}
			System.out.println("grid=" + size + "x" + size + " "+ totalTime + " ms");
			
			size += 10;
		}
		
		/*
		for (int[] row : grid) {
			System.out.println(Arrays.toString(row));
		}
		*/
		//test changing grid sizes
		
		
	}
}
