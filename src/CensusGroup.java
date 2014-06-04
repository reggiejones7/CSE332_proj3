

/**
 * holds data for one line from a census data file.
 * the latitude and longitude, and the population at that point.
 */
public class CensusGroup {
	public int   population;
	public float realLatitude;
	public float latitude;
	public float longitude;
	public CensusGroup(int pop, float lat, float lon) {
		population = pop;
		realLatitude = lat;
		latitude   = mercatorConversion(lat);
		//latitude = lat; //uncomment this line when testing
		longitude  = lon;
	}
	
	private float mercatorConversion(float lat){
		float latpi = (float)(lat * Math.PI / 180);
		float x = (float)Math.log(Math.tan(latpi) + 1 / Math.cos(latpi));
		//System.out.println(lat + " -> " + x);
		return x;
	}
}
