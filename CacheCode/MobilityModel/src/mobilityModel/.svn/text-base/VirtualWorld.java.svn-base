/**
 * 
 */
package mobilityModel;

/**
 * Utility class containing basic utility operations
 * with avatar's coordinates inside the virtual world map
 * @author Legtchenko Sergey
 */
public abstract class VirtualWorld {
	
	private static final long ZERO  = 0;
	private static final int XCOORD = 0;
	private static final int YCOORD = 1;
	private static final int ZCOORD = 2;
	
	/*
	 * used to obtain zipf distributed random longs
	 */
	private static final PowerLaw powerlaw = new PowerLaw();
	
	/* 
	 * maximum coordinate constants
	 */
	public static final long XMAX = Globals.mapSize;
	public static final long XMIN = VirtualWorld.ZERO;
	
	public static final long YMAX = Globals.mapSize;
	public static final long YMIN = VirtualWorld.ZERO;
	
	/* this is a two dimensional world*/
	public static final long ZMAX = VirtualWorld.ZERO;
	public static final long ZMIN = VirtualWorld.ZERO;
	
	/*directions*/
	public static final int LEFT  = 0;
	public static final int RIGHT = 1;

	
	/**
	 * given a dimension, returns a uniformly distributed random coordinate between the min and the max for that dimension
	 * (currently the map is two-dimensional and square, so there is no difference between x and y dimensions)
	 * @param dim a dimension (0 for x axis, 1 for y and 2 for z)
	 * @return the random coordinate
	 */
	private static long randomCoord(int dim) {
		
		long ret;
		switch(dim) {
		case 0:
			ret = Globals.r.nextLong(XMAX);
			break;
		case 1:
			ret = Globals.r.nextLong(YMAX);
			break;
		case 2:
			ret = Globals.r.nextLong(ZMAX);
			break;
		default:
			ret = VirtualWorld.ZERO;
			break;
		}
		
		return ret;
	}
	
	/**
	 * random with gaussian distribution
	 * @return a random according to gaussian distribution
	 */
    private static double gaussian() {
    	
        double U = Globals.r.nextDouble();
        double V = Globals.r.nextDouble();
        return Math.sin(2 * Math.PI * V) * Math.sqrt((-2 * Math.log(1 - U)));
    }
	
    /**
     * returns a uniformly distributed random long between min and max
     * @param min the minimal bound
     * @param max the maximal bound
     * @return the generated random
     */
	private static long constraintedRandomCoord(long min, long max) {
		
		long interval = max - min;
		long rand = Globals.r.nextLong(interval);
		
		return min + rand;
	}
	
    /**
     * returns a random long with gaussian distribution between min and max
     * @param min the minimal bound
     * @param max the maximal bound
     * @return the generated random
     */
	private static long constraintedGaussianRandomCoord(long min, long max) {
		
		long mid = (max + min) / 2;
		long rand = (long) ((Globals.zoneSize / 5) * gaussian());
		long value;
		
		value = mid + rand;
		
		if (value < 0) {
			value = Globals.mapSize + value;
		} 
		if (value > Globals.mapSize) {
			value = value % Globals.mapSize;
		}
		return value;
	}
	
    /**
     * returns a random long with zipf distribution between min and max
     * @param min the minimal bound
     * @param max the maximal bound
     * @return the generated random
     */
	private static long[] constraintedZipfRandomCoord(long xmin, long xmax, long ymin, long ymax) {
		int maxMod;
		long xmid, ymid, mod;
		double angle;
		long[] coord;
		
		coord = new long[3];
		maxMod =(int)(xmax - xmin)/2;
		mod = VirtualWorld.powerlaw.getRandInt(maxMod-1);
		xmid = (xmin + xmax)/2;
		ymid = (ymin + ymax)/2;
		angle = VirtualWorld.powerlaw.getRand()*2*Math.PI;
		coord[0] = xmid + (long)(mod * Math.cos(angle));
		coord[1] = ymid + (long)(mod * Math.sin(angle));
	
		return coord;
	}

	/**
	 * computes next step coordinates of avatars according to the current coordinates, 
	 * the angular direction, the acceleration, the maximal speed and the time delta 
	 * between coordinates.
	 * @param base the current coordinates
	 * @param angle the angular direction
	 * @param a the acceleration
	 * @param maxSpeed the maximal speed
	 * @param t the time delta
	 * @return the next step coordinates
	 */
	public static long [] move(long [] base, double angle, int a, int maxSpeed, long t) {
		long [] coords = new long[3];
		double sITime = new Long(t).doubleValue() / 1000; /*from milliseconds to seconds*/
		double speed = new Integer(a).doubleValue()*sITime;/*calculating the new speed*/
		double norm;
		
		speed = (speed>maxSpeed)?maxSpeed:speed;
		norm = (double)(speed * (sITime / 2));
		/*
		 * transforming polar coordinates into cartesian coordinates
		 */
		coords[0] = new Double(norm * Math.cos(angle)).longValue();
		coords[1] = new Double(norm * Math.sin(angle)).longValue();
		coords[2] = 0;
		coords = VirtualWorld.sumCoords(base,coords);
		return coords;
	}
	
	/**
	 * inverts the current direction
	 * @param direction the current direction
	 * @return the inverted direction
	 */
	public static int invert(int direction) {
		return 1 - direction;
	}
	
	/**
	 * given two points, calculates angular direction
	 * @param pointA source point coordinates
	 * @param pointB destination point coordinates
	 * @return the angular direction
	 */
	public static double calculateDirection(long [] pointA, long [] pointB) {
		return Math.atan2(pointB[1]-pointA[1], pointB[0]-pointA[0]);
	}
	
	/**
	 * calculates coordinate constraints that fit with a zone of interest
	 * @param zoneOfInterest coordinates of the upper left corner of the square interest zone
	 * @param zoneSize the size of the zone.
	 * @return a set of geometric constraints
	 */
	public static long[][] setConstraints(long [] zoneOfInterest, long zoneSize) {
		long [][] constraints = new long[3][2];
		
		constraints[0][0] = zoneOfInterest[0];
		constraints[0][1] = zoneOfInterest[0] + zoneSize;
		constraints[1][0] = zoneOfInterest[1];
		constraints[1][1] = zoneOfInterest[1] + zoneSize;
		
		return constraints;
	}
	
	/**
	 * computes a random set of coordinates respecting given constraints, and with uniform distribution.
	 * @param constraints a set of constraints representing a bi-dimensional geometric region
	 * @return constrainted random coordinates
	 */
	public static long[] getConstraintedRandomLocation(long [][]constraints) {
		
		if (constraints.length != 3 || constraints[0].length != 2) {
			return getRandomLocation();
		} else {
			long [] location = new long[3];
			location[XCOORD] = VirtualWorld.constraintedRandomCoord(constraints[XCOORD][0], constraints[XCOORD][1]);
			location[YCOORD] = VirtualWorld.constraintedRandomCoord(constraints[YCOORD][0], constraints[YCOORD][1]);
			location[ZCOORD] = 0; //VirtualWorld.constraintedRandomCoord(constraints[ZCOORD][0], constraints[ZCOORD][1]);
			
			return location;
		}
	}
	
	/**
	 * computes a random set of coordinates respecting given constraints, and with zipf distribution.
	 * @param constraints a set of constraints representing a bi-dimensional geometric region
	 * @return constrainted random coordinates
	 */
	public static long[] getConstraintedZipfLocation(long [][]constraints) {
		
		if (constraints.length != 3 || constraints[0].length != 2) {
			throw new IllegalStateException();
		} else {
			long [] location = new long[3];
			location = VirtualWorld.constraintedZipfRandomCoord(constraints[XCOORD][0], constraints[XCOORD][1],constraints[YCOORD][0], constraints[YCOORD][1]);
			if (location[XCOORD] < 0 || location[YCOORD] < 0) {
				System.err.println("VirtualWorld.getConstraintedZipfLocation Error: location ("+location[XCOORD]+","+location[YCOORD] +") out of the map");
				System.exit(1);
			}
			return location;
		}	
	}
	
	/**
	 * computes a random set of coordinates respecting given constraints, and with gaussian distribution.
	 * @param constraints a set of constraints representing a bi-dimensional geometric region
	 * @return constrainted random coordinates
	 */
	public static long[] getConstraintedGaussianLocation(long [][]constraints) {
		
		if (constraints.length != 3 || constraints[0].length != 2) {
			throw new IllegalStateException();
		} else {
			long [] location = new long[3];
			location[XCOORD] = VirtualWorld.constraintedGaussianRandomCoord(constraints[XCOORD][0], constraints[XCOORD][1]);
			location[YCOORD] = VirtualWorld.constraintedGaussianRandomCoord(constraints[YCOORD][0], constraints[YCOORD][1]);
			location[ZCOORD] = 0; //VirtualWorld.constraintedRandomCoord(constraints[ZCOORD][0], constraints[ZCOORD][1]);
			if (location[XCOORD] < 0 || location[YCOORD] < 0) {
				System.err.println("Error: location ("+location[XCOORD]+","+location[YCOORD] +") out of the map");
				System.exit(1);
			}
			return location;
		}	
	}
	
	/**
	 * computes a random set of coordinates between 0 and max, with uniform distribution.
	 * @param max the maximal authorized value for a coordinate
	 * @return constrainted random coordinates
	 */
	public static long[] getSimpleConstraintedRandomLocation(long max) {
		
		long [] location = new long[3];
		location[XCOORD] = VirtualWorld.constraintedRandomCoord(0, max);
		location[YCOORD] = VirtualWorld.constraintedRandomCoord(0, max);
		location[ZCOORD] = 0;
		
		return location;
	}
	
	/**
	 * given a and b points, computes the ab vector (substracts the two coordinates).
	 * @param a first point
	 * @param b second point
	 * @return (a-b)
	 */
	public static long[] substract(long[] a, long[] b) {
		long[] result = new long[3];
		
		result[0] = a[0] - b[0];
		result[1] = a[1] - b[1];
		
		return result;
	}
	
	/**
	 * calculates the sign of the angle (ab, ac)
	 * @param a center point coordinate
	 * @param b first point coordinate
	 * @param c second point coordinate
	 * @return 1 if the angle is positive, -1 if it is negative
	 */
	public static int simpleAngleSign(long [] a, long [] b, long [] c) {
		long det = (b[0] - a[0]) * (c[1] - a[1]) - (c[0] - a[0]) * (b[1] - a[1]) ;
		int sign = 0;
		
		if (det < 0) {
			sign = -1;
		}
		
		if (det > 0) {
			sign = 1;
		}
		
		return sign;
	}
	
	/**
	 * generates a random coordinate on the map
	 * @return
	 */
	private static long[] getRandomLocation() {
		
		long [] location = new long[3];
		
		location[XCOORD] = VirtualWorld.randomCoord(XCOORD);
		location[YCOORD] = VirtualWorld.randomCoord(YCOORD);
		location[ZCOORD] = 0;//VirtualWorld.randomCoord(ZCOORD);
		
		return location;
	}
	
	/**
	 * computes the square of the distance between a and b
	 * @param a first point 
	 * @param b second point
	 * @return
	 */
	private static long simpleSqDistance(long [] a, long [] b) {
		long xvect = b[0] - a[0];
		long yvect = b[1] - a[1];
		
		return xvect*xvect + yvect*yvect;
	}
	
	/**
	 * returns the distance between a and b
	 * @param a first point
	 * @param b second point
	 * @return distance between a and b
	 */
	public static long simpleDistance(long [] a, long [] b) {
		return (long)Math.sqrt(simpleSqDistance(a,b));
	}

	/**
	 * determines if a and b are the same coordinate
	 * @param a first coordinate
	 * @param b second coordinate
	 * @return true if a and b are the same
	 */
	public static boolean samePosition(long [] a, long [] b) {
		boolean yes = (a[0] == b[0])&&(a[1] == b[1]);
	
		return yes;
	}
	
	/**
	 * determines if the given coordinate is zero
	 * @param a the coordinate to check
	 * @return true if it is zero
	 */
	public static boolean isNullCoord(long[]a) {
		return a[0] == 0 && a[1] == 0;
	}
	
	/**
	 * sums a and b coordinates with a modulo
	 * @param a the first coordinate
	 * @param b the second coordinate
	 * @return a+b
	 */
	public static long[] sumCoords(long[] a, long[] b) {
		long [] sum = new long[3];
		
		sum[0] = (a[0] + b[0]) % Globals.mapSize;
		sum[1] = (a[1] + b[1]) % Globals.mapSize;
		sum[2] = (a[2] + b[2]) % Globals.mapSize; 
		
		return sum;
	}
	
	/**
	 * converts the given coordinate to a string representation
	 * @param coord the coordinate to convert
	 * @return the string representation of the coordinate
	 */
	public static String coordToString(long[]coord) {
		return "(" + coord[0] + "," + coord[1] + "," + coord[2] + ")";
	}
}