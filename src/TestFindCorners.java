import static org.junit.Assert.*;

import java.util.concurrent.ForkJoinPool;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Reggie Jones
 * Test FindCorners class with some pretty small and basic inputs
 *
 */
public class TestFindCorners {
	public static ForkJoinPool fjPool = new ForkJoinPool();
	CensusGroup[] cg;
	FindCorners findC;
	
	@Before
	public void setUp() {
		cg = new CensusGroup[4];
		cg[0] = new CensusGroup(1, 1, 1);
		cg[1] = new CensusGroup(10, 2, 2);
		cg[2] = new CensusGroup(100, 3, 3);
		cg[3] = new CensusGroup(1000, 4, 4); 
		
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

}
