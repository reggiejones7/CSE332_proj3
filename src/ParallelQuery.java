import java.awt.geom.Point2D;
import java.util.concurrent.RecursiveTask;
public class ParallelQuery extends RecursiveTask<popHold> {
	
	int lo, hi;
	Rectangle rec;
	CensusGroup[] array;
	
	public ParallelQuery(int lo, int hi, CensusGroup[] arr, Rectangle rec) {
		this.array = arr;
		this.lo = lo;
		this.hi = hi;
		this.rec = rec;
	}

	@Override
	protected popHold compute() {

		if(hi - lo == 1) {
			CensusGroup group = array[lo];
			Point2D.Float point = new Point2D.Float(group.longitude, group.latitude);
			if (rec.insideRectangle(point)) {
				return new popHold(group.population, group.population);
			}
			return new popHold(group.population, 0);
		}
		
		ParallelQuery left = new ParallelQuery(lo, (hi+lo)/2, array, rec);
		ParallelQuery right = new ParallelQuery((hi+lo)/2, hi, array, rec);
		left.fork();
		popHold rightA = right.compute();
		popHold leftA = left.join();
		
		int newTotal = rightA.totalPop + leftA.totalPop;
		int newGroup = rightA.recPop + leftA.recPop;
		return new popHold(newTotal, newGroup);	
	}

}
