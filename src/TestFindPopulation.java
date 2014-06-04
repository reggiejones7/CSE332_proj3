import static org.junit.Assert.*;

import java.util.concurrent.ForkJoinPool;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Reggie Jones
 * 
 * Tests the FindPopulation class. both the sequential and the parallel parts.
 * 
 * Note:will need to lower the SEQUENTIAL_CUTOFF by hand in order to get
 * the small inputs to run in parallel (this wont affect sequential as they
 * are explicit calls to the sequential code)
 *
 */

public class TestFindPopulation {
	public static ForkJoinPool fjPool = new ForkJoinPool();
	private CensusGroup[] cg;
	private Rectangle map;

	@Before
	public void setUp() {
		cg = new CensusGroup[4];
		cg[0] = new CensusGroup(1, 1, 1, true);
		cg[1] = new CensusGroup(10, 2, 2, true);
		cg[2] = new CensusGroup(100, 3, 3, true);
		cg[3] = new CensusGroup(1000, 4, 4, true); 
		map = new Rectangle(0, 5, 5, 0);
	}
	
	@Test
	public void test_sequentialPopulation_entire_map() {
		Pair<Integer, Integer> pair = sequentialPopulation(true, 1, 1, 4, 4);
		assertEquals((int) pair.getElementA(), 1111); //total pop
		assertEquals((int) pair.getElementB(), 1111); //total pop for the query
	}
	
	@Test
	public void test_sequentialPopulation_first_half_map() {
		Pair<Integer, Integer> pair = sequentialPopulation(true, 1, 1, 2, 2);
		assertEquals((int) pair.getElementA(), 1111); //total pop
		assertEquals((int) pair.getElementB(), 11); //total pop for the query
	}
	
	@Test
	public void test_sequentialPopulation_second_half_map() {
		Pair<Integer, Integer> pair = sequentialPopulation(true, 3, 3, 4, 4);
		assertEquals((int) pair.getElementA(), 1111); //total pop
		assertEquals((int) pair.getElementB(), 1100); //total pop for the query
	}
	
	
	/*=======WILL NEED TO CHANGE SEQUENTIAL_CUTOFF IN FINDCORNERS.JAVA TO A SMALL NUMBER
	 * FOR THESE TO RUN IN PARALLEL (as the spec suggests)============== */
	@Test
	public void test_FindPopulation_parallel_whole_map() {
		Pair<Integer, Integer> pair = sequentialPopulation(false, 1, 1, 4, 4);
		assertEquals((int) pair.getElementA(), 1111); //total pop
		assertEquals((int) pair.getElementB(), 1111); //total pop for the query
	}
	
	@Test
	public void test_FindPopulation_parallel_second_half_map() {
		Pair<Integer, Integer> pair = sequentialPopulation(false, 3, 3, 4, 4);
		assertEquals((int) pair.getElementA(), 1111); //total pop
		assertEquals((int) pair.getElementB(), 1100); //total pop for the query
	}
	
	@Test
	public void test_FindPopulation_parallel_first_half_map() {
		Pair<Integer, Integer> pair = sequentialPopulation(false, 1, 1, 2, 2);
		assertEquals((int) pair.getElementA(), 1111); //total pop
		assertEquals((int) pair.getElementB(), 11); //total pop for the query
	}
	
	
	//private helper
	private Pair<Integer, Integer> sequentialPopulation(boolean sequential, int west, 
													int south, int east, int north) {
		Rectangle queryRec = Rectangle.makeRectangle(west, south, east, north,
				map, 4, 4);
		FindPopulation findPop = new FindPopulation(0, cg.length, cg, queryRec);
		Pair<Integer, Integer> pair;
		if (sequential) {
			pair = findPop.sequentialPopulation(0, cg.length);
		} else {
			pair = fjPool.invoke(findPop);
		}
		return pair;
	} 

}
