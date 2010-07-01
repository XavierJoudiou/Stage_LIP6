package cache.tests;

import cache.utils.Node;
import junit.framework.TestCase;

public class TestNode extends TestCase {
	Node n1,n2;
	long[] coord = {0,0,0};

	
	public void testNode() {
		n1 = new Node(coord, 0, 0);
	}

	public void testIncPos() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		int av = n1.getCachePos();
		n1.IncPos();
		assertEquals((av + 1), n1.getCachePos());
	}

	public void testDecPos() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		n1.IncPos();
		n1.IncPos();
		int av = n1.getCachePos();
		n1.DecPos();
		assertEquals((av - 1), n1.getCachePos());
	}

	public void testIncInfo() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		int av = n1.getCacheInfo();
		n1.IncInfo(3);
		assertEquals((av + 3), n1.getCacheInfo());
	}

	public void testDecInfo() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		n1.IncInfo(4);
		int av = n1.getCacheInfo();
		n1.DecInfo();
		assertEquals((av - 1), n1.getCacheInfo());
	}

	public void testGetNodeCoord() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		long[] coortest = {1,2,3};
		n1.setNodeCoord(coortest);
		assertEquals(coortest, n1.getNodeCoord());
	}

	public void testSetNodeCoord() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		long[] coortest = {3,5,3};
		n1.setNodeCoord(coortest);
		assertEquals(coortest, n1.getNodeCoord());
	}

	public void testGetId() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		assertEquals(n1.getId(), 0);
	}

	public void testSetId() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		assertEquals(n1.getId(), 0);
		n1.setId(7);
		assertEquals(n1.getId(), 7);
	}

	public void testGetQuality() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		assertEquals(n1.getQuality(), 0);	
	}

	public void testSetQuality() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		assertEquals(n1.getQuality(), 0);
		n1.setQuality(7);
		assertEquals(n1.getQuality(), 7);
	}

	public void testGetCacheInfo() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		assertEquals(n1.getCacheInfo(), -1);	
	}

	public void testSetCacheInfo() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		assertEquals(n1.getCacheInfo(), -1);
		n1.setCacheInfo(7);
		assertEquals(n1.getCacheInfo(), 7);

	}

	public void testSetCachePos() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		assertEquals(n1.getCacheInfo(), -1);	
	}

	public void testGetCachePos() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);
		assertEquals(n1.getCachePos(), -1);
		n1.setCachePos(7);
		assertEquals(n1.getCachePos(), 7);	
	}

	public void testToString() {
		System.out.println("---------------------------");
		System.out.println("Test: " + this);
		System.out.println("---------------------------");
		n1 = new Node(coord, 0, 0);	
		n1.toString();
	}

}
