package peersim.solipsis;

import peersim.core.CommonState;
import peersim.edsim.EDSimulator;
import peersim.solipsis.VirtualEntity;

import java.util.*;

public class StatisticsGatherer {

	private VirtualWorldDistribution distribution;
	private long sendCounter;
	private long rcvCounter;
	private double topologyCoherenceCounter;
	private int stepCount;
	private double viewDivergence;
	private boolean hasBeenReinitialized;
	private int movingEntities;
	private long connectionDurations;
	private int rejectCounter;
	
	private int detectCounter;
	private int deltaCounter;
	private int searchCounter;
	
	private long prefetched;
	private long normal;
	
	private double aheadCounter; 
	
	StatisticsGatherer () {
		this.distribution = Globals.distribution;
		this.sendCounter = 0;
		this.rcvCounter  = 0;
		this.topologyCoherenceCounter = 0;
		this.stepCount = 0;
		this.viewDivergence = 0;
		this.movingEntities = 0;
		this.hasBeenReinitialized = false;
		this.connectionDurations = 0;
		this.rejectCounter = 0;
		this.deltaCounter = 0;
		this.detectCounter = 0;
		this.searchCounter = 0;
		this.aheadCounter = 0;
	}
	
	private void reinitializeCounts() {
		this.sendCounter = 0;
		this.rcvCounter  = 0;
		this.topologyCoherenceCounter = 0;
		this.stepCount = 0;
		this.viewDivergence = 0;
		this.movingEntities = 0;
		this.connectionDurations = 0;
		this.rejectCounter = 0;
		this.deltaCounter = 0;
		this.detectCounter = 0;
		this.searchCounter = 0;
		this.hasBeenReinitialized = true;
		this.aheadCounter = 0;
	}
	
	public int getStepCount() {
		return this.stepCount;
	}
	
	public String getAverageMessageRejection() {
		long time = CommonState.getTime();
		if (time != 0) {
			return ""+(this.rejectCounter / CommonState.getTime());
		}
		
		return null;
	}
	
	public void printStatistics () {
//		String detailedMessageInfo = this.getAverageMessageCount()+" (delta: "+this.getDeltaMessageCount()+", search: "+this.getSearchMessageCount()+", detect: "+this.getDetectMessageCount()+")"+ "Rejection: "+this.getAverageMessageRejection();
		String result = ""+this.getCumulatedAverageViewDivergence()+" "+this.getCumulatedOverallTopologyCoherence()+" "+this.getAverageMessageCount() + " " + getCumulatedAheadNeighborCount() + " " + getOverallAverageConnectionDuration() ;
		
		this.stepCount++;
		Globals.steps = this.stepCount;
		if (!this.hasBeenReinitialized && this.stepCount == 1000) {
			this.reinitializeCounts();
		}
		if (this.hasBeenReinitialized) {
			if (!Globals.quiet) {
				System.out.println(result);
			} else {
				if (CommonState.getTime() > CommonState.
						getEndTime() - 100) {
					System.out.println(result);
				} 
			}
		}else {
//			if (getAverageNeighborSetSize() > 25) System.out.println(getAverageNeighborSetSize());
			System.err.print("- "+EDSimulator.heapSize());
		}
	}
	
	private String getAverageMessageCount () {
//		long time = CommonState.getTime();
		if (this.stepCount != 0) {
			return ""+(this.sendCounter / this.stepCount);//CommonState.getTime());
		}
		
		return null;
	}
	
	private String getOverallAverageConnectionDuration() {
		this.connectionDurations += this.getAverageConnectionDuration();
		
		return ""+((double)this.connectionDurations / (double)this.stepCount);
	}
	
	private long getAverageConnectionDuration() {
		Iterator neighbors,it = this.distribution.getDistribution().entrySet().iterator();
		VirtualEntity current;
		long neighborCount, overall, diff, currentTime = CommonState.getTime();
		Long time;
		int id;
		Map.Entry<Integer, Long> entry;
		overall = 0;
		neighborCount = 0;
		while (it.hasNext()) {
			current = (VirtualEntity)((Map.Entry)it.next()).getValue();
			if (current.getState() == MobilityStateMachine.TRAVELLING) {
				neighbors = current.getProtocol().getConnectionTimes().entrySet().iterator();
				while(neighbors.hasNext()) {
					entry = (Map.Entry)neighbors.next();
					time = entry.getValue();
					id = entry.getKey();
					if (current.isAheadOfMovement(id) && current.addedInMovement(id)) { 
						if (current.getQualityOf(id) == NeighborProxy.PREFETCHED) {
							diff = (currentTime - time.longValue() - current.getProtocol().getLatency());
							diff = (diff>0)?diff:0;
						} else {
							diff = currentTime - time.longValue();
						}
						overall += diff;
						neighborCount++;
					}
				}
			}
		}
		return (long)((double)overall /(double)neighborCount);
	}
	
	private String getCumulatedAheadNeighborCount() {
		this.aheadCounter += this.getAheadNeighborCount();
		if (this.stepCount == 0) {
			return "0";
		}
		return ""+(this.aheadCounter/this.stepCount);
	}
	
	private double getAheadNeighborCount() {
		Iterator neighbors,it = this.distribution.getDistribution().entrySet().iterator();
		VirtualEntity current;
		int neighborCount, travelling;
		Integer id;
		Map.Entry<Integer, VirtualEntity> entry;
		
		neighborCount = 0;
		travelling = 0;
		while (it.hasNext()) {
			current = (VirtualEntity)((Map.Entry)it.next()).getValue();
			if (current.getState() == MobilityStateMachine.TRAVELLING) {
				travelling++;
				neighbors = current.getProtocol().getConnectionTimes().entrySet().iterator();
				while(neighbors.hasNext()) {
					entry = (Map.Entry)neighbors.next();
					id = entry.getKey();
					if (current.isAheadOfMovement(id.intValue()) && current.addedInMovement(id)) { 
//						System.out.println("ucu");
						neighborCount++;
					}
				}
			}
		}
		if (travelling == 0) {
			return 0;
		}
		
		return (double)neighborCount / (double)travelling;	
	}
	
	private double calculateDivergence (VirtualEntity owner, VirtualEntity neighbor, NeighborProxy proxy) {
//	    System.out.println(owner+" "+neighbor+" "+proxy);
		return VirtualWorld.simpleDistance(owner.relativeCoord(neighbor.getCoord()), owner.relativeCoord(proxy.getCoord()));
	}
	
	private String getTime() {
		return ""+CommonState.getTime();
	}
	
	private int getOverallTopologyCoherence () {
		HashMap<Integer, VirtualEntity> set = this.distribution.getDistribution();
		Iterator it = set.entrySet().iterator();
		Iterator it2;
		VirtualEntity current1, current2;
		int counter;
		
		counter = 0;
		while(it.hasNext()) {
			current1 = (VirtualEntity)((Map.Entry)it.next()).getValue();
			if (current1.isStabilized()) {
				it2 = set.entrySet().iterator();
				while (it2.hasNext()) {
					current2 = (VirtualEntity)((Map.Entry)it2.next()).getValue();
					if (current1 != current2 && current1.getProtocol().isInsideKnowledgeZone(current2) && current1.getNeighbor(current2.getId()) == null) {
						counter++;
//						break;
					}
				}
			} else {
				counter++;
			}
		}
		return counter;
	}
	
	private String getCumulatedOverallTopologyCoherence () {
		this.topologyCoherenceCounter += this.getOverallTopologyCoherence();
		return ""+(this.topologyCoherenceCounter/this.stepCount);
	}
	
	private String getCumulatedMovingTopologyCoherence () {
		this.topologyCoherenceCounter += this.getMovingTopologyCoherence();//(this.topologyCoherenceCounter * (double)(this.stepCount - 1) + (double)getMovingTopologyCoherence() ) / (double)this.stepCount;
		return ""+(this.topologyCoherenceCounter/this.stepCount);
	}
	
	private String getCumulatedMovingEntities () {
		this.movingEntities += this.getMovingEntities();
		if (this.stepCount == 0) {
			return "0";
		}
		return ""+((double)this.movingEntities /(double)this.stepCount);
	}
	
	private int getMovingEntities() {
		Iterator it = this.distribution.getDistribution().entrySet().iterator();
		VirtualEntity current;
		int counter;
		
		counter = 0;
		while(it.hasNext()) {
			current = (VirtualEntity)((Map.Entry)it.next()).getValue();
			if (current.getState() == MobilityStateMachine.TRAVELLING) {
				counter++;
			}
		}
		
		return counter;
	}
	
	
	
	private int getMovingTopologyCoherence () {
		HashMap<Integer, VirtualEntity> set = this.distribution.getDistribution();
		Iterator it = set.entrySet().iterator();
		Iterator it2;
		VirtualEntity current1, current2;
		int counter;
		
		counter = 0;
		while(it.hasNext()) {
			current1 = (VirtualEntity)((Map.Entry)it.next()).getValue();
			if (current1.getState() == MobilityStateMachine.TRAVELLING) {
				if (current1.isStabilized()) {
					it2 = set.entrySet().iterator();
					while (it2.hasNext()) {
						current2 = (VirtualEntity)((Map.Entry)it2.next()).getValue();
						if (current1 != current2 && current1.getProtocol().isInsideKnowledgeZone(current2) && current1.getNeighbor(current2.getId()) == null) {
							counter++;
							break;
						}
					}
				} else {
					counter++;
				}
			}
		}
		
		return counter;
	}
	
	
	
	private String getCumulatedAverageViewDivergence () {
		this.viewDivergence += getAverageViewDivergence();
		
		return (this.stepCount == 0)?"0":""+(this.viewDivergence / this.stepCount);

	}
	
	private double getAverageViewDivergence () {
		HashMap<Integer, VirtualEntity> set = this.distribution.getDistribution();
		Iterator it = set.entrySet().iterator();
		VirtualEntity current, neighbor;
		LinkedList<Integer> neighbors;
		int count,size;
		long overallDivergence;
		int neighborId;
		NeighborProxy proxy;
		
		count = 0;
		overallDivergence = 0;
		while(it.hasNext()) {
			current = (VirtualEntity)((Map.Entry)it.next()).getValue();
			neighbors = current.getProtocol().getParticularNeighbors(NeighborProxy.REGULAR);
			size = neighbors.size();
			for (int i = 0; i < size; i++) {
				neighborId = neighbors.get(i);
//				System.out.println("neighb"+neighborId);
				neighbor = this.distribution.get(neighborId);
//				if (neighbor.getState() == MobilityStateMachine.TRAVELLING) {
					proxy = current.getNeighbor(neighborId);
					overallDivergence += this.calculateDivergence(current, neighbor, proxy);
					count++;
//				}
			}
		}
		
		if (count == 0) {
			return 0;
		}
		
		return ((double)overallDivergence / (double)count);
	
	}
	
	public void messageSent () {
		sendCounter++;
	}
	
	public void messageReceived () {
		rcvCounter++;
	}
	
	public void messageRejected () {
		rejectCounter++;
	}
	
	public void messageSearch() {
		this.searchCounter++;
	}
	
	private int getAverageNeighborSetSize() {
		HashMap<Integer, VirtualEntity> set = this.distribution.getDistribution();
		Iterator it = set.entrySet().iterator();
		VirtualEntity current;
		int count = 0;
		
		while(it.hasNext()) {
			current = (VirtualEntity)((Map.Entry)it.next()).getValue();
			count += current.getProtocol().getProxies().size();//getParticularNeighbors(NeighborProxy.REGULAR).size();
		}
		
		return (count/set.size());
	}
	
	private String getSearchMessageCount () {
		long time = CommonState.getTime();
		if (time != 0) {
			return ""+((double)this.searchCounter / (double)CommonState.getTime());
		}
		
		return null;
	}
	
	public void messageDetect() {
		this.detectCounter++;
	}
	
	private String getDetectMessageCount () {
		long time = CommonState.getTime();
		if (time != 0) {
			return ""+(this.detectCounter / CommonState.getTime());
		}
		
		return null;
	}
	
	public void messageDelta() {
		this.deltaCounter++;
	}
	
	private String getDeltaMessageCount () {
		long time = CommonState.getTime();
		if (time != 0) {
			return ""+(this.deltaCounter / CommonState.getTime());
		}
		
		return null;
	}
}
