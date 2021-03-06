/**
 * 
 */
package Test;

import java.util.HashMap;

import Cache.CacheModule;
import Cache.CacheData;
import Cache.NeighborProxy;
import junit.framework.TestCase;

/**
 * @author xavier
 *
 */
public class TestCacheAv extends TestCase {
	
	NeighborProxy n1,n2,n3,n4,n5,n6,n7;
	CacheModule testfifo;
	
	int cacheSize = 5;
	private long[] proxyCoord = new long[3];
	private double proxyRadius = 0;
	
	
	public void testCacheAv() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		testfifo = new CacheModule(null, null, cacheSize);
		System.out.println("Taille cache		:" + testfifo.getCache().size() + "/" + cacheSize);
		System.out.println("Taille cacheInfo	:" + testfifo.getCacheInfo().size() + "/" + cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
	}


	public void testAddCache() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		n1 = new NeighborProxy(proxyCoord, proxyRadius, 1, 1);
		n2 = new NeighborProxy(proxyCoord, proxyRadius, 2, 2);
		testfifo.AddCache(n1);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertEquals(testfifo.getCache().size(), 1);
		
		testfifo.AddCache(n2);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 2);
		testfifo.AddCache(n1);
		testfifo.RmCache(n2);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertEquals(testfifo.getCache().size(), 1);
		assertEquals(testfifo.getCacheInfo().get(n1.getId()).getPosition(), 1);
		
		testfifo.RmCache(n1);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertFalse(testfifo.IsInCache(n1));
		assertFalse(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 0);
	}

	
	public void testRmCache() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		n1 = new NeighborProxy(proxyCoord, proxyRadius, 1, 1);
		testfifo.AddCache(n1);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertEquals(testfifo.getCache().size(), 1);
		
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertEquals(testfifo.getCache().size(), 1);
		assertEquals(testfifo.getCacheInfo().get(n1.getId()).getPosition(), 1);
		
		testfifo.RmCache(n1);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertFalse(testfifo.IsInCache(n1));
		assertEquals(testfifo.getCache().size(), 0);
		
	}
	
	
	public void testSelectNode() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		n1 = new NeighborProxy(proxyCoord, proxyRadius, 1, 1);
		n2 = new NeighborProxy(proxyCoord, proxyRadius, 2, 2);
		testfifo.AddCache(n2);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 1);
		
		testfifo.AddCache(n1);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 2);
		
		NeighborProxy res = testfifo.SelectNode();
		assertEquals(res, n2);
		
		testfifo.RmCache(n2);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertEquals(testfifo.getCache().size(), 1);
		assertEquals(testfifo.getCacheInfo().get(n1.getId()).getPosition(), 1);
		
		testfifo.RmCache(n1);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertFalse(testfifo.IsInCache(n1));
		assertFalse(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 0);
		
	}

	public void testComplex() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		n1 = new NeighborProxy(proxyCoord, proxyRadius, 1, 1);
		n2 = new NeighborProxy(proxyCoord, proxyRadius, 2, 2);
		n3 = new NeighborProxy(proxyCoord, proxyRadius, 3, 3);
		n4 = new NeighborProxy(proxyCoord, proxyRadius, 4, 4);
		n5 = new NeighborProxy(proxyCoord, proxyRadius, 5, 5);
		n6 = new NeighborProxy(proxyCoord, proxyRadius, 6, 6);
		n7 = new NeighborProxy(proxyCoord, proxyRadius, 7, 7);
		
		
		/* Test1: roulement normal */
		testfifo.AddCache(n2);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 1);
		
		testfifo.AddCache(n1);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 2);
		
		testfifo.AddCache(n3);
		testfifo.AddCache(n4);
		testfifo.AddCache(n5);
		testfifo.ShowCache();
		
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertTrue(testfifo.IsInCache(n3));
		assertTrue(testfifo.IsInCache(n4));
		assertTrue(testfifo.IsInCache(n5));
		assertEquals(testfifo.getCache().size(), 5);
		
		
		testfifo.AddCache(n6);
		assertFalse(testfifo.IsInCache(n2));
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n3));
		assertTrue(testfifo.IsInCache(n4));
		assertTrue(testfifo.IsInCache(n5));
		assertTrue(testfifo.IsInCache(n5));
		assertEquals(testfifo.getCache().size(), 5);
		
		
		testfifo.AddCache(n7);
		assertFalse(testfifo.IsInCache(n2));
		assertFalse(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n3));
		assertTrue(testfifo.IsInCache(n4));
		assertTrue(testfifo.IsInCache(n5));
		assertTrue(testfifo.IsInCache(n5));
		assertTrue(testfifo.IsInCache(n6));
		assertEquals(testfifo.getCache().size(), 5);
	
		
		testfifo.RmCache(n3);
		testfifo.RmCache(n4);
		testfifo.RmCache(n5);
		testfifo.RmCache(n6);
		testfifo.RmCache(n7);
		
		/* Test1: roulement suppression en cours */
		
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		testfifo.AddCache(n2);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 1);
		
		testfifo.AddCache(n1);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 2);
		
		testfifo.AddCache(n3);
		testfifo.AddCache(n4);
		testfifo.AddCache(n5);
		testfifo.ShowCache();
		
		testfifo.RmCache(n4);
		testfifo.RmCache(n3);
		
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertTrue(testfifo.IsInCache(n5));
		assertEquals(testfifo.getCache().size(), 3);
		
		testfifo.AddCache(n4);
		
		
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertTrue(testfifo.IsInCache(n4));
		assertTrue(testfifo.IsInCache(n5));
		assertEquals(testfifo.getCache().size(), 4);
		
		
		testfifo.AddCache(n6);
		assertTrue(testfifo.IsInCache(n2));
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n4));
		assertTrue(testfifo.IsInCache(n5));
		assertTrue(testfifo.IsInCache(n5));
		assertEquals(testfifo.getCache().size(), 5);
		
		
		testfifo.AddCache(n7);
		assertFalse(testfifo.IsInCache(n2));
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n4));
		assertTrue(testfifo.IsInCache(n5));
		assertTrue(testfifo.IsInCache(n5));
		assertTrue(testfifo.IsInCache(n6));
		assertEquals(testfifo.getCache().size(), 5);
	
		testfifo.RmCache(n3);
		testfifo.RmCache(n4);
		testfifo.RmCache(n5);
		testfifo.RmCache(n6);
		testfifo.RmCache(n7);
		
		
	}

	
	public void testSetGetCache() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		n1 = new NeighborProxy(proxyCoord, proxyRadius, 1, 1);
		n2 = new NeighborProxy(proxyCoord, proxyRadius, 2, 2);
		testfifo.AddCache(n1);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertEquals(testfifo.getCache().size(), 1);
		
		testfifo.AddCache(n2);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 2);
		
		HashMap<Integer, NeighborProxy> test = new HashMap<Integer, NeighborProxy>();
		testfifo.setCache(test);
		assertEquals(testfifo.getCache(), test);
		
	}
	

	
	public void testSetGetCacheInfo() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		n1 = new NeighborProxy(proxyCoord, proxyRadius, 1, 1);
		n2 = new NeighborProxy(proxyCoord, proxyRadius, 2, 2);
		testfifo.AddCache(n1);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertEquals(testfifo.getCache().size(), 1);
		
		testfifo.AddCache(n2);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 2);
		
		HashMap<Integer, CacheData> test = new HashMap<Integer, CacheData>();
		testfifo.setCacheInfo(test);
		assertEquals(testfifo.getCacheInfo(), test);
		
	}

	
	
	public void testSetGetCacheSize() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		testfifo.setCacheSize(4);
		assertEquals(testfifo.getCacheSize(), 4);
	}
	
	
	
	public void testSetStrategieCache() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		testfifo.setStrategieCache(2);
		assertEquals(testfifo.getStrategieCache(), 2);
	}
	
	public void testNeighborProxyFindNearest(){
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		long[] proxyCoord1 = {3,3,3};
		long[] proxyCoord2 = {2,2,3};
		NeighborProxy res;
		
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		
		n1 = new NeighborProxy(proxyCoord1, proxyRadius, 1, 1);
		n2 = new NeighborProxy(proxyCoord2, proxyRadius, 2, 2);
		n3 = new NeighborProxy(proxyCoord, proxyRadius, 3, 3);
		
		testfifo.AddCache(n1);
		testfifo.ShowCache();
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertEquals(testfifo.getCache().size(), 1);
		
		testfifo.AddCache(n2);
		testfifo.AddCache(n3);
		testfifo.ShowCache();
		res = testfifo.NeighborProxyFindNearest(testfifo.getCache(), n3);
		assertEquals(res, n2);
		
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertTrue(testfifo.IsInCache(n1));
		assertTrue(testfifo.IsInCache(n2));
		assertEquals(testfifo.getCache().size(), 3);
		
	}
	
	
	public void testsearchCacheNeighborLimit(){
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		
		long[] proxyCoord1 = {3,3,3};
		long[] proxyCoord2 = {2,2,3};
		long[] proxyCoord3 = {1,1,2};
		long[] proxyCoord4 = {5,5,5};
		long[] proxyCoord5 = {0,0,0};
		long[] proxyCoord6 = {4,5,4};
		NeighborProxy res;
		
		n1 = new NeighborProxy(proxyCoord1, proxyRadius, 1, 1);
		n2 = new NeighborProxy(proxyCoord2, proxyRadius, 2, 2);
		n3 = new NeighborProxy(proxyCoord3, proxyRadius, 3, 3);
		n4 = new NeighborProxy(proxyCoord4, proxyRadius, 4, 4);
		n5 = new NeighborProxy(proxyCoord5, proxyRadius, 5, 5);
		n6 = new NeighborProxy(proxyCoord6, proxyRadius, 6, 6);
		
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
				
		
		testfifo.AddCache(n4);

		NeighborProxy resultat = testfifo.searchCacheNeighborLimit(proxyCoord);
		assertEquals(resultat, null);

		
		testfifo.AddCache(n2);
		testfifo.AddCache(n3);
		
		resultat = testfifo.searchCacheNeighborLimit(proxyCoord5);
		assertEquals(resultat, n3);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),3);
		
		
		
	}
	
	public void testsearchCacheNeighbor(){
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		
		long[] proxyCoord1 = {3,3,3};
		long[] proxyCoord2 = {2,2,3};
		long[] proxyCoord3 = {1,1,2};
		long[] proxyCoord4 = {5,5,5};
		long[] proxyCoord5 = {0,0,0};
		long[] proxyCoord6 = {4,5,4};
		NeighborProxy res;
		
		HashMap<Integer,NeighborProxy> voisin = new HashMap<Integer,NeighborProxy>();
		
		n1 = new NeighborProxy(proxyCoord1, proxyRadius, 1, 1);
		n2 = new NeighborProxy(proxyCoord2, proxyRadius, 2, 2);
		n3 = new NeighborProxy(proxyCoord3, proxyRadius, 3, 3);
		n4 = new NeighborProxy(proxyCoord4, proxyRadius, 4, 4);
		n5 = new NeighborProxy(proxyCoord5, proxyRadius, 5, 5);
		n6 = new NeighborProxy(proxyCoord6, proxyRadius, 6, 6);
		
		testfifo = new CacheModule(null, null, cacheSize);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),0);
		
		voisin.put(n1.getId(), n1);
		voisin.put(n6.getId(), n6);
		
		
		testfifo.AddCache(n4);
		NeighborProxy resultat = testfifo.searchCacheNeighbor(proxyCoord5, voisin);
		resultat = testfifo.searchCacheNeighbor(proxyCoord5, voisin);
		assertEquals(resultat, null);

		
		testfifo.AddCache(n2);
		testfifo.AddCache(n3);
		
		resultat = testfifo.searchCacheNeighbor(proxyCoord5, voisin);
		assertEquals(resultat, n3);
		assertEquals(testfifo.getCache().size(),testfifo.getCacheInfo().size());
		assertEquals(testfifo.getCache().size(),3);
		
		
		
	}
		
}


