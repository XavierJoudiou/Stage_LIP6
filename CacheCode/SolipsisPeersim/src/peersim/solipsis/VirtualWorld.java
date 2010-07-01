/**
 * 
 */
package peersim.solipsis;

import peersim.core.CommonState;
import peersim.solipsis.Globals;
import peersim.solipsis.VirtualWorld;
import gps.util.PowerLaw;

/**
 * @author Legtchenko Sergey
 *
 */
public class VirtualWorld {
	
	private static final long ZERO  = 0;
	private static final int XCOORD = 0;
	private static final int YCOORD = 1;
	private static final int ZCOORD = 2;
	
	private static final PowerLaw powerlaw = new PowerLaw();
	private static int counter  = 0;
	private static int location = 0;
	
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

	
	public static long randomCoord(int dim) {
		
		long ret;
		switch(dim) {
		case 0:
			ret = CommonState.r.nextLong(XMAX);
			break;
		case 1:
			ret = CommonState.r.nextLong(YMAX);
			break;
		case 2:
			ret = (ZMAX == 0)?0:CommonState.r.nextLong(ZMAX);
			break;
		default:
			ret = VirtualWorld.ZERO;
			break;
		}
		
		return ret;
	}
	
    private static double gaussian() {
    	
        double U = Math.random();
        double V = Math.random();
        return Math.sin(2 * Math.PI * V) * Math.sqrt((-2 * Math.log(1 - U)));
    }
	
	private static long constraintedRandomCoord(long min, long max) {
		
		long interval = max - min;
		long rand = CommonState.r.nextLong(interval);
		
		return min + rand;
	}
	
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
	
	private static long[] constraintedZipfRandomCoord(long xmin, long xmax, long ymin, long ymax) {
		int maxMod;
		long xmid, ymid, mod;
		double angle;
		long[] coord;
		
		coord = new long[3];
		maxMod =(int)(xmax - xmin)/2;
		mod = VirtualWorld.powerlaw.getRandInt(maxMod);
		xmid = (xmin + xmax)/2;
		ymid = (ymin + ymax)/2;
		angle = VirtualWorld.powerlaw.getRand()*2*Math.PI;
		coord[0] = xmid + (long)(mod * Math.cos(angle));
		coord[1] = ymid + (long)(mod * Math.sin(angle));
	
		return coord;
	}

	
//	public static long[] normalize(long [] coords) {
//		if(coords[0] > Globals.mapSize) 
//			coords[0] = Globals.mapSize;
//		if(coords[0] < 0) 
//			coords[0] = 0;
//		if(coords[1] > Globals.mapSize) 
//			coords[1] = Globals.mapSize;
//		if(coords[1] < 0) 
//			coords[1] = 0; 
//		coords[2] = 0;
//		
//		return coords;
//	}
	
	public static long [] move(long [] base, double angle, int a, int maxSpeed, long t) {
		long [] coords = new long[3];
		double sITime = new Long(t).doubleValue() / 1000; /*from milliseconds to seconds*/
		double speed = new Integer(a).doubleValue()*sITime;
		double norm;
		
		speed = (speed>maxSpeed)?maxSpeed:speed;
		norm = (double)(speed * (sITime / 2));
		coords[0] = new Double(norm * Math.cos(angle)).longValue();
		coords[1] = new Double(norm * Math.sin(angle)).longValue();
		coords[2] = 0;
		coords = VirtualWorld.sumCoords(base,coords);
//		coords = normalize(coords);
		return coords;
	}
	
	
	public static int invert(int direction) {
		return 1 - direction;
	}
	
	public static double calculateDirection(long [] pointA, long [] pointB) {
		return Math.atan2(pointB[1]-pointA[1], pointB[0]-pointA[0]);
	}
	
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
	
	public static long[][] setConstraints(long [] zoneOfInterest, long zoneSize) {
		long [][] constraints = new long[3][2];
		
		constraints[0][0] = zoneOfInterest[0];
		constraints[0][1] = zoneOfInterest[0] + zoneSize;
		constraints[1][0] = zoneOfInterest[1];
		constraints[1][1] = zoneOfInterest[1] + zoneSize;
		
		return constraints;
	}
	
	public static long[] getConstraintedZipfLocation(long [][]constraints) {
		
		if (constraints.length != 3 || constraints[0].length != 2) {
			throw new IllegalStateException();
		} else {
			long [] location = new long[3];
			location = VirtualWorld.constraintedZipfRandomCoord(constraints[XCOORD][0], constraints[XCOORD][1],constraints[YCOORD][0], constraints[YCOORD][1]);
//			location[YCOORD] = VirtualWorld.constraintedZipfRandomCoord(constraints[YCOORD][0], constraints[YCOORD][1]);
//			location[ZCOORD] = 0; //VirtualWorld.constraintedRandomCoord(constraints[ZCOORD][0], constraints[ZCOORD][1]);
			if (location[XCOORD] < 0 || location[YCOORD] < 0) {
				System.err.println("Error: location ("+location[XCOORD]+","+location[YCOORD] +") out of the map");
				System.exit(1);
			}
			return location;
		}	
	}
	
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
	
	public static long[] getSimpleConstraintedRandomLocation(long max) {
		
		long [] location = new long[3];
		location[XCOORD] = VirtualWorld.constraintedRandomCoord(0, max);
		location[YCOORD] = VirtualWorld.constraintedRandomCoord(0, max);
		location[ZCOORD] = 0;
		
		return location;
	}
	
//	public static int angleSign(long [] a, long [] b, long [] c, long [] ref) {
//		long [] realA = (a==ref)?a:VirtualWorld.adjustCoordToModulo(a,ref);
//		long [] realB = (b==ref)?b:VirtualWorld.adjustCoordToModulo(b,ref);
//		long [] realC = (c==ref)?c:VirtualWorld.adjustCoordToModulo(c,ref);
//		return simpleAngleSign(realA,realB,realC);
//	}
	
	public static long[] substract(long[] a, long[] b) {
		long[] result = new long[3];
		
		result[0] = a[0] - b[0];
		result[1] = a[1] - b[1];
		
		return result;
	}
	
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
	
	private static long[] getRandomLocation() {
		
		long [] location = new long[3];
		
		location[XCOORD] = VirtualWorld.randomCoord(XCOORD);
		location[YCOORD] = VirtualWorld.randomCoord(YCOORD);
		location[ZCOORD] = 0;//VirtualWorld.randomCoord(ZCOORD);
		
		return location;
	}
	
	private static long simpleSqDistance(long [] a, long [] b) {
		long xvect = b[0] - a[0];
		long yvect = b[1] - a[1];
		
		return xvect*xvect + yvect*yvect;
	}
	
	public static double simpleDistance(long [] a, long [] b) {
		return Math.sqrt(simpleSqDistance(a,b));
	}

	public static long[] realRelativeCoord(long[] distant, long[] local) {
		return VirtualWorld.moduloModificationRule(local, distant);
	}
	private static long[] moduloModificationRule(long[] ref, long[] distant) {
		long[] subjective = new long[3];

		subjective[0] = distant[0];
		subjective[1] = distant[1];

		if (distant[0] > ref[0]) {
			if (distant[0] - ref[0] > Globals.mapSize / 2) {
				subjective[0] -= Globals.mapSize;
			} 
		} else {
			if (ref[0] - distant[0] > Globals.mapSize / 2) {
				subjective[0] += Globals.mapSize;
			}	
		}

		if (distant[1] > ref[1]) {
			if (distant[1] - ref[1] > Globals.mapSize / 2) {
				subjective[1] -= Globals.mapSize;
			}
		} else {
			if (ref[1] - distant[1] > Globals.mapSize / 2) {
				subjective[1] += Globals.mapSize;
			}	
		}
		
		return subjective;
	}
	
	private static double sqDistance(double [] a, double [] b) {
		double xvect = b[0] - a[0];
		double yvect = b[1] - a[1];
		
		return xvect*xvect + yvect*yvect;
	}
	
	public static double distance(double [] a, double [] b) {
		return Math.sqrt(sqDistance(a,b));
	}
	
//	public static int [] moduloCoeffs(long [] referential, long [] a) {
//		long currentDist;
//		long minValue = Long.MAX_VALUE;
//		long [] current = new long[2];
//		int [] coefs = new int[2];
//	
//		for(int i = -1; i < 2; i++) {
//			current[0] = a[0] + i*Globals.mapSize;
//			for(int j = -1; j < 2; j++) {
//				current[1] = a[1] + j*Globals.mapSize;
//				currentDist = simpleDistance(current,referential);
//				if(currentDist < minValue) {
//					minValue = currentDist;
//					coefs[0] = i;
//					coefs[1] = j;
//				}
//			}
//		}	
//		return coefs;		
//	}
	
//	public static long[] adjustCoordToModulo(long [] referential, long []a) {
//		long currentDist;
//		long minValue = Long.MAX_VALUE;
//		long [] current = new long[2];
//		long [] minCoords = new long[3];
//	
//		for(int i = -1; i < 2; i++) {
//			current[0] = a[0] + i*Globals.mapSize;
//			for(int j = -1; j < 2; j++) {
//				current[1] = a[1] + j*Globals.mapSize;
//				currentDist = simpleDistance(current,referential);
//				if(currentDist < minValue) {
//					minValue = currentDist;
//					minCoords[0] = current[0];
//					minCoords[1] = current[1];
//				}
//			}
//		}	
//		return minCoords;
//	}
	
	public static boolean samePosition(long [] a, long [] b) {
		boolean yes = (a[0] == b[0])&&(a[1] == b[1]);
	
		return yes;
	}
	
	public static boolean isNullCoord(long[]a) {
		return a[0] == 0 && a[1] == 0;
	}
	
	public static long[] sumCoords(long[] a, long[] b) {
		long [] sum = new long[3];
		
		sum[0] = (a[0] + b[0]) % Globals.mapSize;
		sum[1] = (a[1] + b[1]) % Globals.mapSize;
		sum[2] = (a[2] + b[2]) % Globals.mapSize; 
		
		return sum;
	}
	
	public static String coordToString(long[]coord) {
		return "(" + coord[0] + "," + coord[1] + "," + coord[2] + ")";
	}
	
//	public static long distance(long [] a, long [] b) {
//		long currentDist;
//		long minValue = Long.MAX_VALUE;
//		long [] current = new long[2];
//		
//		for(int i = -1; i < 2; i++) {
//			current[0] = a[0] + i*Globals.mapSize;
//			for(int j = -1; j < 2; j++) {
//				current[1] = a[1] + j*Globals.mapSize;
//				currentDist = simpleSqDistance(current,b);
//				if(currentDist < minValue) {
//					minValue = currentDist;
//				}
//			}
//		}
//		return (long)Math.sqrt((double)minValue);
//	}
//		long absc = a[0] - b[0];
//		long ord = a[1] - b[1];
//		double normal = (double)(absc*absc + ord*ord);
//		double modulo;
//		long nextModXm, nextModYm, nextModXp, nextModYp, nextModX, nextModY;
//		
//		nextModXm = a[0] - Globals.mapSize;
//		nextModYm = a[1] - Globals.mapSize;
//		nextModXp = a[0] + Globals.mapSize;
//		nextModYp = a[1] + Globals.mapSize;
//		
//		if(nextModXm < b[0] && b[0] < a[0] && nextModYm < b[1] && b[1] < a[1]) {
//			nextModX = nextModXm;
//			nextModY = nextModYm;
//		} else {
//			nextModX = nextModXp;
//			nextModY = nextModYp;
//		}
//
//		absc = nextModX - b[0];
//		ord = nextModY - b[1];
//		modulo = (double)(absc*absc + ord*ord);
//		return (long) Math.sqrt( ((normal>modulo)?modulo:normal) );
//	}
}
