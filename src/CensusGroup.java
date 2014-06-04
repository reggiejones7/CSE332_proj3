

/**
 * holds data for one line from a census data file.
 * the latitude and longitude, and the population at that point.
 */
public class CensusGroup {
	public int   population;
	public float realLatitude;
	public float latitude;
	public float longitude;
	
	/**
	 * Constructor. Use this for general purpose cases.
	 * Uses mercatorConversion for the latitude
	 */
	public CensusGroup(int pop, float lat, float lon) {
		this(pop, lat, lon, false);
	}
	
	/**
	 * overloaded Constructor. Use this for testing purposed
	 * ignored mercatorConversion for the latitude
	 * @param pop population 
	 * @param lat latitude 
	 * @param lon longitude
	 * @param test true if used for testing purposes,
	 */
	public CensusGroup(int pop, float lat, float lon, boolean test) {
		population = pop;
		realLatitude = lat;
		latitude   = mercatorConversion(lat);
		longitude  = lon;
		
		if (test) {
			latitude = lat;
		}
	}
	
	private float mercatorConversion(float lat){
		float latpi = (float)(lat * Math.PI / 180);
		float x = (float)Math.log(Math.tan(latpi) + 1 / Math.cos(latpi));
		//System.out.println(lat + " -> " + x);
		return x;
	}
}
