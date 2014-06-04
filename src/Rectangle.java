import java.awt.geom.Point2D;


/**
 * Reggie Jones & Tristan Riddell
 * 
 * Rectangle is an object that contains four float values representing 
 * the location of each corner.
 */
public class Rectangle {
        // invariant: right >= left and top >= bottom (i.e., numbers get bigger as you move up/right)
        // note in our census data longitude "West" is a negative number which nicely matches bigger-to-the-right
	public float left;
	public float right;
	public float top;
	public float bottom;
	
	/**
	 * constructor
	 * @param l left edge
	 * @param r right edge
	 * @param t top edge
	 * @param b bottom edge
	 */
	public Rectangle(float l, float r, float t, float b) {
		left   = l;
		right  = r;
		top    = t;
		bottom = b;
	}
	
	/**
	 * encompass finds a rectangle that is the smallest rectangle 
	 * containing this and that
	 * @param that the rectangle to compare to this
	 * @returns a new rectangle that is the smallest rectangle containing this and that
	 * 
	 */
	public Rectangle encompass(Rectangle that) {
		return new Rectangle(Math.min(this.left,   that.left),
						     Math.max(this.right,  that.right),             
						     Math.max(this.top,    that.top),
				             Math.min(this.bottom, that.bottom));
	}
	
	/**
	 * states whether the given point is inside of this rectangle
	 * @param point the point to check 
	 * @returns true if the point is in the rectangle, else false.
	 */
	public boolean insideRectangle(Point2D.Float point) {
		if (point.x >= left && point.x <= right && point.y >= bottom && point.y <= top) {
			return true;
		}

		return false;
	}
	
	/**
	 * returns rectangle inside of the map. the west south east north are integers
     * that represent the integer coordinates in the map. Useful for making
     * a query rectangle.
	 * @param west west coordinate
	 * @param south south coordinate
	 * @param east east coordinate
	 * @param north north coordinate
	 * @param map Rectangle of the country
	 * @param xBuckets number of columns user specified
	 * @param yBuckets number of rows user specified
	 * @return Rectangle of 
	 */
	public static Rectangle makeRectangle(int west, int south, int east, int north,
											Rectangle map, int xBuckets, int yBuckets) {
    	float longMin = map.left;
    	float longMax = map.right;
    	float latMin = map.bottom;
    	float latMax = map.top;
    	
    	float xBucketSize = Math.abs((longMax - longMin) / xBuckets);
		float yBucketSize = Math.abs((latMax - latMin) / yBuckets);
		float left = (west - 1) * xBucketSize + longMin;
		float right = east * xBucketSize + longMin;
		float bottom = (south - 1) * yBucketSize + latMin;
		float top = north * yBucketSize + latMin;
		
		return new Rectangle(left, right, top, bottom);
    }
	
	/**
	 * returns the String of the coordinates of a Rectangle
	 * @return String representation of the rectangle
	 */
	public String toString() {
		return "[left=" + left + " right=" + right + " top=" + top + " bottom=" + bottom + "]";
	}
}
