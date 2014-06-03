
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * Reggie Jones & Tristan Riddell
 * Project 3
 * TA: Hye In Kim
 * 
 * PopulationQuery accepts a filename of a file with government population census data
 *  and x/y coordinates of a grid as program arguments,
 *   then a set of four coordinates of the grid from the command line.
 * It prints the population within the given rectangle, and the percentage
 * of the total population of the rectangle compared to the entire data file.
 *
 */

public class PopulationQuery {
	// next four constants are relevant to parsing
	public static final int TOKENS_PER_LINE  = 7;
	public static final int POPULATION_INDEX = 4; // zero-based indices
	public static final int LATITUDE_INDEX   = 5;
	public static final int LONGITUDE_INDEX  = 6;
	public static final int LOCK_THREADS = 4;
	
    public static ForkJoinPool fjPool = new ForkJoinPool();
	
	// the number of columns and rows in a query
	private static int xBuckets;
	private static int yBuckets;
	
	//the data parsed from the file given by client in preprocess
	private static CensusData cd;
	//version number the client specifies
	private static int version;
	
	//largest rectangle from cd, e.g. map of the U.S.
	private static Rectangle map;
	
	//grid used for v3-5
	private static int[][] grid;
	
	
	// parse the input file into a large array held in a CensusData object
	public static CensusData parse(String filename) {
		CensusData result = new CensusData();
		
        try {
            @SuppressWarnings("resource")
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
    
    /**
     * preprocess accepts the runtime arguments as parameters and creates a 
     * Rectangle object which represents the four corners of the map 
     * according to the provided data file.
     * @param filename the name of the file of data to use
     * @param x the number of columns in the grid 
     * @param y the number of rows in the grid
     * @param versionNum the method of processing and data parsing to use
     * @throws InterruptedException 
     */
    public static void preprocess(String filename, int x, int y, int versionNum) throws InterruptedException {
    	//The arguments to the preprocess method are the same arguments that should be passed via
    	//the command line to the main method in PopulationQuery, only parsed into their datatypes 
    	//and not as Strings. This method should read the file and prepare any data structures 
    	//necessary for the given version of the program. 
    	xBuckets = x;
		yBuckets = y;
		version = versionNum;
		cd = parse(filename);
		
		//find corners-store in map
		ParallelCorners pc = null;
		if (version == 1 || version == 3) {
			pc = new ParallelCorners(0, cd.data_size, cd.data, cd.data_size);
		} else if (version == 2 || version == 4 || version == 5) {
			pc = new ParallelCorners(0, cd.data_size, cd.data);
		}
		Rectangle r = fjPool.invoke(pc);
		map = r;
		
		//additional preprocessing steps for v3-4
		if (version == 3 || version == 4 || version == 5) {
			BuildGridData data = new BuildGridData(xBuckets, yBuckets, cd.data, map);
			BuildGrid bg = new BuildGrid(0, cd.data_size, data);
			if (version == 3 || version == 4) {

				if (version == 3) {
					grid = bg.sequentialBuildGrid(0, cd.data_size, data);
				}
				if (version == 4) {
					grid = fjPool.invoke(bg);
				}
			} else {
				int[][] tempGrid = new int[yBuckets][xBuckets];
				Lock[][] lockGrid = new Lock[yBuckets][xBuckets];
				for (int lockY = 0; lockY < yBuckets; lockY++) {
					for (int lockX = 0; lockX < xBuckets; lockX++) {
						lockGrid[lockY][lockX] = new ReentrantLock();
					}
				}
				LockBuildGrid[] lb = new LockBuildGrid[4];
				for (int i = 0; i < LOCK_THREADS; i++) {
					lb[i] = new LockBuildGrid(lockGrid, tempGrid, data, 
							(i*cd.data_size)/LOCK_THREADS, ((i+1) * cd.data_size) / LOCK_THREADS);
					lb[i].start();
				}
				for (int i = 0; i < LOCK_THREADS; i++) {
					lb[i].join();
				}
				grid = tempGrid;
					
				//still tippin on four fours.. << rofl
			}
			

			//step 2- update grid with single pass over the grid.
			//every position in grid will now hold the total population for all
			//positions that are neither farther east nor farther south
			for (int y1 = 0; y1 < yBuckets; y1++) {
				for (int x1 = 0; x1 < xBuckets; x1++) {
					if (y1 != 0 || x1 != 0) { //leave first element as is
						if (y1 == 0) { 
							//top row
							grid[y1][x1] += grid[y1][x1 - 1]; 
						} else if (x1 == 0) {
							//left column
							grid[y1][x1] += grid[y1 - 1][x1]; 
						} else {
							grid[y1][x1] += grid[y1 - 1][x1] + grid[y1][x1 - 1] - grid[y1 - 1][x1 - 1];
						}
					}
				}
			}
		}
    }
    
    /**
     * singleInteraction accepts 4 ints which represent a rectangle within the grid
     * and returns the population within that rectangle, and the percentage 
     * of the total population within that rectangle.
     * @param west the farthest west column of the rectangle
     * @param south the farthest south row of the rectangle
     * @param east the farthest east column of the rectangle
     * @param north the farthest north row of the rectangle
     * @returns a pair of values: the population in the rectangle,
     * and the percentage of the population within the rectangle.
     */
    public static Pair<Integer, Float> singleInteraction(int west, int south,
    													 int east, int north) {
    
    	checkQueryInputs(west, south, east, north);
   
    	Rectangle queryRec = makeRectangle(west, south, east, north);
    	
		ParallelQuery pq = new ParallelQuery(0, cd.data_size, cd.data, queryRec);
		Pair<Integer, Integer> p = null;
		if (version == 1) {
			p = pq.sequentialPopulation(0, cd.data_size);
		} else if (version == 2) {
			p = fjPool.invoke(pq);
		} else if (version == 3 || version == 4 || version == 5) {
			// all minus one because user is using 1 based index
			int gridWest = west - 1;
			int gridEast = east - 1;
			int gridNorth = yBuckets - north;
			int gridSouth = yBuckets - south;
			
			//to find population: pop of d = d - b - c + a 
			//in the example of [a b
			//					 c d]
			int population = grid[gridSouth][gridEast];
			if (gridNorth - 1 >= 0) {
				population -= grid[gridNorth - 1][gridEast];
			}
			if (gridWest - 1 >= 0) {
				population -= grid[gridSouth][gridWest - 1];
			}
			if ((gridNorth - 1 >= 0) && (gridWest - 1 >= 0)) {
				population += grid[gridNorth - 1][gridWest - 1];
			}
			//totalPopulation will just be the bottom right entry of the grid
			int totalPopulation = grid[yBuckets - 1][xBuckets - 1];
			p = new Pair<Integer, Integer>(totalPopulation, population);
		}
		
		float percent = (p.getElementB() / (float) p.getElementA())* 100;
		return new Pair<Integer, Float>(p.getElementB(), percent);
    }
    
    //todo; remove this and calls to this and replace with Rectangle.makeRectangle()
    //returns rectangle inside of the map. the west south east north are integers
    // that represent the coordinates in the map
    private static Rectangle makeRectangle(int west, int south, int east, int north) {
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
    
    /*
     * the main method handles accepting the runtime arguments 
     * and calling the necessary methods to parse the datafile correctly.
     * queries the user for coordinates, then prints the population 
     * and percent of total population of the rectangle.
     * 
     * argument 1: file name for input data: pass this to parse
	 * argument 2: number of x-dimension buckets
	 * argument 3: number of y-dimension buckets
	 * argument 4: -v1, -v2, -v3, -v4, or -v5
     */

	public static void main(String[] args) throws InterruptedException {
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
				
				System.out.println("Please give west, south, east, north coordinates "
						+ "of your query rectangle:");
				query = s.nextLine();
				inputs = query.split(" ");
			} catch (NumberFormatException e) {
				System.exit(1);
			}
		}
		s.close();
	}
}
