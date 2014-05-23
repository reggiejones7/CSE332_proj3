import java.util.concurrent.RecursiveTask;
public class ParallelCorners extends RecursiveTask<Rectangle> {
	
	int lo, hi;
	CensusGroup[] array;
	
	public ParallelCorners(int lo, int hi, CensusGroup[] arr) {
		this.array = arr;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	protected Rectangle compute() {

		if(hi - lo == 1) {
			return new Rectangle(array[lo].longitude, array[lo].longitude, array[lo].latitude, array[lo].latitude);

		}
		ParallelCorners left = new ParallelCorners(lo, (hi+lo)/2, array );
		ParallelCorners right = new ParallelCorners((hi+lo)/2, hi, array);
		left.fork();
		Rectangle rightA = right.compute();
		Rectangle leftA = left.join();
		
		float newLeft = Math.min(rightA.left, leftA.left);
		float newRight = Math.max(rightA.right, leftA.right);
		float newBottom = Math.min(rightA.bottom,  leftA.bottom);
		float newTop = Math.max(rightA.top, leftA.top);
		return new Rectangle(newLeft, newRight, newTop, newBottom);
	}

}
