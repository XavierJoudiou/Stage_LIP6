/**
 * 
 */
package cache.tests;

import cache.utils.CacheTest;
import cache.utils.Node;

/**
 * @author xavier
 *
 */
public class CacheMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Node n1, n2, n3, n4, n5, n6, n7;
		
		long[] coord = {0,0,0};
		/* Test Fifo */
//		CacheTest test = new CacheTest(null, 0);
//		n1 = new Node(coord,1,0);
//		n2 = new Node(coord,2,0);
//		n3 = new Node(coord,3,0);
//		n4 = new Node(coord,4,0);
//		n5 = new Node(coord,5,0);
//		n6 = new Node(coord,6,0);
//		n7 = new Node(coord,7,0);
//		test.AddNode(n1);
//		test.AddNode(n2);
//		test.AddNode(n3);
//		test.AddNode(n4);
//		test.AddNode(n5);
//		test.ShowCache();
//		test.AddNode(n6);
//		test.ShowCache();
//		test.AddNode(n7);
//		test.ShowCache();
		
		
		/* Test MFU */
		CacheTest test = new CacheTest(null, 2);
		
		n1 = new Node(coord,1,0);
		n2 = new Node(coord,2,0);
		n3 = new Node(coord,3,0);
		n4 = new Node(coord,4,0);
		n5 = new Node(coord,5,0);
		n6 = new Node(coord,6,0);
		n7 = new Node(coord,7,0);
		
		
		test.AddNode(n2);
		test.AddNode(n1);
		test.ShowCache();
		test.consultNode(n1,10);
		test.AddNode(n3);
		test.AddNode(n4);
		test.AddNode(n5);
		test.consultNode(n2,10);
		test.consultNode(n4,10);
		test.consultNode(n3,10);
		test.ShowCache();
		test.AddNode(n6);
		test.ShowCache();
		test.AddNode(n7);
		test.ShowCache();
	}

}
