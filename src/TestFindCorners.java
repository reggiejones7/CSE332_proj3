import static org.junit.Assert.*;

import java.util.concurrent.ForkJoinPool;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Reggie Jones
 * Test FindCorners class with some pretty small and basic inputs
 * 
 * NOTE: ignores mercator conversion by giving true as argument
 * to CensusGroup constructors
 */
public class TestFindCorners {
	public static ForkJoinPool fjPool = new ForkJoinPool();
	private CensusGroup[] cg;
	private FindCorners findC;
	
	@Before
	public void setUp() {
		cg = new CensusGroup[4];
		cg[0] = new CensusGroup(1, 1, 1, true);
		cg[1] = new CensusGroup(10, 2, 2, true);
		cg[2] = new CensusGroup(100, 3, 3, true);
		cg[3] = new CensusGroup(1000, 4, 4, true); 
	}

	@Test
	public void test_FindCornersSequential() {
		findC = new FindCorners(0, cg.length, cg, cg.length);
		Rectangle result = fjPool.invoke(findC);
		assertEquals(result.left, 1, 0);
		assertEquals(result.bottom, 1, 0);
		assertEquals(result.right, 4, 0);
		assertEquals(result.top, 4, 0);
		
	}
	
	@Test
	public void test_FindCorners_parallel() {
		findC = new FindCorners(0, cg.length, cg, 1); //last arg indicates parallel
		Rectangle result = fjPool.invoke(findC);
		assertEquals(result.left, 1, 0);
		assertEquals(result.bottom, 1, 0);
		assertEquals(result.right, 4, 0);
		assertEquals(result.top, 4, 0);
		
	}
}
