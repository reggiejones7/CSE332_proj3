import java.awt.geom.Point2D;
import java.util.concurrent.RecursiveTask;
public class ParallelQuery extends RecursiveTask<Pair<Integer, Integer>> {
	
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
	protected Pair<Integer, Integer> compute() {

		if(hi - lo == 1) {
			CensusGroup group = array[lo];
			Point2D.Float point = new Point2D.Float(group.longitude, group.latitude);
			if (rec.insideRectangle(point)) {
				return new Pair<Integer,Integer>(group.population, group.population);
			}
			return new Pair<Integer, Integer>(group.population, 0);
		}
		
		ParallelQuery left = new ParallelQuery(lo, (hi+lo)/2, array, rec);
		ParallelQuery right = new ParallelQuery((hi+lo)/2, hi, array, rec);
		left.fork();
		Pair<Integer, Integer> rightA = right.compute();
		Pair<Integer, Integer> leftA = left.join();
		
		int newTotal = rightA.getElementA() + leftA.getElementA();
		int newGroup = rightA.getElementB() + leftA.getElementB();
		return new Pair<Integer, Integer>(newTotal, newGroup);	
	}

}
