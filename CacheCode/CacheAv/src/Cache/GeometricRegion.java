package Cache;

public class GeometricRegion {

	public final static int CIRCLE   = 0;
	public final static int HALFPLAN = 1;
	public final static int CONE	 = 2;
	
	public final static int CONE_LEFT  = 1;
	public final static int CONE_RIGHT = 2;
	public final static int ORIGIN     = 0;
	
	public final static int FIRST  = 0;
	public final static int SECOND = 1;

	
	private int type;
	
	private long [][] points;
	
	private double radius;

	private int ori;
	
	private long maxDistanceFromOrigin;
	
	private int state;
	
	GeometricRegion(long [] origin, double radius) {
		this.type = CIRCLE;
		points = new long[1][2];
		points[0][0] = origin[0];
		points[0][1] = origin[1];
		this.radius = radius;
	}
	
	GeometricRegion(long[] origin, long[] left, long[] right) {
		this.type = CONE;
		points = new long[3][2];
		points[ORIGIN][0] = origin[0];
		points[ORIGIN][1] = origin[1];
		
		points[CONE_LEFT][0] = left[0];
		points[CONE_LEFT][1] = left[1];
		
		points[CONE_RIGHT][0] = right[0];
		points[CONE_RIGHT][1] = right[1];
	}
	
	GeometricRegion(long[] origin, long[] left, long[] right, long maxDist) {
		this(origin,left,right);
		this.maxDistanceFromOrigin = maxDist;
	}
	
	GeometricRegion(long[] a, long[] b) {
		this.type = HALFPLAN;
		points = new long[2][2];
		
		points[FIRST][0] = a[0];
		points[FIRST][1] = a[1];
		
		points[SECOND][0] = b[0];
		points[SECOND][1] = b[1];
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public int getState() {
		return this.state;
	}
	
	public void setOri(int ori) {
		this.ori = ori;
	}
	
	public int getOri() {
		return this.ori;
	}
	
	public long getType() {
		return this.type;
	}
	
	public long[] getOrigin() {
		long[] ret;
		switch(this.type) {
		case CIRCLE:
			ret = this.points[ORIGIN];
			break;
		case HALFPLAN:
			ret = null;
			break;
		case CONE:
			ret = this.points[ORIGIN];
			break;
		default:
			ret = null;
			break;
		}
		return ret;
	}
	
	public long[] getFirstCoord() {
		long[] ret;
		switch(this.type) {
		case CIRCLE:
			ret = null;
			break;
		case HALFPLAN:
			ret = this.points[FIRST];
			break;
		case CONE:
			ret = this.points[CONE_LEFT];
			break;
		default:
			ret = null;
			break;
		}
		return ret;
	}
	
	public long[] getSecondCoord() {
		long[] ret;
		switch(this.type) {
		case CIRCLE:
			ret = null;
			break;
		case HALFPLAN:
			ret = this.points[SECOND];
			break;
		case CONE:
			ret = this.points[CONE_RIGHT];
			break;
		default:
			ret = null;
			break;
		}
		return ret;
	}
	
	public long getMaxDistanceFromOrigin() {
		return this.maxDistanceFromOrigin;
	}
	
	public double getRadius() {
		double ret;
		switch(this.type) {
		case CIRCLE:
			ret = this.radius;
			break;
		case HALFPLAN:
			ret = -1;
			break;
		case CONE:
			ret = -1;
			break;
		default:
			ret = -1;
			break;
		}
		return ret;	
	}
}
