import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Tristan Riddell
 * Project 3
 * Tests the LockGrid with a small input size
 *
 */

public class TestLockGrid {
	public static ForkJoinPool fjPool = new ForkJoinPool();
	private BuildGrid bg;
	private BuildGridData data;
	private Rectangle map;
	private CensusGroup[] cg;
	
	@Before
	public void setUp() {

		cg = new CensusGroup[10];
		cg[0] = new CensusGroup(100, 1, 1, true);
		cg[1] = new CensusGroup(100, 1, 1, true);
		cg[2] = new CensusGroup(100, 1, 1, true);
		cg[3] = new CensusGroup(100, 1, 1, true); 
		cg[4] = new CensusGroup(100, 1, 1, true); 
		cg[5] = new CensusGroup(1000, 1, 1, true); 
		cg[6] = new CensusGroup(1000, 1, 1, true); 
		cg[7] = new CensusGroup(1000, 1, 1, true); 
		cg[8] = new CensusGroup(1000, 1, 1, true); 
		cg[9] = new CensusGroup(1000, 1, 1, true); 
		map = new Rectangle(0, 5, 5, 0);
		data = new BuildGridData(4, 4, cg, map);
	}
	
	@Test
	public void test_sequentialBuildGrid() throws InterruptedException {
		//build whole grid
		int[][] grid = BuildGrid.sequentialBuildGrid(0, cg.length, data);
		int[][] tempGrid = new int[10][10];
		Lock[][] lockGrid = new Lock[10][10];
		for (int lockY = 0; lockY < 10; lockY++) {
			for (int lockX = 0; lockX < 10; lockX++) {
				lockGrid[lockY][lockX] = new ReentrantLock();
			}
		}
		LockBuildGrid[] lb = new LockBuildGrid[4];
		for (int i = 0; i < 4; i++) {
			lb[i] = new LockBuildGrid(lockGrid, tempGrid, data, 
					(i*cg.length)/4, ((i+1) * cg.length) / 4);
			lb[i].start();
		}
		for (int i = 0; i < 4; i++) {
			lb[i].join();
		}
		 for (int[] row : grid) {
			System.out.println(Arrays.toString(row));
		 }
		 	assertEquals(grid[3][0], 5500);
	}
}
