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
	}
}
