package peersim.solipsis;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.solipsis.PrefetchRequest;
import peersim.solipsis.SolipsisProtocol;

import java.util.*;

public class PrefetchingModule {
	private final static String PREFETCH_EXP = "prefetch_exp";
	private final static String ALGORITHM = "algorithm";
	private final static String EXPAND_COEF = "expand_coef";
	
	private final static int SHARP = 0;
	
	private final static int EXPANDED = 1;

	private final static int DETONATOR_VALUE = 0;
	
	
	private int prefetchExp;
	
	private int prefetchExpTolerance;
	
	private long timeSinceLastRequest;
	
	private SolipsisProtocol protocol;
	
	private HashMap<Integer,NeighborProxy> proxies;
	
	private PrefetchRequest currentRequest;
	
	private int prefetchAlgorithm;
	private int expandCoefficient;
	
	
	
	
	PrefetchingModule(SolipsisProtocol protocol) {
		this.protocol = protocol;
		this.prefetchExp = Configuration.getInt(protocol.getPrefix() + "." + PREFETCH_EXP);
		this.proxies = this.protocol.getProxies();
		this.currentRequest = null;
		this.prefetchExpTolerance = 2;
		
		this.prefetchAlgorithm = Configuration.getInt(protocol.getPrefix() + "." + ALGORITHM);
		this.expandCoefficient = Configuration.getInt(protocol.getPrefix() + "." + EXPAND_COEF);
	}
	
	public boolean needToPropagateRequest() {
		boolean stillTravelling = this.protocol.getState() == MobilityStateMachine.TRAVELLING;
		boolean interRequestTimeExpired = ((getTime() - this.timeSinceLastRequest) >= 100 + this.protocol.estimateMaxRTT()*2);
		boolean runningOutOfAheadNeighbors = this.currentRequest == null || distanceToFarestNeighbor() < this.protocol.getKnowledgeRay() + 2*this.protocol.estimateMaxRTT()*this.currentRequest.getSpeed()/1000;
		return stillTravelling && interRequestTimeExpired && runningOutOfAheadNeighbors;
	}
	
	public boolean prefetchSetFull() {
		return !this.notEnoughPrefetchedNodes();
	}
	
	private long getTime() {
		return CommonState.getTime();
	}
	
	private double distanceToFarestNeighbor() {
		LinkedList<Integer> neighbors = this.protocol.getParticularNeighbors(NeighborProxy.REGULAR);
		int size = neighbors.size();
		double currentDist, farest = 0;
		NeighborProxy current;
		long[] position = this.protocol.getPosition();
		
		for (int i = 0; i < size; i++) {
			current = this.proxies.get(neighbors.get(i));
			if (!this.leftAside(current)) {
				currentDist = VirtualWorld.simpleDistance(position, this.protocol.subjectiveCoord(current.getCoord()));
				
				if (currentDist > farest) {
					farest = currentDist;
				}
			}
		}
		return farest;
	}
	
	public void propagatePrefetchRequest() {
		long[] prefetchVector;
		int speed, ttl, size;
		LinkedList<Integer> destinations;
		int destination;
		Message msg;
		
		switch(this.prefetchAlgorithm) {
		case PrefetchingModule.SHARP:
			prefetchVector = this.calculatePrefetchVector();
			speed = this.protocol.getVirtualEntity().getMaxSpeed();
			this.currentRequest = new PrefetchRequest(this.protocol.createMyImage(), this.protocol.getPosition(), prefetchVector, speed);
			this.currentRequest.setAverageRTT(this.protocol.estimateMaxRTT());
			this.timeSinceLastRequest = CommonState.getTime();
			destinations = this.choosePrefetchDestinations(this.currentRequest);
			size = destinations.size();
			
			if (size > 0) {
				ttl = (this.prefetchExp -this.countPrefetched());
				destination = destinations.get(0);
				msg = this.createPrefetchMessage(destination,ttl, this.currentRequest);
				this.protocol.send(msg, this.proxies.get(destination));
			}
			break;
		case PrefetchingModule.EXPANDED:
			prefetchVector = this.calculatePrefetchVector();
			speed = this.protocol.getVirtualEntity().getMaxSpeed();
			this.currentRequest = new PrefetchRequest(this.protocol.createMyImage(), this.protocol.getPosition(), prefetchVector, speed);
			this.currentRequest.setAverageRTT(this.protocol.estimateMaxRTT());
			this.timeSinceLastRequest = CommonState.getTime();
			destinations = this.choosePrefetchDestinations(this.currentRequest);
			size = destinations.size();
			size = (size > this.expandCoefficient)?this.expandCoefficient:size;
			if (size > 0) {
				ttl = (this.prefetchExp -this.countPrefetched()) / size;
				Random exp = new Random();
				for (int i = 0; i < size; i++) {
					destination = this.protocol.getParticularNeighbors(NeighborProxy.REGULAR).get(exp.nextInt(this.protocol.getParticularNeighbors(NeighborProxy.REGULAR).size()));
					msg = this.createPrefetchMessage(destination,ttl, this.currentRequest);
					this.protocol.send(msg, this.proxies.get(destination));	
				}
			}
			break;
		default:
			break;
		}
	}
	
	public void processPrefetchMsg(Message msg) {
		LinkedList<Integer> destinations;
		int size,ttl;
		int destination;
		PrefetchRequest prefetch = (PrefetchRequest)msg.getContent();
		Message propagateMsg;
		NeighborProxy prefetchedProxy;

		ttl = msg.getTtl() - 1;
		if (!this.protocol.hasNeighbor(prefetch.getSource().getId())) {
			this.sendPrefetchConnectMessage(prefetch.getSource());
		}

		if (ttl > 0) {
			if (this.isFarEnough(prefetch)) { 
				destinations = this.choosePrefetchDestinations(prefetch);
				size = destinations.size();
				size = (size > ttl)?ttl:size;
				msg.setTtl(ttl - size);
				if (size > 0) {
					this.protocol.send(msg, this.proxies.get(destinations.get(0)));				
					for (int i = 1; i < size; i++) {
						destination = destinations.get(i);
						prefetchedProxy = this.proxies.get(destination).clone();
						prefetchedProxy.setQuality(NeighborProxy.PREFETCHED);
						propagateMsg = this.protocol.createFoundMsg(prefetchedProxy, prefetch.getSource().getId());
						this.protocol.send(propagateMsg, prefetch.getSource());
					}	
				}

			} else {
				destinations = this.choosePrefetchDestinations(prefetch);
				size = destinations.size();
				switch (this.prefetchAlgorithm) {
				case PrefetchingModule.SHARP:
					msg.setTtl(ttl);
					if (size > 0) {
						this.protocol.send(msg, this.proxies.get(destinations.get(0)));	
					}
					break;
				case PrefetchingModule.EXPANDED:
					size = (size > this.expandCoefficient)?this.expandCoefficient:size;
					if (size > 0) {
						ttl = (this.prefetchExp -this.countPrefetched()) / size;
						msg.setTtl(ttl);
						for (int i = 0; i < size; i++) {
							destination = destinations.get(i);
							this.protocol.send(msg, this.proxies.get(destination));	
						}
					}
					break;
				default:
					break;
				}
			}
		}
	}

	private boolean isFarEnough(PrefetchRequest request) {
		long speed = request.getSpeed();
		long rtt   = request.getAverageRTT();
		long detonateThreshold = speed * 2 * rtt / 1000;
		double distance = VirtualWorld.simpleDistance(this.protocol.getVirtualEntity().getCoord(), this.protocol.subjectiveCoord(request.getOrigin()));
		return distance > detonateThreshold;
	}
	
	public LinkedList<Integer> getPrefetchedNeighbors() {
		return this.protocol.getParticularNeighbors(NeighborProxy.PREFETCHED);
	}
	
	private boolean importantToKnowledgeZone(NeighborProxy entity) {
		boolean yes = this.protocol.isInsideKnowledgeZone(entity);
		double[] distances;
		long securityDistance = this.currentRequest.getSpeed() * (100 + this.currentRequest.getAverageRTT()) * 2 / 1000;
		
		if (!yes) {
			distances = this.calculatePrefetchProbabilityDistances(entity, this.currentRequest);
			if (distances != null && distances[0] <= this.protocol.getKnowledgeRay() && distances[1] - this.protocol.getKnowledgeRay()  <= securityDistance) {
				yes = true;
			}
		}
		
		return yes;
	}
	
	public void prospectPrefetched() {
		LinkedList<Integer> prefetched = this.getPrefetchedNeighbors();
		int counter, size = prefetched.size();
		NeighborProxy current, closest = null;
		double currentDist, minDist = Double.MAX_VALUE;
		
		counter = 0;
		for (int i = 0; i < size; i++) {
			current = this.proxies.get(prefetched.get(i));
			if (this.importantToKnowledgeZone(current) || this.protocol.constructingEnvelope(current)) {
				current.setQuality(NeighborProxy.REGULAR);
				this.protocol.sendConnectMessage(current);
				counter++;
			} else  if(this.protocol.helpfulToEnvelope(current)) {
				currentDist = VirtualWorld.simpleDistance(this.protocol.getPosition(), this.protocol.subjectiveCoord(current.getCoord()));
				if (minDist > currentDist) {
					minDist = currentDist;
					closest = current;
				}
			} else
				if (leftAside(current)) {
					this.protocol.getVirtualEntity().removeNeighbor(current.getId());
				}		
			}
		if (closest != null) {
			counter++;
			closest.setQuality(NeighborProxy.REGULAR);
			this.protocol.sendConnectMessage(closest);
		}
		if (this.needToPropagateRequest()) {
			this.propagatePrefetchRequest();
		}
	}
	
	public void togglePrefetch(boolean prefetch) {
		if (prefetch == false) {
			LinkedList<Integer> prefetched = this.getPrefetchedNeighbors();
			int size = prefetched.size();
			
			for (int i = 0; i < size; i++) {
				this.protocol.getVirtualEntity().removeNeighbor(prefetched.get(i));
			}
		}
	}
	
	public boolean leftAside(NeighborProxy neighbor) {
		long[] normalVector, lineCoefficients, colinearVector, origin, point;
		double parametricCoefficient;
		double[] doubleOrigin, doublePoint, h, distances = null;
		boolean aside = true;

		if (this.currentRequest != null) {
			point = this.protocol.getVirtualEntity().subjectiveCoord(neighbor.getCoord());
			origin = this.protocol.getVirtualEntity().getCoord();
			colinearVector = this.currentRequest.getPrefetchVector();
			normalVector = new long[3];
			normalVector[0] = -colinearVector[1];
			normalVector[1] = colinearVector[0];

			lineCoefficients = new long[3];
			lineCoefficients[0] = normalVector[0];
			lineCoefficients[1] = normalVector[1];
			lineCoefficients[2] = -lineCoefficients[0] * origin[0] - lineCoefficients[1] * origin[1];

			parametricCoefficient = ((double)(- lineCoefficients[2] - lineCoefficients[0] * point[0] - lineCoefficients[1] * point[1])) / ((double)(lineCoefficients[0]) * normalVector[0] + lineCoefficients[1] * normalVector[1]);

			h = new double[2];
			h[0] = parametricCoefficient * normalVector[0] + point[0];
			aside = (h[0] - (double)origin[0]) / (double)colinearVector[0] < 0;
		}
		
		return aside;
	}
	
	private Message createPrefetchMessage(int destination, int ttl, PrefetchRequest prefetchRequest) {
		Message prefetch = new Message(Message.PREFETCH, this.protocol.getVirtualEntity().getOrder(), this.protocol.getVirtualEntity().getId(), destination, prefetchRequest);
		return prefetch;
	}
	
	private long[] calculatePrefetchVector() {
		long[] destination = this.protocol.getVirtualEntity().getDestination();
		long[] origin = this.protocol.getPosition();
		long[] vector = null;
		
		if (destination != null && origin != null) {
			vector = VirtualWorld.substract(this.protocol.getVirtualEntity().getDestination(), this.protocol.getPosition());
		}
		return vector;
		
	}
	
	
	private double[] calculatePrefetchProbabilityDistances(NeighborProxy current, PrefetchRequest line) {
		long[] normalVector, lineCoefficients, colinearVector, origin, point;
		double parametricCoefficient;
		double[] doubleOrigin, doublePoint, h, distances = null;
		
		point = this.protocol.getVirtualEntity().subjectiveCoord(current.getCoord());
		origin = this.protocol.getVirtualEntity().subjectiveCoord(line.getOrigin());
		colinearVector = line.getPrefetchVector();
		normalVector = new long[3];
		normalVector[0] = -colinearVector[1];
		normalVector[1] = colinearVector[0];
		
		lineCoefficients = new long[3];
		lineCoefficients[0] = normalVector[0];
		lineCoefficients[1] = normalVector[1];
		lineCoefficients[2] = -lineCoefficients[0] * origin[0] - lineCoefficients[1] * origin[1];
		
		parametricCoefficient = ((double)(- lineCoefficients[2] - lineCoefficients[0] * point[0] - lineCoefficients[1] * point[1])) / ((double)(lineCoefficients[0]) * normalVector[0] + lineCoefficients[1] * normalVector[1]);
		
		h = new double[2];
		h[0] = parametricCoefficient * normalVector[0] + point[0];
		h[1] = parametricCoefficient * normalVector[1] + point[1];
		
		if ( (h[0] - (double)origin[0]) / (double)colinearVector[0] >= 0 ) {
			distances = new double[2];
			doubleOrigin = new double[2];
			doublePoint = new double[2];
			doubleOrigin[0] = (double)origin[0];
			doubleOrigin[1] = (double)origin[1];
			doublePoint[0] = (double)point[0];
			doublePoint[1] = (double)point[1];
			distances[0] = VirtualWorld.distance(h, doublePoint);
			distances[1] = VirtualWorld.distance(h, doubleOrigin);
		}
		
		return distances;
	}
	
	private boolean prefetchProbability(double tan) {
		Random rand;
		int intTan;
		int coef;
		boolean choice = false;
		
		coef = 5;
		if (tan < 1) {
			intTan = 1;
		} else if ((long)tan > (long)Integer.MAX_VALUE){
			intTan = Integer.MAX_VALUE;
		} else {
			intTan = (int)tan;
		}
		if (coef >= intTan) {
			choice = true;
		} else {
			rand = new Random();
			for (int i = 0; i < coef; i++) {
				if (rand.nextInt(intTan) == 1) {
					choice = true;
					break;
				}
			}
		}
		
		return choice;
	}
	
	private LinkedList<Integer> choosePrefetchDestinations(PrefetchRequest prefetchRequest) {
		double azimut, dist, tan, mintan;
		double[] distances;
		int size, min, movingSize;
		LinkedList<Integer> neighbors;
		NeighborProxy current, backup = null;
		LinkedList<Integer> chosen;
		LinkedList<Double> tans;
		LinkedList<Integer> sorted;
		double dist1, dist2;
		
		chosen = new LinkedList<Integer>();
		sorted = new LinkedList<Integer>();
		tans = new LinkedList<Double>();
		neighbors = this.protocol.getParticularNeighbors(NeighborProxy.REGULAR);//this.proxies.entrySet().iterator();
		mintan = Double.MAX_VALUE;
		size = neighbors.size();
		for (int i = 0; i < size; i++) {
			current = this.proxies.get(neighbors.get(i));
			distances = this.calculatePrefetchProbabilityDistances(current, prefetchRequest);
			if (distances != null) {
				azimut = distances[0];
				dist   = distances[1];
				dist1 = VirtualWorld.simpleDistance(this.protocol.subjectiveCoord(prefetchRequest.getOrigin()), this.protocol.getPosition());
				dist2 = VirtualWorld.simpleDistance(this.protocol.subjectiveCoord(prefetchRequest.getOrigin()), this.protocol.subjectiveCoord(current.getCoord()));
				if (dist1 < dist2) {
					if (dist != 0) { 
						tan = azimut/dist;// * this.protocol.getVirtualEntity().getMaxSpeed()/10;
//						if (this.prefetchAlgorithm == PrefetchingModule.EXPANDED) {
//							tan /= this.expandCoefficient;
//						}
						if (mintan > tan) {
							mintan = tan;
							backup = current;
						}
						//			System.out.println("azim="+azimut+" dist= "+dist);
						//			System.out.println("tan= "+tan);
						if (prefetchProbability(tan)) {
							tans.add(tan);
							chosen.add(current.getId());
						}
					}
				}
			}
		}
		
//		switch(this.prefetchAlgorithm) {
//		case PrefetchingModule.SHARP:
			size = chosen.size();
			movingSize = size;
			min = 0;
			for (int i = 0; i < size; i++) {
				mintan = Double.MAX_VALUE;
				for (int j = 0; j < movingSize; j++) {
					tan = tans.get(j);
					if (mintan > tan) {
						mintan = tan;
						min = j;
					}
				}
				movingSize--;			
				sorted.add(chosen.remove(min));
			}

		if (sorted.size() == 0) {
			if (backup != null) {
				sorted.add(backup.getId());
			}
		}
		
		return sorted;
	}
	
	private void sendPrefetchConnectMessage(NeighborProxy dest) {
		NeighborProxy me;
		Message msg;
	
		me = this.protocol.createMyImage();
		me.setQuality(NeighborProxy.PREFETCHED);
		msg = this.protocol.createFoundMsg(me, dest.getId());
		this.protocol.send(msg, dest);
	}

	
	public int countPrefetched() {
		return this.getPrefetchedNeighbors().size();
	}
	
	public boolean hasPrefetched() {
		return this.countPrefetched() > 0;
	}
	
	private boolean notEnoughPrefetchedNodes() {
		boolean lacking = this.countPrefetched() < this.prefetchExp;// - this.prefetchExpTolerance;
		return lacking;
	}
	
}
