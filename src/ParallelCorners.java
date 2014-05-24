import java.util.concurrent.RecursiveTask;

/**
 * Reggie Jones & Tristan Riddell
 *  
 * ParallellCorners traverses the data and returns a rectangle containing 
 * the minimum and maximum longitude and latitude found in the array of CensusGroups.
 * 
 * Uses either parallelism or sequentialism depending on how it is constructed.
 */
public class ParallelCorners extends RecursiveTask<Rectangle> {

	private static final int SEQUENTIAL_CUTOFF = 1000;
	
	private int lo, hi;
	private CensusGroup[] array;
	private int sequentialCutoff;
	
	public ParallelCorners(int lo, int hi, CensusGroup[] arr) {
		this(lo, hi, arr, SEQUENTIAL_CUTOFF);
	}
	
	public ParallelCorners(int lo, int hi, CensusGroup[] arr, int cutoff) {
		this.array = arr;
		this.lo = lo;
		this.hi = hi;
		sequentialCutoff = cutoff;
	}
	
	@Override
	protected Rectangle compute() {
		if (hi - lo > sequentialCutoff) {
			ParallelCorners left = new ParallelCorners(lo, (hi+lo)/2, array );
			ParallelCorners right = new ParallelCorners((hi+lo)/2, hi, array);
			left.fork();
			Rectangle rightA = right.compute();
			Rectangle leftA = left.join();

			return rightA.encompass(leftA);
		} else {
			Rectangle rec = new Rectangle(array[lo].longitude, array[lo].longitude,
											array[lo].latitude, array[lo].latitude);
			for (int i = lo; i < hi; i++) { 
				CensusGroup cg = array[i];
				Rectangle nextRec = new Rectangle(array[i].longitude, array[i].longitude,
						array[i].latitude, array[i].latitude);
				rec = rec.encompass(nextRec);
			}
			return rec;
		}
	}
}
