import java.util.concurrent.RecursiveTask;
public class ParallelCorners extends RecursiveTask<Corner> {
	
	int lo, hi;
	CensusGroup[] array;
	
	public ParallelCorners(int lo, int hi, CensusGroup[] arr) {
		this.array = arr;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	protected Corner compute() {

		if(hi - lo == 1) {
			return new Corner(array[lo].longitude, array[lo].longitude, array[lo].latitude, array[lo].latitude);

		}
		ParallelCorners left = new ParallelCorners(lo, (hi+lo)/2, array );
		ParallelCorners right = new ParallelCorners((hi+lo)/2, hi, array);
		left.fork();
		Corner rightA = right.compute();
		Corner leftA = left.join();
		
		float longMin = Math.min(rightA.longMin, leftA.longMin);
		float longMax = Math.max(rightA.longMax, leftA.longMax);
		float latMin = Math.min(rightA.latMin,  leftA.latMin);
		float latMax = Math.max(rightA.latMax, leftA.latMax);
		return new Corner(longMin, longMax, latMin, latMax);
	}

}
