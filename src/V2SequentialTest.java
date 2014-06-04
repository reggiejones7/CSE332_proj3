import java.util.concurrent.ForkJoinPool;
/**
 * 
 * @author tristan
 * times v2's preprocess method using an increasing sequential cut off
 * 
 */

public class V2SequentialTest {
    public static ForkJoinPool fjPool = new ForkJoinPool();
    public static int WARM_UP = 1000;
    public static int TESTS = 5000;
    
	public static void main(String[] args) {
		CensusData cd = PopulationQuery.parse("CenPop2010.txt");
		System.out.println(cd.data_size);
		int cutOff = 1000;
		FindCorners pc;
		while (cutOff <= cd.data_size) {
			double totalTime = 0;
			for (int i = 0; i < (WARM_UP + TESTS); i++) {
				long startTime = System.currentTimeMillis();
				pc = new FindCorners(0, cd.data_size, cd.data, cutOff);
				Rectangle r = fjPool.invoke(pc);
				long endTime = System.currentTimeMillis();
				long theTime = endTime - startTime;
				if (i >= WARM_UP) {
					totalTime += theTime;
				}
			}
			double avgTime = totalTime / (double)TESTS;
			//System.out.println("avg time with cut off " + cutOff + " is: " + avgTime);
			System.out.println(avgTime);
			if (cutOff == cd.data_size) {
				cutOff += 1;
			} else {
				cutOff += 1000;
				if (cutOff > cd.data_size) {
					cutOff = cd.data_size;
				}
			}
		}
	}
}
