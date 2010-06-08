package peersim.solipsis;

import java.util.HashMap;
import java.util.LinkedList;

public class LinkList implements LinkSet {

	private static final int LRU     = 0;
	private static final int ENTROPY = 1;
	
	private LinkedList<Link> links;
	private int setSize;
	private int policy;
	private int oldestAge;
	private double closestDistance;
	
	private class Link {
		public double weight;
		public NeighborProxy link;
		public int age;
		
		Link(int w, NeighborProxy l) {
			this.weight = w;
			this.link = l;
			this.age = 0;
		}
		
		Link(NeighborProxy l) {
			this(0,l);
		}
	}
	
	LinkList(int size) {
		this(size, LinkList.ENTROPY);
	}
	
	LinkList(int size, int policy) {
		this.links = new LinkedList<Link>();
		this.setSize = size;
		this.policy = policy;
		this.oldestAge = 0;
		this.closestDistance = Integer.MAX_VALUE;
	}
	
	public NeighborProxy addLink(NeighborProxy proxy) {
		NeighborProxy evicted;
		
		evicted = null;
		if (! this.alreadyLinked(proxy.getId())) {
			evicted = this.cachePolicy(proxy);
		}
		
		return evicted;
	}

	private NeighborProxy cachePolicy(NeighborProxy proxy) {
		Link newLink;
		NeighborProxy evicted;
		
		evicted = null;
		switch(this.policy) {
		case LinkList.LRU:
			this.links.addLast(new Link(proxy));
			if (this.links.size() > this.setSize) {
				this.links.removeFirst();
			}
			break;
		default:
			newLink = new Link(proxy);
			this.assignWeight(newLink);
			this.putWeighted(newLink);
			if (this.links.size() > this.setSize) {
				evicted = this.removeLightest();
			}
			this.refreshWeights();
			break;
		}
		
		return evicted;
	} 

	private void refreshWeights() {
		int size;
		Link current;
		
		size = this.links.size();
//		System.out.println("BEGIN (LinkList)");
		for (int  i = 0; i < size; i++) {
			current = this.links.get(i);
			this.assignWeight(current);
			current.age++;
			if (current.age > this.oldestAge) {
				this.oldestAge = current.age;
			}
//			System.out.println(current.weight+", age="+current.age);
		}
//		System.out.println("END");
		
	}
	
	private boolean alreadyLinked(int requester) {
		int size;
		
		size = this.links.size();
		for (int i = 0; i < size; i++) {
			if (this.links.get(i).link.getId() == requester) {
				return true;
			}
		}
		
		return false;
	}

	private NeighborProxy removeLightest() {
		int size;
		Link current;
		double minWeight, currentWeight;
		int index;
		
		current = null;
		minWeight = Integer.MAX_VALUE;
		size = this.links.size();
		index = -1;
		for (int  i = 0; i < size; i++) {
			current = this.links.get(i);
			currentWeight = current.weight;
			if (currentWeight < minWeight) {
				minWeight = currentWeight;
				index = i;
			}
		}
		
		if (index >= 0) {
			current = this.links.remove(index);
//			this.entity.getProtocol().neighborProxyToVirtualEntity(current.link).getProtocol().removePointingAtMe(this.entity.getProtocol().createMyImage());
//			this.lightestAge = current.age;
		}
		
		return (current == null)?null:current.link;
	}

	private void putWeighted(Link newLink) {
		this.links.add(newLink);
		
	}

	private void assignWeight(Link newLink) {
		switch (this.policy) {
		case LinkList.ENTROPY:
			newLink.weight = this.entropyWeightFunction(newLink);
			break;
		default:
			break;
		}
	}

	private double entropyWeightFunction(Link newLink) {
		double alpha;
		
		alpha = 1F;
		
		if (alpha * this.entropy(newLink) + (1 - alpha) * this.ageMetric(newLink) < 0) { 
			System.out.println(this.entropy(newLink)+" "+this.ageMetric(newLink));
			System.exit(1);
		}
		return (alpha * this.entropy(newLink) + (1 - alpha) * this.ageMetric(newLink));
	}

	private double ageMetric(Link newLink) {
		int metricValue;
		
		if (this.links.size() == 0) {
			this.oldestAge = newLink.age;
			metricValue = Integer.MAX_VALUE;
		} else {
			metricValue = this.oldestAge - newLink.age;
		}
		return metricValue;
	}

	private double entropy(Link newLink) {
		double newDist, entropy;

		newDist = this.distanceToLinks(newLink);

		if (this.closestDistance == 0 || this.closestDistance == Integer.MAX_VALUE) {
			entropy = Integer.MAX_VALUE;
		} else {
			entropy = newDist / this.closestDistance;
		}
		
		if (newDist < this.closestDistance) {
			this.closestDistance = newDist;
		}
//		System.out.println("entropy "+this.closestDistance);
		return entropy;
	}

	private double distanceToLinks(Link newLink) {
		int size;
		long[] link1;
		NeighborProxy link2;
		double currentDist;
		boolean beenThere;
		
		beenThere = false;
		size = this.links.size();
		link1 = newLink.link.getCoord();
		currentDist = 0;
		for (int j = 0; j < size; j++) {
			link2 = this.links.get(j).link;
			if (link2.getId() != newLink.link.getId()) {
				beenThere = true;
				currentDist += VirtualWorld.simpleDistance(VirtualWorld.realRelativeCoord(link1, link2.getCoord()), link2.getCoord());
			}
		}
		if (beenThere) {
			currentDist = currentDist / size;
		} else {
			currentDist = Double.MAX_VALUE;
		}
		
//		System.out.println("distancetoLinks="+currentDist);
		
		return currentDist;
	}

	public HashMap<Integer, NeighborProxy> getLinks() {
		HashMap<Integer,NeighborProxy> result;
		int size;
		NeighborProxy current;
		
		size = this.links.size();
		result = new HashMap<Integer,NeighborProxy>();
		for (int i = 0; i < size; i++ ) {
			current = this.links.get(i).link;
			result.put(current.getId(), current);
		}
		return result;
	}

	public void removeLinks() {
		this.links = new LinkedList<Link>();
	}

	public int size() {
		return this.links.size();
	}

	public NeighborProxy getLink(int id) {
		int size;
		NeighborProxy current;
		
		size = this.links.size();
		
		for (int i = 0; i < size; i++) {
			current = ((Link)this.links.get(i)).link;
			if (current.getId() == id) {
				return current;
			}
		}
		
		return null;
	}

	public boolean contains(int requester) {
		return this.alreadyLinked(requester);
	}
	
}
