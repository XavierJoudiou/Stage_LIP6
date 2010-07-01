package peersim.solipsis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import peersim.core.CommonState;
import peersim.solipsis.SolipsisProtocol;
import peersim.solipsis.VirtualEntity;

public class SmallWorld {
	private final static int HOPDISTANCE = 0;
	
	private boolean wiring;
	private int estimatedHopDistance;
	private NeighborProxy[] clients;
	private VirtualEntity thisEntity;
	private Random rand;
	private LinkSet links;
	private int distanceType;
	private long[] origin;
	private double distance;
	
	public SmallWorld(VirtualEntity thisEntity, int setSize) {
		this.thisEntity     = thisEntity;
		this.rand           = new Random();
		this.links = new LinkList(Globals.smallWorldLinkNb);
		this.reset();
		this.distanceType = SmallWorld.HOPDISTANCE;
		this.origin = null;
	}
	
	public void activateWiring (HashMap<Integer, NeighborProxy> clients) {
		this.reset();
		this.clients  = this.copyClients(clients);
		this.wiring = true;
		if (this.distanceType != SmallWorld.HOPDISTANCE) {
			this.origin = this.thisEntity.getCoord();
		}
	}
	
	public void deactivateWiring () {
		NeighborProxy me;
		
		me = new NeighborProxy(this.thisEntity.getCoord(), this.thisEntity.getKnowledgeRay(), this.thisEntity.getId(), this.thisEntity.getProtocol().getPeersimNodeId());
		if (this.chooseLongRangePeer()) {
			this.sendLongRangeLink(me);
//			for (int i = 0; i < clients.length; i++) {
//				clients[i].setQuality(NeighborProxy.LONGRANGE);
//				this.addLongRangeLink(clients[i]);
//			}
		}
//			ELSE {
//			THIS.DISCONNECT();
//		}
		this.reset();
	}
	
	public boolean chooseLongRangePeer() {
		int probability;
		boolean answer;
		
		probability = this.determineChoiceProbability();
		
//		System.out.println(probability);
		if (probability == 0) {
			answer = true;
		} else {
			answer = rand.nextInt(probability) == 1;
		}
//		System.out.println("proba="+probability);
		return answer;
	}
	
	private int determineChoiceProbability() {
		double d, q, n;
		
		if (this.distanceType == SmallWorld.HOPDISTANCE) {
			d = this.estimatedHopDistance/15;
		} else {
			d = Math.log(this.distance)*Math.log(this.distance*this.distance);
		}
		q = ((double)this.thisEntity.getStateMachine().getKeepTravellingProbability() / 10000);
		n = this.calculateSteps(d,q);
		return 0;//(int)(d*d*(1-q)*Math.pow(q,n-1));
	}

	private double calculateSteps(double d, double q) {
		int steps;
		
		steps = (int)(Math.sqrt(2.0 / (double)this.thisEntity.getAcceleration() * d)) * 10;
//		System.out.println(steps+" "+CommonState.getTime());
		return steps;
	}

	public NeighborProxy[] getClients() {
		return this.clients;
	}
	
	public NeighborProxy addLongRangeLink(NeighborProxy proxy) {
		NeighborProxy evicted;
		
		evicted = null;
		if (!this.thisEntity.getProtocol().hasNeighbor(proxy.getId())) {
			proxy.setQuality(NeighborProxy.LONGRANGE);
			evicted = this.links.addLink(proxy);
		}
		return evicted;
//		this.thisEntity.getProtocol().addLocalView(proxy);
	}
	
	public void updateDistance() {
		if (this.distanceType == SmallWorld.HOPDISTANCE) {
			this.estimatedHopDistance++;
		} else {
			this.distance = VirtualWorld.simpleDistance(this.origin, this.thisEntity.getProtocol().realRelativeCoord(this.thisEntity.getCoord(),this.origin));
		}
//		System.out.println(this.estimatedHopDistance);
	}
	
	public HashMap<Integer, NeighborProxy> getLinks() {
		return this.links.getLinks();
	}
	
	public void sendLongRangeLink(NeighborProxy peer) {
		Message msg;
		SolipsisProtocol protocol;
		
		protocol = this.thisEntity.getProtocol();
//		int rand = (new Random()).nextInt(this.clients.length);
//		
//		msg = this.createSmallWorldMessage(peer, this.clients[rand].getId());
//		protocol.send(msg, this.clients[rand]);
		for (int i = 0; i < this.clients.length; i++) {
			msg = this.createSmallWorldMessage(peer, this.clients[i].getId());
			protocol.send(msg, this.clients[i]);
			msg = this.createSmallWorldMessage(this.clients[i], peer.getId());
			protocol.send(msg, peer);
		}
		
	}
	
	private Message createSmallWorldMessage(NeighborProxy peer, int destination) {
		Message result;
		peer = peer.clone();
		peer.setQuality(SolipsisProtocol.SMALLWORLD);
		result = new Message(Message.SMALLWORLD,this.thisEntity.getProtocol().getPeersimNodeId(), this.thisEntity.getId(), 0, peer);
		
		return result;
	}
	
//	private void disconnect() {
//		SolipsisProtocol me;
//		
//		me = this.thisEntity.getProtocol();
//		for (int i = 0; i < this.clients.length; i++) {
//			me.sendDisconnectMessage(this.clients[i]);
//		}
//	}
	
	private NeighborProxy[] copyClients (HashMap<Integer, NeighborProxy> src) {
		NeighborProxy[] destination;
		Iterator<Entry<Integer, NeighborProxy>> it = src.entrySet().iterator();

		destination = new NeighborProxy[src.size()];
		for (int i = 0; i < destination.length; i++) {
			destination[i] = (NeighborProxy)((Map.Entry<Integer, NeighborProxy>)it.next()).getValue();
		}
		
		return destination;
	}
	
	private void reset() {
		this.clients = null;
		this.estimatedHopDistance = 1;
		this.wiring = false;
	}

	public boolean isWiring() {
		return wiring;
	}

	public void updateState(int incomingId, long[] origin) {
		NeighborProxy link;
		
		link = this.links.getLink(incomingId);
		if (link != null) {
			link.setCoord(origin);
		}
	}

	public boolean contains(int peerToForget) {
		return this.links.contains(peerToForget);
	}

	public void removeLink(int peerToForget) {
		this.links.getLinks().remove(new Integer(peerToForget));
	}

//	public void addPointingAtMe(NeighborProxy proxy) {
//		this.pointingAtMe.add(proxy);
//	}
//	
//	public LinkedList<NeighborProxy> getPointingAtMe() {
//		return this.pointingAtMe;
//	}
//
//	public void removePointingAtMe(NeighborProxy proxy) {
//		System.err.println("SmallWorld:addPointingAtMe size="+this.pointingAtMe.size());
//		this.pointingAtMe.remove(proxy);
//		
//	}
	
	
}
