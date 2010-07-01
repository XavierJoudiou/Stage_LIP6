package cache.tests;

import java.util.Arrays;
import java.util.HashMap;

import cache.utils.CacheTest;
import cache.utils.Node;
import junit.framework.TestCase;

public class TestCacheTest extends TestCase {
	CacheTest testfifo,testLRU,testMFU;
	long[] coord = {0,0,0};
	Node n1, n2, n3, n4, n5, n6, n7;
	
	public void testCacheTest() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		testfifo = new CacheTest(null,0);
		testLRU = new CacheTest(null,1);
		testMFU = new CacheTest(null,2);
		
		n1 = new Node(coord,1,0);
		
	}

	public void testAddNode() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		testfifo = new CacheTest(null,0);
		n1 = new Node(coord,1,0);
		testfifo.AddNode(n1);
		
		assertEquals(testfifo.getCache().size(),1);
		testfifo.AddNode(n1);
		
		assertEquals(testfifo.getCache().size(),1);
		
		n2 = new Node(coord,2,0);
		n3 = new Node(coord,3,0);
		n4 = new Node(coord,4,0);
		n5 = new Node(coord,5,0);
		n6 = new Node(coord,6,0);
		testfifo.AddNode(n2);
		testfifo.AddNode(n3);
		testfifo.AddNode(n4);
		testfifo.AddNode(n5);
		assertEquals(testfifo.getCache().size(),5);
		testfifo.AddNode(n6);
		assertEquals(testfifo.getCache().size(),5);
		testfifo.RmNode(n2);
		assertEquals(testfifo.getCache().size(),4);
		testfifo.RmNode(n1);
		testfifo.RmNode(n3);
		testfifo.RmNode(n4);
		testfifo.RmNode(n5);
		testfifo.RmNode(n6);
		assertEquals(testfifo.getCache().size(),0);

	}

	public void testRmNode() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		testfifo = new CacheTest(null,0);
		n1 = new Node(coord,1,0);				
		n2 = new Node(coord,2,0);
		
		testfifo.AddNode(n2);
		testfifo.AddNode(n1);
		assertEquals(testfifo.getCache().size(),2);
	
		testfifo.RmNode(n1);
		testfifo.RmNode(n2);
		assertEquals(testfifo.getCache().size(),0);
	}

	public void testShowCache() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		testfifo = new CacheTest(null,0);
		n1 = new Node(coord,1,0);	
		testfifo.AddNode(n1);
		testfifo.ShowCache();
		testfifo.RmNode(n1);		
	}

	public void testOrganizeCache() {
	}

	public void testSelectDeletNode() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		/*Fifo*/
		System.out.println("FIFO");
		System.out.println("---------------------------");

		testfifo = new CacheTest(null,0);
		n1 = new Node(coord,1,0);
		n2 = new Node(coord,2,0);
		n3 = new Node(coord,3,0);
		n4 = new Node(coord,4,0);
		n5 = new Node(coord,5,0);
		n6 = new Node(coord,6,0);
		testfifo.AddNode(n2);
		testfifo.AddNode(n1);
		testfifo.AddNode(n3);
		testfifo.AddNode(n4);
		testfifo.AddNode(n5);
		testfifo.AddNode(n6);
		assertEquals(testfifo.getCache().size(),5);
		testfifo.consultNode(n4, 2);
		
		testfifo.RmNode(n1);
		testfifo.RmNode(n2);
		testfifo.RmNode(n3);
		testfifo.RmNode(n4);
		testfifo.RmNode(n5);
		testfifo.RmNode(n6);
		assertEquals(testfifo.getCache().size(),0);

		/*LRU*/
		System.out.println("LRU");
		System.out.println("---------------------------");
		testLRU = new CacheTest(null,1);
		n1 = new Node(coord,1,0);
		n2 = new Node(coord,2,0);
		testLRU.AddNode(n2);
		testLRU.AddNode(n1);
		testLRU.ShowCache();
		testLRU.consultNode(n1, 5); 
		testLRU.ShowCache();
		testLRU.consultNode(n2, 2);
		testLRU.ShowCache();
		Node res;
		res = testLRU.SelectDeletNode();
		assertEquals(res, n1);
		assertEquals(testLRU.getCache().size(),2);
		
		testfifo.RmNode(n1);
		testfifo.RmNode(n2);
		assertEquals(testfifo.getCache().size(),0);
		
		/*MFU*/
		System.out.println("MFU");
		System.out.println("---------------------------");
		testMFU = new CacheTest(null,2);
		n1 = new Node(coord,1,0);
		n2 = new Node(coord,2,0);
		n3 = new Node(coord,3,0);
		n4 = new Node(coord,4,0);
		n5 = new Node(coord,5,0);
		n6 = new Node(coord,6,0);
		testMFU.AddNode(n2);
				
		
		testMFU.AddNode(n1);
		testMFU.consultNode(n1, 3);
		testMFU.consultNode(n2, 2);
		res = testMFU.SelectDeletNode();
		assertEquals(res,n2);
		testMFU.AddNode(n3);
		testMFU.AddNode(n4);
		
		testMFU.consultNode(n2, 5);
		testMFU.consultNode(n1, 5);
		testMFU.consultNode(n3, 5);
		testMFU.consultNode(n4, 5);

		testMFU.AddNode(n5);
		testMFU.AddNode(n6);
		assertEquals(testMFU.getCache().size(),5);
		
		testMFU.RmNode(n1);
		testMFU.RmNode(n2);
		testMFU.RmNode(n3);
		testMFU.RmNode(n4);
		testMFU.RmNode(n5);
		testMFU.RmNode(n6);
		assertEquals(testMFU.getCache().size(),0);

	}

	public void testConsultNode() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
	}

	public void testGetCache() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");		
	}

	public void testSetCache() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		testfifo = new CacheTest(null,0);
		HashMap<Integer,Node>  cachetest = new HashMap<Integer,Node>();
		
		testfifo.setCache(cachetest);
		assertEquals(testfifo.getCache(), cachetest);
		
	}

	public void testGetCacheSize() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		testfifo = new CacheTest(null,0);

		testfifo.setCacheSize(7);
		int res = testfifo.getCacheSize();	
		assertEquals(res, 7);
		
		testfifo.setCacheSize(5);
		res = testfifo.getCacheSize();	
		assertEquals(res, 5);
		
		
		
	}

	public void testSetCacheSize() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		testfifo = new CacheTest(null,0);

		testfifo.setCacheSize(6);
		assertEquals(testfifo.getCacheSize(), 6);
	}
	
	public void testScenarioComplexe(){
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		
		int nb_nodes = 10;
		Node[] tab_node = new Node[nb_nodes];
		
		/*Fifo*/
		System.out.println("FIFO");
		System.out.println("---------------------------");
		
		testfifo = new CacheTest(null,0);
		
		/* Création des nœuds */
		for (int i=0; i<nb_nodes;i++){
			tab_node[i] = new Node(coord,i+1,0);
		}
		
		assertEquals(testfifo.getCache().size(),0);
		
		for (int i=0;i<testfifo.getCacheSize();i++){
			testfifo.AddNode(tab_node[i]);
		}
		System.out.println("Ajout de " +  testfifo.getCacheSize() +" nœuds");
		assertEquals(testfifo.getCache().size(),testfifo.getCacheSize());
		testfifo.ShowCache();
		
		for (int i=testfifo.getCacheSize();i<(testfifo.getCacheSize()+2);i++){
			testfifo.AddNode(tab_node[i]);
		}
		System.out.println("Ajout de  2  nœuds");
		assertEquals(testfifo.getCache().size(),testfifo.getCacheSize());
		assertFalse(testfifo.getCache().containsValue(tab_node[0]));
		assertFalse(testfifo.getCache().containsValue(tab_node[1]));
		assertTrue(testfifo.getCache().containsValue(tab_node[2]));
		testfifo.ShowCache();
		
		testfifo.AddNode(tab_node[0]);
		for (int i=(testfifo.getCacheSize()+2);i<nb_nodes;i++){
			testfifo.AddNode(tab_node[i]);
		}		
		assertTrue(testfifo.getCache().containsValue(tab_node[nb_nodes-1]));
		assertFalse(testfifo.getCache().containsValue(tab_node[2]));
		
		/* Suppression des nœuds*/
		for (int i=0; i<nb_nodes;i++){
			testfifo.RmNode(tab_node[i]);
		}


		/*LRU*/
		System.out.println("LRU");
		System.out.println("---------------------------");
		
		testLRU = new CacheTest(null,1);
		
		/* Création des nœuds */
		for (int i=0; i<nb_nodes;i++){
			tab_node[i] = new Node(coord,i+1,0);
		}
		
		assertEquals(testLRU.getCache().size(),0);
		
		for (int i=0;i<testLRU.getCacheSize();i++){
			testLRU.AddNode(tab_node[i]);
		}
		System.out.println("Ajout de " +  testLRU.getCacheSize() +" nœuds");
		assertEquals(testLRU.getCache().size(),testLRU.getCacheSize());
		testLRU.ShowCache();
		testLRU.consultNode(tab_node[0], 10);
		testLRU.consultNode(tab_node[2], 10);
		testLRU.consultNode(tab_node[1], 10);
		
		for (int i=testLRU.getCacheSize();i<(testLRU.getCacheSize()+2);i++){
			testLRU.AddNode(tab_node[i]);
		}
		System.out.println("Ajout de  2  nœuds");
		assertEquals(testLRU.getCache().size(),testLRU.getCacheSize());
		assertFalse(testLRU.getCache().containsValue(tab_node[3]));
		assertFalse(testLRU.getCache().containsValue(tab_node[4]));
		assertTrue(testLRU.getCache().containsValue(tab_node[2]));
		testLRU.ShowCache();
		
		testLRU.AddNode(tab_node[0]);
		for (int i=(testLRU.getCacheSize()+2);i<nb_nodes;i++){
			testLRU.AddNode(tab_node[i]);
		}		
		assertTrue(testLRU.getCache().containsValue(tab_node[nb_nodes-1]));
		assertFalse(testLRU.getCache().containsValue(tab_node[2]));
		
		/* Suppression des nœuds*/
		for (int i=0; i<nb_nodes;i++){
			testLRU.RmNode(tab_node[i]);
		}
		
	}

}
