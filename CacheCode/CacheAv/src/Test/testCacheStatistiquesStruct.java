package Test;

import Cache.CacheStatistiquesStruct;
import junit.framework.TestCase;

public class testCacheStatistiquesStruct extends TestCase {

	public void testMoy(){
		CacheStatistiquesStruct cur1 = new CacheStatistiquesStruct(2, 2, 2, 2, 2);
		CacheStatistiquesStruct new1 = new CacheStatistiquesStruct(4, 5, 6, 7, 8);
		
		CacheStatistiquesStruct res = null;
		System.out.println(cur1.toString());
		cur1.moy(new1);
		System.out.println(cur1.toString());
		System.out.println("+++++++++++++++");
		
		
		
		double angleA, angleB;
		
		angleA = Angle((double)0,(double)3);
		angleB = Angle((double)-4,(double)0);
		System.out.println("angleA: " + angleA + " angleB: " + angleB);
	}
	
	private double Angle(double x,double y){
		double angle;
		angle = Math.toDegrees(java.lang.Math.atan2(y,x));
		if ( angle < 0){
			angle = angle + 360;
		}
		return angle;
	}
}
