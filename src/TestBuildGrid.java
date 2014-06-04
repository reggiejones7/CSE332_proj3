import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Reggie Jones
 * Project 3
 * Tests the BuildGrid with a small input size
 *
 */

public class TestBuildGrid {
	public static ForkJoinPool fjPool = new ForkJoinPool();
	private BuildGrid bg;
	private BuildGridData data;
	private Rectangle map;
	private CensusGroup[] cg;
	
	@Before
	public void setUp() {
		/*
		 [_ _  _   1000
		  _ _  100 _ 
		  _ 10 _   _ 
		  1 _  _   _]
		 */
		cg = new CensusGroup[4];
		cg[0] = new CensusGroup(1, 1, 1);
		cg[1] = new CensusGroup(10, 2, 2);
		cg[2] = new CensusGroup(100, 3, 3);
		cg[3] = new CensusGroup(1000, 4, 4); 
		map = new Rectangle(0, 5, 5, 0);
		data = new BuildGridData(4, 4, cg, map);
	}
	
	@Test
	public void test_sequentialBuildGrid() {
		//build whole grid
		int[][] grid = BuildGrid.sequentialBuildGrid(true, 0, cg.length, data);
		/*debug println
		 for (int[] row : grid) {
			System.out.println(Arrays.toString(row));
		}*/
		assertEquals(grid[0][3], 1000);
		assertEquals(grid[1][2], 100);
		assertEquals(grid[2][1], 10);
		assertEquals(grid[3][0], 1);
	}
	

	/**
	 * In order this to actually run in parallel, SEQUENTIAL_CUTOFF in BuildGrid by hand 
	 * to a small number such as 1. I considered adding functionality to that class
	 * to be able to change the cutoff through a call, but that class is just going
	 * to keep getting bloated with code only for testing purposes which seems like
	 * bad design choice. 
	 */
	
	@Test
	public void test_parallel_BuildGrid() {
		//have to uncomment line in CensusGroup constructor in order to use real latitude
		//to get this to pass
		bg = new BuildGrid(0, cg.length, data);
		
		int[][] grid = fjPool.invoke(bg);
		//debug println
		for (int[] row : grid) {
			System.out.println(Arrays.toString(row));
		}
		assertEquals(grid[0][3], 1000);
		assertEquals(grid[1][2], 100);
		assertEquals(grid[2][1], 10);
		assertEquals(grid[3][0], 1);
	}
	
}
