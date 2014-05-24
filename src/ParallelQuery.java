import java.awt.geom.Point2D;
import java.util.concurrent.RecursiveTask;

/**
 * Reggie Jones & Tristan Riddell
 *  
 * ParallellQuery traverses the data and returns a pair containing 
 * the population of a given rectangle, and the total population of the datafile
 * 
 * Uses either parallelism or sequentialism.
 */
public class ParallelQuery extends RecursiveTask<Pair<Integer, Integer>> {
	
	private static final int SEQUENTIAL_CUTOFF = 1000;
	
	private int lo, hi;
	private Rectangle rec;
	private CensusGroup[] array;
	private int sequentialCutoff;
	
	public ParallelQuery(int lo, int hi, CensusGroup[] arr, Rectangle rec) {
		this.array = arr;
		this.lo = lo;
		this.hi = hi;
		this.rec = rec;
	}
	

	@Override
	protected Pair<Integer, Integer> compute() {
		if (hi - lo > sequentialCutoff) {
			ParallelQuery left = new ParallelQuery(lo, (hi+lo)/2, array, rec);
			ParallelQuery right = new ParallelQuery((hi+lo)/2, hi, array, rec);
			left.fork();
			Pair<Integer, Integer> rightA = right.compute();
			Pair<Integer, Integer> leftA = left.join();
			
			int newTotal = rightA.getElementA() + leftA.getElementA();
			int newGroup = rightA.getElementB() + leftA.getElementB();
			return new Pair<Integer, Integer>(newTotal, newGroup);
		} else {
			return sequentialPopulation(lo, hi);
		}
	}
	
	/**
	 * sequentialPopulation uses sequentialism to find the population 
	 * of the rectangle. It is used by the parallel compute() method
	 * when the sequential cutoff is reached, or used for the entire 
	 * data file when PopulationQuery is run with version v1
	 * @returns a pair of values : total population of datafile,
	 * and population within rectangle.
	 */
	public Pair<Integer, Integer> sequentialPopulation(int low, int high ) {
		int totalPop = 0;
		int recPop = 0;
		for (int i = low; i < high; i++) {
			CensusGroup group = array[i];
			Point2D.Float point = new Point2D.Float(group.longitude, group.latitude);
			if (rec.insideRectangle(point)) {
				recPop += group.population;
			}
			totalPop += group.population;
		}
		return new Pair<Integer, Integer>(totalPop, recPop);
	}
}
