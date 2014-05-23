
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.concurrent.ForkJoinPool;

/**
 * 
 * Reggie Jones & Tristan Riddell
 * Project 3
 * TA: Hye In Kim
 * 
 * --fill in class description...--
 *
 */

public class PopulationQuery {
	// next four constants are relevant to parsing
	public static final int TOKENS_PER_LINE  = 7;
	public static final int POPULATION_INDEX = 4; // zero-based indices
	public static final int LATITUDE_INDEX   = 5;
	public static final int LONGITUDE_INDEX  = 6;
	
    public static ForkJoinPool fjPool = new ForkJoinPool();
	static final int SEQUENTIAL_CUTOFF= 1000;

	
	
	// the number of columns and rows 
	private static int xBuckets;
	private static int yBuckets;
	//the data parsed from the file given by client in preprocess
	private static CensusData cd;
	
	// next four fields are the metrics of the CensusData
	private static float longMin;
	private static float longMax;
	private static float latMin;
	private static float latMax;
	
	
	// parse the input file into a large array held in a CensusData object
	public static CensusData parse(String filename) {
		CensusData result = new CensusData();
		
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(filename));
            
            // Skip the first line of the file
            // After that each line has 7 comma-separated numbers (see constants above)
            // We want to skip the first 4, the 5th is the population (an int)
            // and the 6th and 7th are latitude and longitude (floats)
            // If the population is 0, then the line has latitude and longitude of +.,-.
            // which cannot be parsed as floats, so that's a special case
            //   (we could fix this, but noisy data is a fact of life, more fun
            //    to process the real data as provided by the government)
            
            String oneLine = fileIn.readLine(); // skip the first line

            // read each subsequent line and add relevant data to a big array
            while ((oneLine = fileIn.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if(tokens.length != TOKENS_PER_LINE)
                	throw new NumberFormatException();
                int population = Integer.parseInt(tokens[POPULATION_INDEX]);
                if(population != 0)
                	result.add(population,
                			   Float.parseFloat(tokens[LATITUDE_INDEX]),
                		       Float.parseFloat(tokens[LONGITUDE_INDEX]));
            }

            fileIn.close();
        } catch(IOException ioe) {
            System.err.println("Error opening/reading/writing input or output file.");
            System.exit(1);
        } catch(NumberFormatException nfe) {
            System.err.println(nfe.toString());
            System.err.println("Error in file format");
            System.exit(1);
        }
        return result;
	}
	
	// System exits from incorrect command line arguments
    private static void argError(String arg) {
    	System.err.println("Incorrect arg \"" + arg + "\"");
    	System.err.println("Usage: <filename> <x-dimension> <y-dimension> [ -v1 | -v2 | -v3 | -v4 | -v5 ]");
		System.exit(1);
    }
    
    //Prints and error with a given direction and exits
    private static void queryInputError(String direction) {
    	System.err.println("Error in " + direction + " coordinate");
    	System.exit(1);
    }
    
    //error if bad query input
    private static void checkQueryInputs(int west, int south, int east, int north) {
		if (west < 1 || west > xBuckets) {
			queryInputError("west");
		} else if (south < 1 || south > yBuckets) {
			queryInputError("south");
		} else if (east < west || east > xBuckets) {
			queryInputError("east");
		} else if (north < south || north > yBuckets) {
			queryInputError("north");
		}
    }
    
    
    public static void preprocess(String filename, int x, int y, int versionNum) {
    	//The arguments to the preprocess method are the same arguments that should be passed via
    	//the command line to the main method in PopulationQuery, only parsed into their datatypes 
    	//and not as Strings. This method should read the file and prepare any data structures 
    	//necessary for the given version of the program. 
    	xBuckets = x;
		yBuckets = y;
		cd = parse(filename);
    	
		if (cd.data_size < SEQUENTIAL_CUTOFF) {
		
		longMin = cd.data[0].longitude;
		longMax = cd.data[0].longitude; 
		latMin = cd.data[0].latitude;
		latMax = cd.data[0].latitude;
		for (int i = 1; i < cd.data_size; i++) {
			CensusGroup cg = cd.data[i];
			longMin = Math.min(cg.longitude, longMin);
			longMax = Math.max(cg.longitude, longMax);
			latMin = Math.min(cg.latitude, latMin);
			latMax = Math.max(cg.latitude, latMax);
		}
			
		} else {
			ParallelCorners pc = new ParallelCorners(0, cd.data_size, cd.data);
			Corner c = fjPool.invoke(pc);
			longMin = c.longMin;
			longMax = c.longMax;
			latMin = c.latMin;
			latMax = c.latMax;		
		}
		//Rectangle us = new Rectangle(longMin, longMax, latMax, longMin);
    }
    
    //returns Pair<total population of query, percent of pop in query / total pop of country>
    public static Pair<Integer, Float> singleInteraction(int west, int south, 
    													 int east, int north) {
    	//The arguments to the singleInteraction method are the arguments that are passed to the 
    	//program when it prompts for query input. This method should determine the population 
    	//size and the population percentage of the U.S. given the parameters, just as your program 
    	//should when given integers at the prompt.
    	checkQueryInputs(west, south, east, north);
    	
    	float xBucketSize = Math.abs((longMax - longMin) / xBuckets);
		float yBucketSize = Math.abs((latMax - latMin) / yBuckets);
		float left = (west - 1) * xBucketSize + longMin;
		float right = east * xBucketSize + longMin;
		float bottom = (south - 1) * yBucketSize + latMin;
		float top = north * yBucketSize + latMin;
		Rectangle rec = new Rectangle(left, right, top, bottom);
		if (cd.data_size < SEQUENTIAL_CUTOFF) {
			int totalPop = 0;
			int recPop = 0;
			for (int i = 0; i < cd.data_size; i++) {
				CensusGroup group = cd.data[i];
				Point2D.Float point = new Point2D.Float(group.longitude, group.latitude);
				if (rec.insideRectangle(point)) {
					recPop += group.population;
				}
				totalPop += group.population;
		}
		float percent = (recPop / (float) totalPop) * 100 ;
    	return new Pair<Integer, Float>(recPop, percent);
		} else {
			ParallelQuery pq = new ParallelQuery(0, cd.data_size, cd.data, rec);
			Pair<Integer, Integer> p = fjPool.invoke(pq);
			float percent = (p.getElementB() / (float) p.getElementA())* 100;
			return new Pair<Integer, Float>(p.getElementB(), percent);
		}
    }
    
	// argument 1: file name for input data: pass this to parse
	// argument 2: number of x-dimension buckets
	// argument 3: number of y-dimension buckets
	// argument 4: -v1, -v2, -v3, -v4, or -v5
	public static void main(String[] args) {
		if (args.length != 4) {
			argError("length");
		}
		try {
			Integer.parseInt(args[1]);
			Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			argError("x or y dimension not an integer");
		}
		if (!Pattern.matches("-v[1-5]", args[3])) {
			argError(args[3]);
		}
		
		/* pre process */
		String filename = args[0];
		int xBucket = Integer.parseInt(args[1]);
		int yBucket = Integer.parseInt(args[2]);
		int version = Integer.parseInt(args[3].substring(args[3].length() - 1));
		preprocess(filename, xBucket, yBucket, version);
		
		System.out.println("Please give west, south, east, north coordinates "
							+ "of your query rectangle:");
		Scanner s = new Scanner(System.in);
		String query = s.nextLine();
		
		String[] inputs = query.split(" ");
		while (inputs.length == 4) {
			try {
				int west = Integer.parseInt(inputs[0]);
				int south = Integer.parseInt(inputs[1]);
				int east = Integer.parseInt(inputs[2]);
				int north = Integer.parseInt(inputs[3]);
			
				Pair<Integer, Float> pair = singleInteraction(west, south, east, north);
				System.out.println("population of rectangle: " + pair.getElementA());
				System.out.printf("percent of total population: %.2f\n", pair.getElementB());
				
				query = s.nextLine();
				inputs = query.split(" ");
			} catch (NumberFormatException e) {
				System.exit(1);
			}
		}
		
		//note:to combine v1 and v2 think about making them both parallel but
		// have v1 just pass in the sequential cutoff of the size of the cd so it never does the parallel case
		
		s.close();
	}
}
