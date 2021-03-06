/**
 * 
 */
package Cache;

/**
 * @author xavier
 *
 */
public class Outils {
	
	
	/* VirtualWorld */
	private static long simpleSqDistance(long [] a, long [] b) {
		long xvect = b[0] - a[0];
		long yvect = b[1] - a[1];
		
		return xvect*xvect + yvect*yvect;
	}
	
	public static double simpleDistance(long [] a, long [] b) {
		return Math.sqrt(simpleSqDistance(a,b));
	}
	

}
