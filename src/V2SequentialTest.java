import java.util.concurrent.ForkJoinPool;


public class V2SequentialTest {
    public static ForkJoinPool fjPool = new ForkJoinPool();
    public static int WARM_UP = 1000;
    public static int TESTS = 5000;
    
	public static void main(String[] args) {
		CensusData cd = PopulationQuery.parse("CenPop2010.txt");
		int cutOff = cd.data_size;
		ParallelCorners pc;
		while (cutOff >= 1) {
			double totalTime = 0;
			for (int i = 0; i < (WARM_UP + TESTS); i++) {
				long startTime = System.currentTimeMillis();
				pc = new ParallelCorners(0, cd.data_size, cd.data, cutOff);
				Rectangle r = fjPool.invoke(pc);
				long endTime = System.currentTimeMillis();
				long theTime = endTime - startTime;
				if (i >= WARM_UP) {
					totalTime += theTime;
				}
			}
			double avgTime = totalTime / (double)TESTS;
			System.out.println("avg time with cut off " + cutOff + " is: " + avgTime);
			cutOff = cutOff / 2;
		}

	}
}
