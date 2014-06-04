import java.util.concurrent.RecursiveTask;

/**
 * Reggie Jones & Tristan Riddell
 *  
 * FindCorners traverses the input data and returns a rectangle containing 
 * the minimum and maximum longitude and latitude found in the
 * input array of CensusGroups.
 * 
 * Uses either parallelism or sequentialism depending on how it is constructed-
 * if you want to explicitly use sequentialismthen add the length of the input array
 * as the cutoff argument in the constructor. Otherwise, dont specify this argument.
 */
public class FindCorners extends RecursiveTask<Rectangle> {

	private static final int SEQUENTIAL_CUTOFF = 1000;
	
	private int lo, hi;
	private CensusGroup[] input;
	private int sequentialCutoff;
	
	/**
	 * Overloaded Constructor
     * @param lo the lowest bound to use of the array
	 * @param hi the highest bound to use of the array
	 * @param input the array we are parsing the data of
	 */
	public FindCorners(int lo, int hi, CensusGroup[] input) {
		this(lo, hi, input, SEQUENTIAL_CUTOFF);
	}
	
	/**
	 * Constructor
	 * @param lo the lowest bound to use of the array
	 * @param hi the highest bound to use of the array
	 * @param input the array we are parsing the data of
	 * @param cutoff the sequential cut off value to use 
	 */
	public FindCorners(int lo, int hi, CensusGroup[] input, int cutoff) {
		this.input = input;
		this.lo = lo;
		this.hi = hi;
		sequentialCutoff = cutoff;
	}
	
	/**
	 * does the work of finding the corners of the input array
	 * @return Rectangle that represents the rectangle bound by 4 corners
	 */
	@Override
	protected Rectangle compute() {
		if (hi - lo > sequentialCutoff) {
			FindCorners left = new FindCorners(lo, (hi+lo)/2, input );
			FindCorners right = new FindCorners((hi+lo)/2, hi, input);
			left.fork();
			Rectangle rightA = right.compute();
			Rectangle leftA = left.join();

			return rightA.encompass(leftA);
		} else {
			Rectangle rec = new Rectangle(input[lo].longitude, input[lo].longitude,
											input[lo].latitude, input[lo].latitude);
			for (int i = lo; i < hi; i++) { 
				CensusGroup cg = input[i];
				Rectangle nextRec = new Rectangle(cg.longitude, cg.longitude,
						cg.latitude, cg.latitude);
				rec = rec.encompass(nextRec);
			}
			return rec;
		}
	}
}
