package peersim.solipsis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class SmallWorldEvaluation implements Control {

	private static final String APPLICATIVE_LAYER = "applicative";
	private static final String LOOKUP_NB         = "lookupnb";
	private static final String KIND              = "evaltype";
	private static final String TYPE              = "type";
	
	private static final int STATIC = 0;
	private static final int DYNAMIC = 1;
	private static final int EFFICIENCY = 2;
	
	private int applicativeLayerId;
	private int lookupNb;
	private int kind;
	private int type;
	
	private class Couple {
		private int distance;
		private SolipsisProtocol node;
		
		Couple(int distance, SolipsisProtocol node) {
			this.distance = distance;
			this.node = node;
		}

		public void setDistance(int distance) {
			this.distance = distance;
		}

		public int getDistance() {
			return distance;
		}

		public SolipsisProtocol getNode() {
			return node;
		}
		
		@Override
		public String toString() {
			return node.getVirtualEntity().getId() + ", " + this.distance;
		}
		
		
	}
	
	public SmallWorldEvaluation(String prefix) {
		this.applicativeLayerId = Configuration.getPid(prefix + "." + APPLICATIVE_LAYER);
		this.lookupNb           = Configuration.getInt(prefix + "." + LOOKUP_NB);
		this.kind               = Configuration.getInt(prefix + "." + KIND);
		this.type               = Configuration.getInt(prefix + "." + TYPE);
//		Globals.envelope = 0;
	}
	
	public boolean execute() {
		switch (this.kind) {
		case SmallWorldEvaluation.STATIC:
			this.staticRoutingEval();
			break;
		case SmallWorldEvaluation.DYNAMIC:
			this.dynamicRoutingEval();
			break;
		case SmallWorldEvaluation.EFFICIENCY:
			this.linkEfficiencyEval();
			break;
		default:
			break;

		}
		
		return false;
	}
	
	private void dynamicRoutingEval() {
		SolipsisProtocol sender,receiver;
		int toSend;
		int meanDist;

		sender = null;
		if ((this.type == SolipsisProtocol.SMALLWORLD && this.countNoLongRange() == 0) || (this.type != SolipsisProtocol.SMALLWORLD)) {
			System.out.println("Evaluating join cost...");
			if (Globals.topologyIsReady) {
				toSend = this.lookupNb - Globals.lookupsPending;
				if (toSend == 0) {
					System.out.println("Warning, cannot send more requests");
				} else {
					System.out.println("Sending " + toSend + " lookup requests...");
				}
				for (int i = 0; i < toSend; i++) {
					Globals.lookupsPending++;
					receiver = sender = this.pickRandomNode();
					while (receiver == sender)  {
						receiver = this.pickRandomNode();
					}

					sender.lookup(receiver.getPosition(), Lookup.LOOKUP);
				}
			}
		}
		if (Globals.lookupsAchieved != 0) {
			meanDist = Globals.hops/Globals.lookupsAchieved;
			System.out.println("The mean distance is " + meanDist + " hops (Variance " + (Globals.sqLookups/Globals.lookupsAchieved - meanDist * meanDist) + ")");
			
			if (this.type == SolipsisProtocol.SMALLWORLD) {
				System.out.println("use of long range: "+ ((double)Globals.longRangeUse / (double)Globals.totalUse * 100) +"%, absolute="+Globals.longRangeUse+")");
				System.out.println("No-long-range nodes: " + this.countNoLongRange() + "(mean = "+ this.countLongRangeMeanSetSize() +")");
			}
		}
	}
	
	private void linkEfficiencyEval() {
		HashMap<Integer,VirtualEntity> avatars;
		Iterator it;
		VirtualEntity current, dest;
		SolipsisProtocol protocol;
		int sum, total, sqsum, value;
		
		sum = total = sqsum = 0;
		avatars = Globals.distribution.getDistribution();
		it = avatars.entrySet().iterator();
		current = null;
		while (it.hasNext()) {
			current = (VirtualEntity)((Map.Entry)it.next()).getValue();
			protocol = current.getProtocol();
			if (protocol.longRangeSetSize() != 0) {
				dest = protocol.neighborProxyToVirtualEntity(protocol.getLongRangeLink());
				value = this.shortestPath(protocol, dest.getProtocol(), false);
				sum += value;
				sqsum += value * value;
				total++;
			}
		}
		if (total != 0) {
			System.out.println("There is "+ (sum/total) + " short range links per long range link (variance: " + ((sqsum/total) - (sum/total) * (sum/total)) + ")");
		} else {
			System.out.println("There is no long range links");
		}
	}

	private int countLongRangeMeanSetSize() {
		int counter, sum;
		HashMap<Integer,VirtualEntity> avatars;
		Iterator it;
		VirtualEntity current;
		
		counter = 0;
		sum = 0;
		avatars = Globals.distribution.getDistribution();
		it = avatars.entrySet().iterator();
		while (it.hasNext()) {
			current = (VirtualEntity)((Map.Entry)it.next()).getValue();
			sum += current.getProtocol().longRangeSetSize();
			counter++;
		}
		
		return sum/counter;
	}
	
	private SolipsisProtocol getLongRangeSrc() {
		HashMap<Integer,VirtualEntity> avatars;
		Iterator it;
		VirtualEntity current;
		
		avatars = Globals.distribution.getDistribution();
		it = avatars.entrySet().iterator();
		current = null;
		while (it.hasNext()) {
			current = (VirtualEntity)((Map.Entry)it.next()).getValue();
			if (current.getProtocol().longRangeSetSize() != 0) {
				break;
			}
		}
		
		return (current==null)?null:current.getProtocol();
	}

	private int countNoLongRange() {
		int counter;
		HashMap<Integer,VirtualEntity> avatars;
		Iterator it;
		VirtualEntity current;
		
		counter = 0;
		avatars = Globals.distribution.getDistribution();
		it = avatars.entrySet().iterator();
		while (it.hasNext()) {
			current = (VirtualEntity)((Map.Entry)it.next()).getValue();
			if (current.getProtocol().longRangeSetSize() == 0) {
				counter++;
			}
		}
		
		return counter;
	}
	
	private void staticRoutingEval() {
		SolipsisProtocol sender,receiver;
		int sum, value, fails, sumShortest;
		
		fails = 0;
		System.err.println("Evaluating small world property...");
		if (Globals.topologyIsReady) {
			sum = 0;
			sumShortest = 0;
			//			System.err.println("1");
			sender = this.pickRandomNode();
			if (sender.getType() != SolipsisProtocol.SMALLWORLD || (sender.getType() == SolipsisProtocol.SMALLWORLD && this.countNoLongRange() < 10)) { 
				for (int i = 0; i < this.lookupNb; i++) {
					//				System.out.print("-");
					receiver = sender = this.pickRandomNode();
					//				System.err.println("2");
					while (receiver == sender) {
						receiver = this.pickRandomNode();
					}
					//				System.err.println("3");
					value = sender.estimateHops(receiver, true);
					if (value != -1) {
						sum += value;
					} else {
						fails++;
						//					System.err.println("Lookup Failed");
					}
					value = this.shortestPath(sender, receiver, true);
					sumShortest += value;
					//				System.err.println("-");
				}
				//			System.out.println();
				//			System.out.println("envelopes:"+Globals.envelope);
				System.out.println(Network.size()+" "+(sum / this.lookupNb) + " " + (sumShortest / this.lookupNb));
				//			Globals.envelope++;
				//			if (Globals.envelope==1){
				//			    System.err.println("Simulation finished");
				//			    System.exit(0);
				//			}
			}
		}
	}
	
	private int shortestPath(SolipsisProtocol src, SolipsisProtocol dest, boolean useLongRange) {
		return dijkstra(src,dest, useLongRange);
	}
	
//	private int pathLength(SolipsisProtocol src, SolipsisProtocol dest) {
//		Iterator neighbors;
//		NeighborProxy current;
//		int currentDist, min;
//		
//		if (src == dest) {
//			return 1;
//		}
//		
//		min = Integer.MAX_VALUE;
//		neighbors = src.getNeighbors();
//		while (neighbors.hasNext()) {
//			current = (NeighborProxy)((Map.Entry)neighbors.next()).getValue();
//			currentDist = this.pathLength(src.neighborProxyToVirtualEntity(current).getProtocol(), dest);
//			
//			if (currentDist < min) {
//				min = currentDist;
//			}
//		}
//		
//		return 1 + min;
//	}
	
	private int dijkstra(SolipsisProtocol sender, SolipsisProtocol receiver, boolean useLongRange) {
		LinkedList<Couple> nodeSet;
		SolipsisProtocol closest;
		Couple closestCouple;
		LinkedList<SolipsisProtocol> neighbors;
		HashMap<Integer, Integer> seen;

		seen = new HashMap<Integer, Integer>();
		nodeSet = new LinkedList<Couple>();
		closest = null;
		closestCouple = null;
		nodeSet.add(new Couple(0,sender));
		while (nodeSet.size() != 0) {
//			System.out.println("nodeSet " + nodeSet.size());
			closestCouple = this.findClosest(nodeSet);
			closest = closestCouple.getNode();
			if (closest == receiver) {
				break;
			}
			seen.put(closest.getVirtualEntity().getId(), closestCouple.getDistance());
			neighbors = closest.getSolipsisNeighbors(useLongRange);
			this.addToSet(neighbors,nodeSet, closestCouple.getDistance(), seen);
//			System.out.println("seen: "+seen.size());
		}
//		System.out.println(closestCouple.getDistance());
		return closestCouple.getDistance();
	}
	
//	private int getDistance(LinkedList<Couple> nodeSet, SolipsisProtocol lastHop) {
//		int size, dist;
//		Couple current;
//		
//		dist = -1;
//		size = nodeSet.size();
//		for (int i = 0; i < size; i++) {
//			current = nodeSet.get(i);
//			if (current.getNode() == lastHop) {
//				dist = current.getDistance();
//			}
//		}
//		return dist;
//	
//	}

	private void addToSet(LinkedList<SolipsisProtocol> neighbors, LinkedList<Couple> nodeSet, int currentDistance, HashMap<Integer, Integer> seen) {
		int size;
		
		size = neighbors.size();
//		System.out.println("curr"+currentDistance);
		for (int i = 0; i < size; i++) {
			this.addToSet(new Couple(currentDistance + 1, neighbors.get(i)), nodeSet, seen);
		}
		
	}

	private void addToSet(Couple couple, LinkedList<Couple> nodeSet, HashMap<Integer, Integer> seen) {
		int size, coupleId;
		Couple current;
		Integer distance;
		boolean added;
		
		size = nodeSet.size();
		added = false;
		coupleId = couple.getNode().getVirtualEntity().getId();
		distance = seen.get(coupleId);
		if (distance == null || distance.intValue() > couple.getDistance()) {
//			System.out.println("rrr"+distance+" "+couple.getDistance());
			for (int i = 0; i < size; i++) {
				current = nodeSet.get(i);
				if (current.getNode().getVirtualEntity().getId() == coupleId) {
//					System.out.println("ici");
					nodeSet.remove(current);
				}
				if (!added && current.getDistance() >= couple.getDistance()) {
					nodeSet.add(i,couple);
					added = true;
				}
			}
			seen.put(coupleId, couple.getDistance());
		} else {
			added = true;
		}
		
		if (!added) {
			nodeSet.add(couple);
		}
	}

	private Couple findClosest(LinkedList<Couple> nodeSet) {
		return nodeSet.remove(0);
	}

	private SolipsisProtocol pickRandomNode() {
		SolipsisProtocol result;
		Random generator;
		Node randomNode;

		generator = new Random();
		randomNode = Network.get(generator.nextInt(Network.size()));
		
		result = (SolipsisProtocol)randomNode.getProtocol(this.applicativeLayerId);
		
		return result;
	}

}
