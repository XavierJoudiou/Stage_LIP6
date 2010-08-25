package peersim.solipsis;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.solipsis.VirtualEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import javax.naming.LimitExceededException;




/**
 * 
 * @author Legtchenko Sergey
 *
 */
public class SolipsisProtocol implements EDProtocol {
	
	/**
	 * The name of the underlying transport protocol.
	 */
	private final static String PAR_TRANSPORT   = "transport";
	private final static String PAR_ID          = "id";
	private final static String TOLERANCE_LEVEL = "tolerance_level";
	private final static String EXP             = "exp";	
	private final static String TYPE            = "type";
	private final static String SMNB            = "small_world";

	/* pour le cache */
	private final static String CACHE_SIZE		= "cacheSize";
	private final static String CACHE_STRATEGIE = "cachestrategie";
	private final static String CACHE_LIMIT		= "limite";
	private final static String TIME_LIMIT		= "time_limite";
	private final static String LIMIT_CONNAISSANCE		= "limite_connaissance";
	private final static String UPDATE_TIME		= "update_time";
	private final static String UPDATE_OK		= "update_ok";
	private final static String CONTACT_NODE	= "contact_node";
	private final static String PREFETCH_AMELIORE	= "prefetch_ameliore";

	private final static String HELP_OK			= "help_ok";
	

	private final static String CACHE_DEBUG		= "cacheDebug";
	private final static int OFF	=  0;
	private final static int FIFO	=  1;
	private final static int LRU	=  2;
	private final static int FIFOMULT	=  3;

	public final static int BASIC      = 0;
	public final static int ENHANCED   = 1;
	public final static int SMALLWORLD = 2;

	private static final int LONGLINKSFIRST = 0;
	
	private final static int WANDERING_UPDATE_INTERVAL = 100;
	private final static int TRAVELLING_UPDATE_INTERVAL = 200;
	
	private int transport;
	
	private int protocolId;
	
	private VirtualEntity mainVirtualEntity;
	
	private String status;
	
	private String prefix;
	
	private int peersimNodeId;
	
	private LinkedList<NeighborProxy> convexEnvelope;
	
	private GenericTransportLayer transportLayer;
	
	private double knowledgeRay;
	
	private double toleranceLevel;
	
	private int exp;
	
	private HashMap<Integer,NeighborProxy> proxies;
	
	private long sendStateUpdateTimer;
	
	private long timerInterval;
	
	private HashMap<Integer,Long> connectionTime;
	
	private boolean searchInProgress;
	
	private boolean stabilized;
	
	private int type;
	
	private PrefetchingModule prefetchingModule;
	
	private HashMap<Integer,Long> updateTimestamps;
	
	private HashMap<Integer, long[]> predictionVector;
	
	private HashMap<Integer, Long> disconnectTimestamps;
	
	private HashMap<Integer, Long> connectTimestamps;

	
	private HashMap<Integer,Long> detected;
	
	private SmallWorld smallWorldModule;
	private int routeAlgorithm;
	
	/* Variables pour le cache */
	private CacheModule cache;
	private int cacheSize;
	private int strategieCache;
	private int limite;
	private int time_limite;
	private int limite_connaissance;
	private int update_time;
	private int update_ok;
	private int help_ok;
	private int cacheDebug;
	int contact_node;
	private int prefetch_ameliore;
	private File fichier3;
	private FileWriter fw3;
	
	
	public SolipsisProtocol(String prefix) throws IOException {
	
		this.transport = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		this.protocolId = Configuration.getPid(prefix+"."+PAR_ID);
		this.toleranceLevel = Configuration.getDouble(prefix+"."+TOLERANCE_LEVEL);
		this.exp = Configuration.getInt(prefix+"."+EXP);
		this.type = Configuration.getInt(prefix + "." + TYPE);
		Globals.smallWorldLinkNb = Configuration.getInt(prefix + "." + SMNB);
		this.status = "Offline";
		this.prefix = prefix;
		this.knowledgeRay = Long.MAX_VALUE;
		this.proxies = new HashMap<Integer,NeighborProxy>();
		this.transportLayer = null;
		this.sendStateUpdateTimer = CommonState.getTime();
		this.connectionTime = new HashMap<Integer,Long>();
		this.searchInProgress = false;
		this.stabilized = false;
		this.updateTimestamps = new HashMap<Integer, Long>();
		this.disconnectTimestamps = new HashMap<Integer, Long>();
		this.connectTimestamps = new HashMap<Integer, Long>();
		this.predictionVector = new HashMap<Integer, long[]>();
		this.detected = new HashMap<Integer,Long>();
		this.routeAlgorithm = 0;
		if (this.type == SolipsisProtocol.ENHANCED) {
			this.prefetchingModule = new PrefetchingModule(this);
		}
		if (this.type == SolipsisProtocol.SMALLWORLD) {
			this.smallWorldModule = new SmallWorld(this.mainVirtualEntity,Globals.smallWorldLinkNb);
		}
//		if (this.type == SolipsisProtocol.FIFO || this.type == SolipsisProtocol.LRU){ 
			this.cacheSize = Configuration.getInt(prefix+"."+CACHE_SIZE);
			this.limite = Configuration.getInt(prefix+"."+CACHE_LIMIT);
			this.time_limite = Configuration.getInt(prefix+"."+TIME_LIMIT);
			this.limite_connaissance = Configuration.getInt(prefix+"."+LIMIT_CONNAISSANCE);

			this.update_time = Configuration.getInt(prefix+"."+UPDATE_TIME);
			Globals.update_time = this.update_time;
			this.update_ok = Configuration.getInt(prefix+"."+UPDATE_OK);
			Globals.update_ok = update_ok;
			this.contact_node = Configuration.getInt(prefix+"."+CONTACT_NODE);
			this.prefetch_ameliore = Configuration.getInt(prefix+"."+CONTACT_NODE);

			this.help_ok = Configuration.getInt(prefix+"."+HELP_OK);
			this.cacheDebug = Configuration.getInt(prefix+"."+CACHE_DEBUG);
			this.strategieCache = Configuration.getInt(prefix+"."+CACHE_STRATEGIE);
			this.cache = new CacheModule(null, null, this.cacheSize, this.strategieCache,this,this.limite_connaissance);
			
			if ( Globals.affichage_options == 0){
				Globals.affichage_options ++;
				fichier3 = new File("Stats_EndTime.txt");
				fichier3.createNewFile();
				fw3 = new FileWriter("Stats_EndTime.txt",true);
				String options = "Limite du cache: " + this.limite + ", CacheSize: " + this.cacheSize + ", Time_limite " + time_limite + 
				", Durée: " + CommonState.getEndTime() + ", update ok ? " + this.update_ok + ", update time: " +
				this.update_time + ", Help Ok ? " + this.help_ok + ", Contact node ? " + this.contact_node + ", Stratégie: " 
				+ this.strategieCache + ", Limite connaissance: " + this.limite_connaissance + "\n";
				fw3.write(options);
				fw3.close();
			}
//		}
		
	}
	
	
	
	public int getPrefetch_ameliore() {
		return prefetch_ameliore;
	}



	public void setPrefetch_ameliore(int prefetchAmeliore) {
		prefetch_ameliore = prefetchAmeliore;
	}



	public int getCacheDebug() {
		return cacheDebug;
	}



	public void setCacheDebug(int cacheDebug) {
		this.cacheDebug = cacheDebug;
	}



	public int estimateHops(SolipsisProtocol peer, boolean useLongRange) {
		return this.estimateHops(peer, false, useLongRange);
	}
	
	public int estimateHops (SolipsisProtocol peer, boolean loopback, boolean useLongRange) {
		int hops;
		
		hops = jumpingToPositionConnectionAlgorithm(this, true, 1, peer.getVirtualEntity().getCoord(), peer.getVirtualEntity().getId(), useLongRange);
		if (!loopback) {
			if (hops == -1) {
				hops = peer.estimateHops(this, true);
			}
		}
		return hops;
	}
	
	public int longRangeSetSize() {
		return this.smallWorldModule.getLinks().size();
	}
	
	
	public void lookup(Lookup request) {
		Globals.totalUse++;
		System.out.println("*********+++++++++++totalUse: " + Globals.totalUse);
		NeighborProxy localClosest;
		long[] neighborCoord, myPosition, destination;
		double currentDistance, neighborMinDistance;
		LinkedList<NeighborProxy> around;
		int direction;

		destination = request.getCoordinates();
		if (VirtualWorld.samePosition(destination, this.getPosition())) {
			if (request.getKind() == Lookup.JOIN) {
				if (this.getVirtualEntity().getId() != request.getSource().getId()) {
					around = this.myNeighbors();
					request.setAround(around);
					this.sendLookupReplyMessage(request.getSource(), request);
					return;
				} 
			} else {
				this.sendLookupReplyMessage(request.getSource(), request);
				return;
			}
		}
		myPosition = this.realRelativeCoord(this.getPosition(),destination);
		localClosest = this.chooseClosestNeighbor(destination);
		if (localClosest != null) {
			neighborCoord = this.realRelativeCoord(localClosest.getCoord(), destination);
			neighborMinDistance = VirtualWorld.simpleDistance(neighborCoord, destination);
			currentDistance = (myPosition==null)?Double.MAX_VALUE:VirtualWorld.simpleDistance(myPosition, destination);

			if (neighborMinDistance < currentDistance) {
				if (this.isLongRange(localClosest)) {
					Globals.longRangeUse++;
				}
				this.sendFindNearestMessage(request, localClosest);
			} else {
				if(this.simpleLeftFromLine(destination, myPosition, neighborCoord)) {
					direction = VirtualWorld.LEFT;
				} else {
					direction = VirtualWorld.RIGHT;
				}
				request.setSupposedGlobalClosest(myPosition);
				around = new LinkedList<NeighborProxy>();
				around.add(this.createMyImage());
				request.setAround(around);
				request.setDirection(direction);
				this.sendQueryAroundMessage(request, localClosest);
			}
		}
	}
	
	
	private boolean isLongRange(NeighborProxy localClosest) {
		return localClosest.getQuality() == NeighborProxy.LONGRANGE;
	}

	private LinkedList<NeighborProxy> myNeighbors() {
		Iterator it;
		LinkedList<NeighborProxy> list;
		
		it = this.proxies.entrySet().iterator();
		list = new LinkedList<NeighborProxy>();
		while (it.hasNext()) {
			list.add((NeighborProxy)((Map.Entry)it.next()).getValue());
		}
		return list;
	}
	

	

	
	public void lookup(long[] destination, int kind) {
		Lookup request;
		
		if (kind == Lookup.JOIN) {
			this.status = "Joining";
			request = new Lookup(Lookup.JOIN, this.createMyImage(),destination);
		} else {
			this.status = "Lookup";
			request = new Lookup(Lookup.LOOKUP, this.createMyImage(),destination);
		}
		this.lookup(request);
	}
	
	private void sendQueryAroundMessage(Lookup request, NeighborProxy localClosest) {
		Message msg;
		
		msg = new Message(Message.QUERYAROUND, this.getPeersimNodeId(), this.getVirtualEntity().getId(), localClosest.getId(), request);
		this.send(msg, localClosest);	
	}

	private void sendFindNearestMessage(Lookup request, NeighborProxy localClosest) {
		Message msg;
		
		msg = new Message(Message.FIND_NEAREST, this.getPeersimNodeId(), this.getVirtualEntity().getId(), localClosest.getId(), request);
		this.send(msg, localClosest);
	}

	private NeighborProxy chooseClosestNeighborAround(long[] destination, LinkedList<NeighborProxy> around, int direction) {
		NeighborProxy current;
		double currentDist;
		NeighborProxy nearest;
		double min = Double.MAX_VALUE;
		long [] myCoord, currentCoord;
		boolean sameDirection;
		Iterator entitySet;
		
		NeighborProxy longest;
		double currentLen;
		double maxLen, dist;
		long [] longestCoord;
		Iterator neighbors;
		LinkedList<NeighborProxy> tested;
		
		nearest = null;
		tested = new LinkedList<NeighborProxy>();
		switch(this.routeAlgorithm) {
		case SolipsisProtocol.LONGLINKSFIRST:
			myCoord = this.realRelativeCoord(this.getPosition(), destination);
			entitySet = this.getNeighbors();//this.proxies.entrySet().iterator();
			while(entitySet.hasNext()) {
				current = (NeighborProxy)((Map.Entry)entitySet.next()).getValue();
				currentCoord = this.realRelativeCoord(current.getCoord(), destination);

				sameDirection = ((direction == VirtualWorld.RIGHT) && this.simpleRightFromLine(destination, myCoord, currentCoord)) ||
				((direction == VirtualWorld.LEFT)  && this.simpleLeftFromLine(destination, myCoord, currentCoord));
				if(sameDirection && notIn(current,around)) {
					currentDist = VirtualWorld.simpleDistance(destination,currentCoord);
					if (currentDist < min) {
						min = currentDist;
						nearest = current;
					}
				}			
				//				System.out.println(sameDirection +" "+ notIn(current,around));
			}
			longestCoord = null;
			longest = null;
			dist = 0;
			do {
				maxLen = -1;
				neighbors = this.getNeighbors();//this.proxies.entrySet().iterator();
				while(neighbors.hasNext()) {
					current = (NeighborProxy)((Map.Entry)neighbors.next()).getValue();
					if (this.notIn(current, tested)) {
						currentCoord = this.realRelativeCoord(current.getCoord(), this.getPosition());

						currentLen = VirtualWorld.simpleDistance(currentCoord,this.getPosition());
						currentDist = VirtualWorld.simpleDistance(this.realRelativeCoord(current.getCoord(), destination),destination);
						if (currentLen > maxLen) {
							maxLen = currentLen;
							longest = current;
							longestCoord = currentCoord;
							dist = currentDist;
						}
					}
				}
				tested.add(longest);
			} while(dist > 100 * min);
			nearest = longest;
			break;
		default:
			myCoord = this.realRelativeCoord(this.getPosition(), destination);
			entitySet = this.getNeighbors();//this.proxies.entrySet().iterator();
			while(entitySet.hasNext()) {
				current = (NeighborProxy)((Map.Entry)entitySet.next()).getValue();
				currentCoord = this.realRelativeCoord(current.getCoord(), destination);

				sameDirection = ((direction == VirtualWorld.RIGHT) && this.simpleRightFromLine(destination, myCoord, currentCoord)) ||
				((direction == VirtualWorld.LEFT)  && this.simpleLeftFromLine(destination, myCoord, currentCoord));
				if(sameDirection && notIn(current,around)) {
					currentDist = VirtualWorld.simpleDistance(destination,currentCoord);
					if (currentDist < min) {
						min = currentDist;
						nearest = current;
					}
				}			
			}
		}
		return nearest;
	}
	


	@SuppressWarnings("unchecked")
	private NeighborProxy chooseClosestNeighbor(long[] position) {	
		NeighborProxy current, nearest, longest;
		double currentLen, currentDist;
		double min, maxLen, dist;
		long [] currentCoord, longestCoord;
		Iterator neighbors;
		LinkedList<NeighborProxy> tested;

		nearest = null;
		tested = new LinkedList<NeighborProxy>();
		switch (this.routeAlgorithm) {
		case SolipsisProtocol.LONGLINKSFIRST:
			min = Double.MAX_VALUE;
			neighbors = this.getNeighbors();//this.proxies.entrySet().iterator();
			while(neighbors.hasNext()) {
				current = ((Map.Entry<Integer, NeighborProxy>)neighbors.next()).getValue();
				currentCoord = this.realRelativeCoord(current.getCoord(), position);

				currentDist = VirtualWorld.simpleDistance(currentCoord,position);
				if (currentDist < min) {
					min = currentDist;
					nearest = current;
				}
			}
			longestCoord = null;
			longest = null;
			dist = 0;
			do {
				maxLen = -1;
				neighbors = this.getNeighbors();//this.proxies.entrySet().iterator();
				while(neighbors.hasNext()) {
					current = (NeighborProxy)((Map.Entry)neighbors.next()).getValue();
					if (this.notIn(current, tested)) {
						currentCoord = this.realRelativeCoord(current.getCoord(), this.getPosition());

						currentLen = VirtualWorld.simpleDistance(currentCoord,this.getPosition());
						currentDist = VirtualWorld.simpleDistance(this.realRelativeCoord(current.getCoord(), position),position);
						if (currentLen > maxLen) {
							maxLen = currentLen;
							longest = current;
							longestCoord = currentCoord;
							dist = currentDist;
						}
					}
				}
				tested.add(longest);
			} while(dist > 100 * min);
			nearest = longest;
			break;
		default:
			min = Double.MAX_VALUE;
			neighbors = this.getNeighbors();//this.proxies.entrySet().iterator();
			while(neighbors.hasNext()) {
				current = (NeighborProxy)((Map.Entry)neighbors.next()).getValue();
				currentCoord = this.realRelativeCoord(current.getCoord(), position);

				currentDist = VirtualWorld.simpleDistance(currentCoord,position);
				if (currentDist < min) {
					min = currentDist;
					nearest = current;
				}
			}
			break;
		}
		return nearest;
	}
	
	public long[] realRelativeCoord(long[] distant, long[] local) {
			return this.moduloModificationRule(local, distant);
	}

	public HashMap<Integer, NeighborProxy> getLongRangeLinks() {
		return this.smallWorldModule.getLinks();
	}
	
	public NeighborProxy getLongRangeLink() {
		Iterator it;
		NeighborProxy longRange;
		
		longRange = null;
		it = this.smallWorldModule.getLinks().entrySet().iterator();
		if (it.hasNext()) {
			longRange = (NeighborProxy)((Map.Entry)it.next()).getValue();
		}
		
		return longRange;
	}
	
	private HashMap<Integer, NeighborProxy> getLongRangeLinks(NeighborProxy proxy) { //TODO
		SolipsisProtocol peer = (SolipsisProtocol)Network.get(proxy.getPeersimNodeId()).getProtocol(this.protocolId);
		
		return peer.smallWorldModule.getLinks();
	}

	private Iterator getNeighbors(NeighborProxy proxy) {
		SolipsisProtocol peer = (SolipsisProtocol)Network.get(proxy.getPeersimNodeId()).getProtocol(this.protocolId);
		return peer.getProxies().entrySet().iterator();
	}
	
	public void activateWiring() {
		this.smallWorldModule.activateWiring(this.proxies);
	}
	
	public void deactivateWiring() {
		this.smallWorldModule.deactivateWiring();
	}
	
	public boolean hasDetected(Integer id) {
		return this.detected.containsKey(id);
	}
	
	public long detectionTime(Integer id) {
		return this.detected.get(id).longValue();
	}
	
	public void setDetected(Integer id, long time) {
		this.detected.put(id, new Long(time));
	}
	
	private void removeDetected(Integer id) {
		this.detected.remove(id);
	}
	
	/**
	 * Retuns <code>this</code>. This way only one instance exists in the system
	 * that is linked from all the nodes. This is because this protocol has no
	 * node specific state.
	 */
	@Override
	public Object clone()
	{
		SolipsisProtocol dolly = null;
		try {
			dolly = new SolipsisProtocol(this.prefix);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dolly;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
		
	public void setConnectionTime(int id) {
		this.connectionTime.put(id, CommonState.getTime());
	}
	
	public void clearConnectionTime(int id) {
		this.connectionTime.remove(id);
	}
	
	public HashMap<Integer,Long> getConnectionTimes() {
		return this.connectionTime;
	}
	
	public boolean stateUpdateTimerReady() {
		return (CommonState.getTime() - this.sendStateUpdateTimer) >= this.timerInterval;
	}
	
	public void rearmStateUpdateTimer() {
		if (this.timerInterval == 0 && Globals.topologyIsReady) {
			this.timerInterval = Long.MAX_VALUE;
		}
		this.sendStateUpdateTimer = CommonState.getTime();
	}
	
	public int getState() {
		return this.mainVirtualEntity.getState();
	}
	
	private Message statePropagationMessage(NeighborProxy dest) {
		GeometricRegion knowledgeArea = new GeometricRegion(this.getPosition(), this.getKnowledgeRay());
		knowledgeArea.setState(this.getState());
		Message update = new Message(Message.DELTA, this.peersimNodeId, this.mainVirtualEntity.getId(), dest.getId(), knowledgeArea);
		
		return update;
	}
	
	public void setStateUpdateTimer(int state) {
		switch(state) {
		case MobilityStateMachine.HALTED:
			this.timerInterval = SolipsisProtocol.TRAVELLING_UPDATE_INTERVAL;//Long.MAX_VALUE;
			break;
		case MobilityStateMachine.TRAVELLING:
			if (this.type == SolipsisProtocol.ENHANCED) {
				this.timerInterval = SolipsisProtocol.TRAVELLING_UPDATE_INTERVAL;
			} else {
				this.timerInterval = SolipsisProtocol.WANDERING_UPDATE_INTERVAL;
			}
			break;
		case MobilityStateMachine.WANDERING:
			this.timerInterval = SolipsisProtocol.WANDERING_UPDATE_INTERVAL;
			break;
		}

	}
	
	public HashMap<Integer,NeighborProxy> getProxies() {
		return this.proxies;
	}
	
	public void setPeersimNodeId(int id) {
		this.peersimNodeId = id;
		this.transportLayer = (GenericTransportLayer) Network.get(id).getProtocol(this.transport);
	}
	
	public void updatePeersimNodeId(int id) {
		this.peersimNodeId = id;
	}
	
	public int getPeersimNodeId() {
		return this.peersimNodeId;
	}
	
	public Node solipsisToPeersimNode(SolipsisProtocol entity) {
		int peersimNodeId = entity.getPeersimNodeId();
		return Network.get(peersimNodeId);
	}
	
	public Node proxyToPeersimNode(NeighborProxy entity) {
		int peersimNodeId = entity.getPeersimNodeId();
		return Network.get(peersimNodeId);
	}
	
	public void setVirtualEntity(VirtualEntity avatar) {
		
		this.mainVirtualEntity = avatar;
		if (this.type == SolipsisProtocol.SMALLWORLD) {
			this.smallWorldModule  = new SmallWorld(this.mainVirtualEntity, Globals.smallWorldLinkNb);
		}
	}
	
	public void join(SolipsisProtocol bootstrap) {
		long[] goal;
		
		this.status = "Joining";
		if (Globals.topologyIsReady) {
			this.addLocalView(bootstrap.createMyImage());
			goal = this.getPosition();
			lookup(goal, Lookup.JOIN);
		} else {
			jumpingToPositionConnectionAlgorithm(bootstrap, false, 0, null, 0, true);
		}
	}
	
	@Override
	public String toString() {
		String msg = "Solipsis Protocol";
		
		if (mainVirtualEntity == null) {
			msg += this.status;
		} else {
			msg += ": " + this.mainVirtualEntity + " (" + this.status + ")";
		}
		
		return msg;
	}
	
	
	public long[] getPosition() {
		return this.mainVirtualEntity.getCoord();
	}
	
	public VirtualEntity getVirtualEntity() {
		return this.mainVirtualEntity;
	}
	
	public boolean isInsideKnowledgeZone(NeighborProxy entity) {
		return VirtualWorld.simpleDistance(this.subjectiveCoord(entity.getCoord()),this.mainVirtualEntity.getCoord()) <= this.knowledgeRay;
	}
	
	public boolean isInsideKnowledgeZone(VirtualEntity entity) {
		return VirtualWorld.simpleDistance(this.realRelativeCoord(entity.getCoord()),this.mainVirtualEntity.getCoord()) <= this.knowledgeRay;
	}
	
	public double getKnowledgeRay() {
		return this.knowledgeRay;
	}
	
	public int getType() {
		return this.type;
	}
	
	public PrefetchingModule getPrefetchingModule() {
		return this.prefetchingModule;
	}
	
	/* ajout d'un nouveau proxies */
	public void addLocalView(VirtualEntity entity) {
		NeighborProxy view = new NeighborProxy(entity.getCoord(), entity.getKnowledgeRay(), entity.getId(), entity.getProtocol().getPeersimNodeId());
		this.proxies.put(view.getId(), view);
		view.setTime(CommonState.getTime());

	}
	
	/* ajout d'un nouveau proxies */
	public void addLocalView(NeighborProxy entity) {
		NeighborProxy view = entity.clone();
		this.proxies.put(view.getId(), view);
		view.setTime(CommonState.getTime());
	}
	
	public void addLocalViewNoCache(NeighborProxy entity) {
		NeighborProxy view = entity.clone();
		this.proxies.put(view.getId(), view);
	}

	private int count() {
		LinkedList<Integer> regular = this.getParticularNeighbors(NeighborProxy.REGULAR);
		NeighborProxy proxy;
		int counter = 0;
		int size = regular.size();
		
		for (int i = 0; i < size; i++) {
			proxy = this.proxies.get(regular.get(i));
			if(isInsideKnowledgeZone(proxy)) {
				counter++;
			}
		}
		
		return counter;
	}
	
	private LinkedList<NeighborProxy> iteratorToList(Iterator it) {
		LinkedList<NeighborProxy> list = new LinkedList<NeighborProxy>();
		
 		while (it.hasNext()) {
			list.add((NeighborProxy)((Map.Entry)it.next()).getValue());
		}
 		
 		return list;
	}
	
	private LinkedList<NeighborProxy> idToProxyList(LinkedList<Integer> list) {
		LinkedList<NeighborProxy> proxyList = new LinkedList<NeighborProxy>();
		int size = list.size();
		
		for (int i = 0; i < size; i++) {
			proxyList.add(this.proxies.get(list.get(i)));
		}
		
		return proxyList;
	}
	
	private LinkedList<NeighborProxy> orderList(Iterator it) {
		LinkedList<NeighborProxy> messedUp = iteratorToList(it);
		return orderList(messedUp);
	}
	
	private HashMap<Integer, NeighborProxy> mergeSets(HashMap<Integer, NeighborProxy> set1, HashMap<Integer, NeighborProxy> set2) {
		HashMap<Integer, NeighborProxy> merged;
		NeighborProxy current;
		Iterator it;
		
		merged = new HashMap<Integer, NeighborProxy>();
		it = set1.entrySet().iterator();
		while (it.hasNext()) {
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			merged.put(current.getId(), current);
		}
		
		it  = set2.entrySet().iterator();
		while (it.hasNext()) {
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			merged.put(current.getId(), current);
		}
		
		return merged;
	}
	
	private HashMap<Integer, NeighborProxy> mergeSets(HashMap<Integer, NeighborProxy> set1, LinkedList<NeighborProxy> set2) {
		HashMap<Integer, NeighborProxy> merged;
		NeighborProxy current;
		Iterator it;
		int size;
		
		merged = new HashMap<Integer, NeighborProxy>();
		it = set1.entrySet().iterator();
		while (it.hasNext()) {
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			merged.put(current.getId(), current);
		}
		
		size = set2.size();
		for (int i = 0; i < size; i++) {
			current = set2.get(i);
			merged.put(current.getId(), current);
		}
		
		return merged;
	}
	
	private LinkedList<NeighborProxy> orderList(LinkedList messedUp) {
		LinkedList<NeighborProxy> ordered = new LinkedList<NeighborProxy>();
		int size = messedUp.size();
		boolean [] bitmap = new boolean[size];
		double minDist,currentDist;
		NeighborProxy currentProxy, minProxy = null;
		int index = -1;
		
		if ((size > 0) && (messedUp.get(0) instanceof Integer)) {
			messedUp = idToProxyList(messedUp);
		}
		
		for (int i = 0; i < size; i++) {
			minDist = Long.MAX_VALUE;
			for (int j = 0; j < size; j++) {
				if (!bitmap[j]) {
					currentProxy = (NeighborProxy)messedUp.get(j);
					currentDist = VirtualWorld.simpleDistance(this.subjectiveCoord(currentProxy.getId()),this.mainVirtualEntity.getCoord());
					if (currentDist < minDist) {
						minDist = currentDist;
						minProxy = currentProxy;
						index = j;
					}
				}
			}
			bitmap[index] = true;
			ordered.add(i, minProxy);
		}
		
		return ordered;
	}
	
	private double dist(int order) {
		LinkedList<NeighborProxy> ordered = orderList(this.getParticularNeighbors(NeighborProxy.REGULAR));//this.proxies.entrySet().iterator());
		return VirtualWorld.simpleDistance(this.subjectiveCoord(ordered.get(order).getId()),this.mainVirtualEntity.getCoord());
	}
	
	private boolean isInHalfPlan(NeighborProxy convexFail, long [] entityCoord) {
		long[] convexFailCoord = this.subjectiveCoord(convexFail.getId());
		
		return simpleAngleSign(convexFailCoord, this.mainVirtualEntity.getCoord(), entityCoord) > 0;
	}
	

	public boolean isInHalfPlan(GeometricRegion line, NeighborProxy entity) {
		return isInHalfPlan(line, this.subjectiveCoord(entity.getCoord()));
	}
	
	private boolean isInHalfPlan(GeometricRegion line, long[] coord) {
		long [] origin = this.subjectiveCoord(line.getFirstCoord());
		long [] convexFail = this.subjectiveCoord(line.getSecondCoord());
		boolean answer;
		
		if (line.getOri() == VirtualWorld.LEFT) {
			answer = simpleAngleSign(convexFail, origin, coord) > 0;
		} else {
			answer = simpleAngleSign(convexFail, origin, coord) < 0;
		}
		return answer;
	}
	
	
	public long getLatency() {
		return this.transportLayer.getLatency();
	}
	
	public void processEvent( Node node, int pid, Object event ) {
		receive((Message)event,(SolipsisProtocol)node.getProtocol(pid));
	}
	
	
	/*
	 * Fonction solipsisRecoverTopology:
	 * 	Point d'entrée pour l'utilisation du cache, on fait appel 
	 * 	à la fonction maintainCacheTopology si le nœud est dans 
	 * 	l'état Wandering
	 */
	private void solipsisRecoverTopology(NeighborProxy [] sector) {

		GeometricRegion line;
		int [][] moduloCoefs;
		Message recoverMsg = null;
		NeighborProxy sendTo;
		int find = 0;;

	
		

//		if (find == 0){
			if (sector != null) {
				if (sector[0] != null) {
					sendTo = sector[0];
					long [] a = this.mainVirtualEntity.getCoord();
					long [] b = this.subjectiveCoord(sendTo.getId());
					line  = new GeometricRegion(this.mainVirtualEntity.getCoord(), this.subjectiveCoord(sendTo.getId()));

					if (sector[1] != null && simpleAngleSign(this.subjectiveCoord(sector[0].getId()), this.mainVirtualEntity.getCoord(), this.subjectiveCoord(sector[1].getId())) < 0) {
						line.setOri(VirtualWorld.LEFT);
					} else {
						line.setOri(VirtualWorld.RIGHT);
					}
					
					
					if ( this.mainVirtualEntity.getState() == MobilityStateMachine.WANDERING ){
						find = maintainCacheTopology(line);
					}
					
//					if ( find == 0 ){
					
						
						this.searchInProgress = true;
						Node n = Network.get(this.getPeersimNodeId());
						
						if ( help_ok == 1 ){
							CacheHelpRequest request = new CacheHelpRequest(line, this.convexEnvelope,this.mainVirtualEntity.getCoord(),this.knowledgeRay);
							recoverMsg = new Message(Message.SEARCH_HELP, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), sendTo.getId(), request);
						}else{
							recoverMsg = new Message(Message.SEARCH, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), sendTo.getId(), line);
						}
						this.send(recoverMsg, sendTo);
//					}
				}
			}
//		}
	}


	private NeighborProxy searchForAppropriateNeighbor(GeometricRegion region) {
		LinkedList<Integer> neighbors = this.getParticularNeighbors(NeighborProxy.REGULAR);
		NeighborProxy current, appropriate = null;
		LinkedList<NeighborProxy> choices = new LinkedList<NeighborProxy>();
		Random rand = new Random();
		int size = neighbors.size();

		
		for (int i = 0; i < size; i++) {
			current = this.proxies.get(neighbors.get(i));
			if (isInHalfPlan(region,current)) {
				choices.add(current);
			}
		}	
		size = choices.size();
		if (size > 0) {
			appropriate = choices.get(rand.nextInt(size));
		}
		
		return appropriate;
	}
	
	public Message createFoundMsg(NeighborProxy neighbor, int destination) {
		return new Message(Message.FOUND, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), destination, neighbor.clone());
	}

	
	/*****************************************************/
				/****Fonctions pour Update ****/
	/*****************************************************/
	
	
	/*
	 * Fonction processUpdateCaheREQ:
	 * 	Traite la réception d'un message de type UPDATE_CACHE_REQ
	 * 	Met dans les objets du message les informations nécessaires
	 * 	à la mise à jour de u nœud dans le cache 
	 * 
	 */
	public void processUpdateCaheREQ(Message msg) {
		
		NeighborProxy source = (NeighborProxy)msg.getContent();
		Message updateResponse = new Message(Message.UPDATE_CACHE_REP, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), source.getId(), this.mainVirtualEntity);
		this.send(updateResponse, msg.getOriginAddress());
		
	}
	
	
	/*
	 * Fonction processUpdateCaheREP:
	 * 	Traite la reception d'un message de type UPDATE_CACHE_REP
	 * 	Met à jour les coordonnées de la données du cache.
	 * 
	 */
	public void processUpdateCaheREP(Message msg) {

		VirtualEntity update = (VirtualEntity)msg.getContent();
		if (((NeighborProxy)this.cache.getCache().get(msg.getDestination())) != null){
			if (cacheDebug == 1){
				this.cache.ShowCache();
				System.out.println("ancienne Coord: " + ((NeighborProxy)this.cache.getCache().get(msg.getDestination())).getCoord()[0] + ", " + 
						((NeighborProxy)this.cache.getCache().get(msg.getDestination())).getCoord()[1] + ", id: " + update.getId());
			}
			((NeighborProxy)this.cache.getCache().get(msg.getDestination())).setCoord(update.getCoord());
			if (cacheDebug == 1){
				System.out.println("New Coord: " + ((NeighborProxy)this.cache.getCache().get(update.getId())).getCoord()[0] + ", " + 
					((NeighborProxy)this.cache.getCache().get(update.getId())).getCoord()[1] + ", id: " + update.getId());
			}
		}
	}

	/*
	 * Fonction CacheUpdate:
	 * 	Va lancer la mis à jour du cache, on fait une mise à 
	 * 	jour que sur les nœuds qui ont des données vieilles de
	 * 	de plusde la moitié de la limite de update
	 *  
	 */
	public void CacheUpdate(){
		NeighborProxy current;
		Iterator it;

		if (update_ok == 1){
			it = this.cache.getCache().entrySet().iterator();

			while(it.hasNext()){
				Globals.cacheEvaluator.incnbMessUpdateTot();
				current = (NeighborProxy)((Map.Entry)it.next()).getValue();
				if ( current.getTime() + ( this.update_time/2) < CommonState.getIntTime() ){
					Message updateRequest = new Message(Message.UPDATE_CACHE_REQ, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), current.getId(), current);
					this.send(updateRequest, current);
				}
			}
		}
		
	}
	
	
	/*****************************************************/
	/***** Fonctions pour le traitement des messages *****/
	/****************************************************=/
	
	
	/*
	 * Fonction processSearch:
	 * 	traite les messages de type SEARCH et SEARCH_HELP
	 * 
	 */
	private void processSearchMsg(Message msg) {
		GeometricRegion line;
		int srcId = msg.getSource();
		NeighborProxy neighbor = null;
		NeighborProxy source = this.proxies.get(msg.getSource());
		Message response = null;
		VirtualEntity stranger;
		long [] origin;
		Node n;
		int find = 0;

		switch (msg.getType()) {
		
			case Message.SEARCH_HELP:
				CacheHelpRequest request = (CacheHelpRequest) msg.getContent();
				line = (GeometricRegion)request.getRegion();
				
				
				
							
					neighbor = searchForAppropriateNeighbor(line);
					if (neighbor != null) {
						response = createFoundMsg(neighbor, msg.getSource());
						if (source == null) {
							n = Network.get(msg.getOriginAddress());
							if (n != null) {
								stranger = ((SolipsisProtocol)n.getProtocol(this.protocolId)).getVirtualEntity();
								this.send(response, stranger);
							}
						} else {
							this.send(response, source);
						}
						Globals.cacheEvaluator.incnbSearchHelpHIT();

					}else{
						Globals.cacheEvaluator.incnbSearchHelpMISS();
					}
					//TODO TESTER
					if (cacheDebug == 1){
						System.out.println("Destination: " + ((CacheHelpRequest)msg.getContent()).getDestination()[0] + ", "  + ((CacheHelpRequest)msg.getContent()).getDestination()[1]);
					}
					if ( help_ok == 1){
						if (this.strategieCache == SolipsisProtocol.FIFO && this.mainVirtualEntity.getState() == MobilityStateMachine.WANDERING){
							find = HelpNeighborCache(msg);
							
						}
					}
			
				
				break;
	
			case Message.SEARCH:
				line = (GeometricRegion) msg.getContent();
				
				neighbor = searchForAppropriateNeighbor(line);
				if (neighbor != null) {
					response = createFoundMsg(neighbor, msg.getSource());
					if (source == null) {
						n = Network.get(msg.getOriginAddress());
						if (n != null) {
							stranger = ((SolipsisProtocol)n.getProtocol(this.protocolId)).getVirtualEntity();
							this.send(response, stranger);
						}
					} else {
						this.send(response, source);
					}
				}
				
				break;
				
			default:
				break;
		}
		
	}
	
	
	/*
	 * Fonction HelpNeighborCache:
	 * 	elle est appelée par la fonction processSearchMsg
	 * 	Elle va regarder dans le cache si un ou plusieurs
	 * 	nœud(s) (en fonction de la stratégie) peu(t/vent)
	 * 	aider le nœud qui a émis le message.
	 * 	Si aucun nœud trouvé dans le cache, elle effectue
	 *  le traitement de base.
	 */	
	public int HelpNeighborCache(Message msg) {
		Node n;
		NeighborProxy neighbor = null;
		
		Globals.cacheEvaluator.incnbCachePassClob();
		CacheHelpRequest request = (CacheHelpRequest)msg.getContent();
		NeighborProxy source = this.proxies.get(msg.getSource());
		n = Network.get(msg.getOriginAddress());
		VirtualEntity source1 = ((SolipsisProtocol)n.getProtocol(this.protocolId)).getVirtualEntity();
		HashMap<Integer,NeighborProxy> responseMult = new HashMap<Integer, NeighborProxy>();

			
			switch (this.strategieCache) {
			case SolipsisProtocol.FIFO:
				
						
						
						if (request != null){
							switch(limite){
							case -1: 
								neighbor = cache.searchCacheHelpNeighbor(request,msg.getSource());
								break;
							default:
								neighbor = cache.searchCacheHelpNeighbor(request,msg.getSource());
								break;
							}
						}
						
						if (neighbor != null){
							if (cacheDebug == 1){
								System.out.println("----------------------------------------");
								System.out.println("--- On a trouvé un nœud dans le cache: " + neighbor.getId() + " ---");
								System.out.println("----------------------------------------");
							}

							if ( neighbor.getTime() + time_limite > CommonState.getIntTime()){
							
								if (cacheDebug == 1){
									System.out.println("HELP HITTTTTTTTTTTTT");
								}
								Message response = new Message(Message.CACHE_HELP_REP, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), msg.getSource(), neighbor);
								if(source == null){
									System.out.println("sourceestnull");
									source = this.cache.getCache().get(msg.getSource());
								}
								if(source == null){
									System.out.println("========================= ++++++++++++++++ sourceestnullagain");
//									this.send(response,source1);
									this.send(response,msg.getOriginAddress());

								}else{
									this.send(response,source);
								}
								/*************************************/
								this.stabilized = true;
								this.searchInProgress = false;
								return 1;
								
							}else{
								Globals.cacheEvaluator.incCacheMissHelpGlob();
								return 0;
							}
							
							}else{
								if (cacheDebug == 1){
									System.out.println("HELP MISSSSSSSSSSSSS");
								}
								Globals.cacheEvaluator.incCacheMissHelpGlob();
								return 0;
							}
			case SolipsisProtocol.FIFOMULT:
				int res = 0;
				if (request != null){
					switch(limite){
					case -1: 
						responseMult = cache.searchCacheHelpNeighborMult(request,msg.getSource());
						break;
					default:
						responseMult = cache.searchCacheHelpNeighborMult(request,msg.getSource());
						break;
					}
				}
				
				Iterator it;
				it = responseMult.entrySet().iterator();
				if ( responseMult.size() != 0 ){
					while(it.hasNext()){
						neighbor = (NeighborProxy)((Map.Entry)it.next()).getValue();
						if (neighbor != null){
																	
							if ( neighbor.getTime() + time_limite > CommonState.getIntTime()){
								
								if (cacheDebug == 1){
									System.out.println("HELP HITTTTTTTTTTTTT");
								}
								Message response = new Message(Message.CACHE_HELP_REP, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), msg.getSource(), neighbor);
								if(source == null){
									System.out.println("sourceestnull");
									source = this.cache.getCache().get(msg.getSource());
								}
								if(source == null){
									System.out.println("sourceestnullagain");
									this.send(response,msg.getOriginAddress());
								}else{
									this.send(response,source);
								}
								res = 1;	
							}else{
								Globals.cacheEvaluator.incCacheMissHelpGlob();
							}
						}else{
							if (cacheDebug == 1){
								System.out.println("HELP MISSSSSSSSSSSSS");
							}
							Globals.cacheEvaluator.incCacheMissHelpGlob();
						}	
					}
				}
				return res;
				
			default:
				return 0;
			}
	}
	
		
	
	/*
	 * Fonction MaintainCacheTopology:
	 * 	Va regarder dans le cache si un nœud de ce dernier est proche 
	 * 	de la destination du nœud courant. Si un nœud est trouvé, nous 
	 * 	allons alors le contacter avec un message de type CACHE_UPD et 
	 * 	changer la Quality du nœud courant. Si nous ne trouvons pas de 
	 * 	nœud dans le cache, nous incrémentons les caches MISS
	 * 
	 */
	public int maintainCacheTopology(GeometricRegion region) {
		Node n;
		NeighborProxy neighbor = null;
		HashMap<Integer,NeighborProxy> responseMult = new HashMap<Integer, NeighborProxy>();

		Globals.cacheEvaluator.incnbCachePassClob();
		
		if ( this.mainVirtualEntity.getStateMachine().getState() == MobilityStateMachine.WANDERING ) {
//			System.err.println(("STRATEGIE: " + this.strategieCache));
			n = Network.get(this.getPeersimNodeId());
			long[] destination = ((SolipsisProtocol)n.getProtocol(this.protocolId)).getVirtualEntity().getCoord();
			long [] destinationsubj = this.subjectiveCoord(destination);

			switch (this.strategieCache) {
			case SolipsisProtocol.FIFO:

			
						
						/* 
						 * Recherche dans le cache 
						 * Limite en fonction de la distance avec les voisins si -1
						 * Limite fixe et avec la valeur de limite de config si différent de -1
						 * 
						 */
						if (destination != null){
							
							switch(limite){
							case -1: 
								neighbor = cache.searchCacheNeighborLimitNeighbor(destinationsubj, this.cache.getCache());
								break;
							
							
								
							default:
								
//								neighbor = cache.searchCacheNeighborLimit(destination,limite);
								neighbor = cache.searchCacheNeighborKnoledgeRay(destinationsubj,this.knowledgeRay);
//								neighbor = cache.searchCacheNeighborRegion(destinationsubj,region);
								break;
								
							}
						}
					
						/* 
						 * On vérifie que le nœud ne soit pas null, ce qui signifie
						 * que l'on a trouvé un nœud dans le cache, il faut alors le 
						 * contacter pour savoir si il a bougé par rapport à la position
						 * que l'on a dans le cache
						 *  
						 */
						
					
						
						if (neighbor != null){
							if (cacheDebug == 1){
								System.out.println("----------------------------------------");
								System.out.println("--- On a trouvé un nœud dans le cache: " + neighbor.getId() + " ---");
								System.out.println("----------------------------------------");
							}
						
							
							
							if ( neighbor.getTime() + time_limite > CommonState.getIntTime()){
								
								boolean aide = this.helpfulToEnvelopeCache(neighbor);
								boolean avant = convexEnvelopeProperty();
								
								if (cacheDebug == 1){
									System.out.println("!?aide_ou_pas : " + aide +", myId: " + this.mainVirtualEntity.getId() + ", newId: " + neighbor.getId() + ", time: " + CommonState.getTime());
									System.out.println("!?Envelop state before: " + avant);
									System.out.println("!?My Coords: " + this.mainVirtualEntity.getCoord()[0] + ", " + this.mainVirtualEntity.getCoord()[1]);
									System.out.println("!?Coord: " + neighbor.getCoord()[0] + ", " + neighbor.getCoord()[1]);
								}
								cache.RmCache(neighbor);
								addLocalView(neighbor);
								neighbor.setQuality(NeighborProxy.REGULAR);
								Globals.cacheEvaluator.incCacheHitGLob();
								boolean apres = convexEnvelopeProperty();
								if (cacheDebug == 1){
									System.out.println("!?Envelop State after: " + apres);
								}
								/*************************************/
								this.stabilized = true;
								this.searchInProgress = false;
								return 1;
								
								
							}
							if (contact_node == 1){
								if (cacheDebug == 1){
									System.out.println("====== TROP vieux neigtime: " + neighbor.getTime() +", time: " + CommonState.getIntTime());
								}
	
								Message cache_test;
								CacheRequest cache_request;
								NeighborProxy source = this.createMyImage();
								NeighborProxy neigh = neighbor.clone();
								
								if (source == null){
									if (cacheDebug == 1){
										System.out.println("Erreur: La Source est Null!!");
									}
								}	
								cache_request = new CacheRequest(neigh,source);
								cache_test = new Message(Message.CACHE_UPD, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), neighbor.getId(), cache_request);
					
								neighbor.setQuality(NeighborProxy.CACHED);
								
								this.send(cache_test,neighbor);
								if (cacheDebug == 1 || cacheDebug == 2){
									System.out.println("<" + cache_test.getTypeString() + ", " +  this.mainVirtualEntity.getId() + ", " + neighbor.getId() + ">");
								}
								return 0;
							}else{
								Globals.cacheEvaluator.incCacheMissGlob();
								return 0;
							}
				
						}else{
							Globals.cacheEvaluator.incCacheMissGlob();
							return 0;
						}
						
			case SolipsisProtocol.FIFOMULT:
		
				int res = 0;
//				responseMult = cache.searchCacheNeighborEnvelopEvMult(destination,this.knowledgeRay);
				responseMult = cache.searchCacheNeighborRegionMult(destinationsubj,region);
				Iterator it;
				it = responseMult.entrySet().iterator();
				if ( responseMult.size() != 0 ){
					while(it.hasNext()){
						neighbor = (NeighborProxy)((Map.Entry)it.next()).getValue();
						if (neighbor != null){
																	
							if ( neighbor.getTime() + time_limite > CommonState.getIntTime()){
								
								boolean aide = this.helpfulToEnvelopeCache(neighbor);
								boolean avant = convexEnvelopeProperty();
								
								if (cacheDebug == 1){
									System.out.println("!?aide_ou_pas : " + aide +", myId: " + this.mainVirtualEntity.getId() + ", newId: " + neighbor.getId() + ", time: " + CommonState.getTime());
									System.out.println("!?Envelop state before: " + avant);
									System.out.println("!?My Coords: " + this.mainVirtualEntity.getCoord()[0] + ", " + this.mainVirtualEntity.getCoord()[1]);
									System.out.println("!?Coord: " + neighbor.getCoord()[0] + ", " + neighbor.getCoord()[1]);
								}
								cache.RmCache(neighbor);
								addLocalView(neighbor);
								neighbor.setQuality(NeighborProxy.REGULAR);
								Globals.cacheEvaluator.incCacheHitGLob();
								boolean apres = convexEnvelopeProperty();
								if (cacheDebug == 1){
									System.out.println("!?Envelop State after: " + apres);
								}
								res = 1;
								
								
							}else{
								if (contact_node == 1){

									Message cache_test;
									CacheRequest cache_request;
									NeighborProxy source = this.createMyImage();
									NeighborProxy neigh = neighbor.clone();
									
									if (source == null){
										if (cacheDebug == 1){
											System.out.println("Erreur: La Source est Null!!");
										}
									}	
									cache_request = new CacheRequest(neigh,source);
									cache_test = new Message(Message.CACHE_UPD, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), neighbor.getId(), cache_request);
						
									neighbor.setQuality(NeighborProxy.CACHED);
									
									this.send(cache_test,neighbor);
								}else{
									Globals.cacheEvaluator.incCacheMissGlob();
								}
							}
						}				
					}
					return res;

				}else{
					Globals.cacheEvaluator.incCacheMissGlob();
					return 0;
				}

				
			default:
				return 0;
			}
			
			
		}
		return 0;
	}
	

	/*
	 * Fonction processCacheUpd:
	 * 	Fonction qui va traiter l'arrivée d'un message de type CACHE_UPD,
	 * 	ce message est envoyé par la fonction maintainCacheTopology() qui 
	 * 	sert à chercher dans le cache un nœud. La fonction suivante va 
	 * 	comparer les coordonnées du nœud envoyée et les coordonnées réelles
	 * 	de ce dernier. S'il n'a pas trop bougé, nous pourrons émettre un
	 * 	message CACHE_UPD_REP pour provoquer un cache HIT. Si il a trop 
	 * 	bougé, nous incrémenterons le compteur des caches MIT.
	 * 
	 */
	private void processCacheUpd(Message msg){
		
		NeighborProxy source = ((NeighborProxy)((CacheRequest)msg.getContent()).getSource());

		
		if (compareNeighborProxy((CacheRequest)msg.getContent(), this.mainVirtualEntity )){
			
			if ( cacheDebug == 1 || cacheDebug == 2 ){
				System.out.println("... Comparaison OK: Old " + ((CacheRequest)msg.getContent()).getOldData().getCoord()[0] + "," + ((CacheRequest)msg.getContent()).getOldData().getCoord()[1] +
						", Real " + this.mainVirtualEntity.getCoord()[0] + "," + this.mainVirtualEntity.getCoord()[1] + " ...");	
			}
			

			/* Faire la Mis A Jour du Noeud */
			NeighborProxy responseProx = ((CacheRequest)msg.getContent()).getOldData();
			CacheRequest response = new CacheRequest(responseProx,this.mainVirtualEntity.getNeighbor( this.mainVirtualEntity.getId()));
			this.MajNeighborProxy(((CacheRequest)msg.getContent()).getOldData(), this.mainVirtualEntity);

			if (cacheDebug == 1){
				System.out.println("-- realcoord: " + this.mainVirtualEntity.getCoord()[0] + "," + this.mainVirtualEntity.getCoord()[1] + ", id: " +  this.mainVirtualEntity.getId());
				System.out.println("-- new coord: " + response.getOldData().getCoord()[0] + "," + response.getOldData().getCoord()[1] );
			}
			
			/*Envoyer un message pour faire un hit de cache */
			Message cache_rep;
			cache_rep = new Message(Message.CACHE_UPD_REP, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), msg.getSource(), msg.getContent());

			if (source != null){
				this.send(cache_rep,source);
				if (cacheDebug == 1 || cacheDebug == 2){
					System.out.println("<" + cache_rep.getTypeString() + ", " +  this.mainVirtualEntity.getId() + ", " + msg.getSource() + ", " + source.getId() + ">");
				}
			/* Si erreur dans le source du message */
			}else{
				if (cacheDebug == 1){
					System.out.println("Erreur: La source est null");
					System.out.println("<--ERREUR " + cache_rep.getTypeString() + ", " + msg.getOriginAddress() + ", " +  this.mainVirtualEntity.getId() + ", " + msg.getSource() + " ERREUR-->");
				}
			}
			
		}else{
			if ( cacheDebug == 1 || cacheDebug == 2 ){
				System.out.println("''' Comparaison FOLD: Old " + ((CacheRequest)msg.getContent()).getOldData().getCoord() +
						", Real " + this.mainVirtualEntity.getCoord() + " '''");	
			}
			Globals.cacheEvaluator.incCacheMitGLob();
			/*Envoyer un message pour pas faire un hit de cache ?*/

		}
		
	}
	
	private boolean isInCache(NeighborProxy neighbor){

		NeighborProxy current;
		Iterator it;
		it = this.cache.getCache().entrySet().iterator();
				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			if (neighbor.getId() == current.getId()){
//				System.out.println("DANS LE CACHE -----------");
				return true;
			}
		}
		return false;
	}
	
	private boolean isInNeig(NeighborProxy neighbor){

		NeighborProxy current;
		Iterator it;
		it = this.proxies.entrySet().iterator();
				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			if (neighbor.getId() == current.getId()){
				return true;
			}
		}
		return false;
	}
	
	private void processCacheHelpResponse(Message msg){

		NeighborProxy neighbor = (NeighborProxy)msg.getContent();
		
		boolean aide = helpfulToEnvelope(neighbor);
		boolean inCache = isInCache(neighbor);
		boolean inNeig = isInNeig(neighbor);
		boolean avant = convexEnvelopeProperty();
		
		if (cacheDebug == 1 ){
			System.out.println("Is In CACHE ?? " + inCache);
			System.out.println("Is In PROXIES ?? " + inNeig);
			System.out.println("newNode_Coord= " + neighbor.getCoord()[0] + ", " + neighbor.getCoord()[1]);
			System.out.println("me_Coord= " + this.mainVirtualEntity.getCoord()[0] + ", " + this.mainVirtualEntity.getCoord()[1]);
			System.out.println("aide_ou_pas : " + aide);
			System.out.println("Envelop state before: " + avant);
		}

		if ( cacheDebug == 1 || cacheDebug == 2 ){
			System.out.println("Message Recu : <" + msg.getTypeString() + ", " +  this.mainVirtualEntity.getId() + ", " + msg.getSource() + ">");
		}
		
		double dist = VirtualWorld.simpleDistance(this.mainVirtualEntity.getCoord(), neighbor.getCoord());

		if (aide || dist < this.knowledgeRay ){		
			
			addLocalView(neighbor);
		
			if (cacheDebug == 1){
				System.out.println("taille cache: " + cache.getCache().size());
				System.out.println("taille voisin: " + proxies.size());
				cache.ShowCache();
				System.out.println("--- Modification de la quality du nœud: " + neighbor.getQuality());
			}
		
			boolean apres = convexEnvelopeProperty();
			if (cacheDebug == 1 ){
				System.out.println("Envelop State after: " + apres);
			}
			Globals.cacheEvaluator.incCacheHitHelpGLob();
		}
	}
	
	/*
	 * Fonction processCacheUpdResponse:
	 * 	On traite la réponse en supprimant le nœud du cache et en l'ajoutant 
	 * 	dans la liste des voisins, on incrément le compteur de cache HIT, on 
	 * 	passe la quality du nœud à REGULAR et on vérifie l'enveloppe.
	 * 
	 */
	
	private void processCacheUpdResponse(Message msg){

		CacheRequest neig = (CacheRequest) msg.getContent();
		NeighborProxy neighbor = neig.getOldData();

		boolean aide = this.helpfulToEnvelopeCache(neighbor);
		boolean avant = convexEnvelopeProperty();

		if (cacheDebug == 1 ){
			System.out.println("!!!aide_ou_pas : " + aide +", myId: " + this.mainVirtualEntity.getId() + ", newId: " + neighbor.getId() + ", time: " + CommonState.getTime());
			System.out.println("!!!Envelop state before: " + avant);
			System.out.println("!!!My Coords: " + this.mainVirtualEntity.getCoord()[0] + ", " + this.mainVirtualEntity.getCoord()[1]);
			System.out.println("!!!Coord: " + neighbor.getCoord()[0] + ", " + neighbor.getCoord()[1]);
		}
		
		double dist = VirtualWorld.simpleDistance(this.mainVirtualEntity.getCoord(), neighbor.getCoord());
		
		if (aide || dist < this.knowledgeRay ){		
			if ( cacheDebug == 1 || cacheDebug == 2 ){
					System.out.println("--- Envelope OK, nodeId: " + neighbor.getId() );
					System.out.println("Message Recu : <" + msg.getTypeString() + ", " +  this.mainVirtualEntity.getId() + ", " + msg.getSource() + ">");
			}
			
			if (cacheDebug == 1){
				System.out.println("--- Nœud supprimé du cache: " + neighbor.getId() + " ---");
				cache.ShowCache();
				System.out.println("taille  cache: " + cache.getCache().size());
				System.out.println("taille voisin: " + proxies.size());
			}		
			cache.RmCache(neighbor);
			
			if (cacheDebug == 1){
				System.out.println("taille cache: " + cache.getCache().size());
				System.out.println("taille voisin: " + proxies.size());
			}
			
			addLocalView(neighbor);
			
			if (cacheDebug == 1){
				System.out.println("taille cache: " + cache.getCache().size());
				System.out.println("taille voisin: " + proxies.size());
				cache.ShowCache();
			}
			
			/* Changement de la Qulity du nœud que l'on repasse à REGULAR */
			neighbor.setQuality(NeighborProxy.REGULAR);
			neighbor.setTime(CommonState.getIntTime());
			
			if (cacheDebug == 1){
				System.out.println("--- Modification de la quality du nœud: " + neighbor.getQuality());
			}
		
		}else{
			if ( cacheDebug == 1 || cacheDebug == 2 ){
				System.out.println("--- Envelope NOT_OK, nodeId: " + neighbor.getId() );
			}
			if ( isInCache(neighbor)){
				MajNeighborProxy(this.cache.getCache().get(neighbor.getId()), neighbor);
				System.out.println("YESPAPA+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			}else{
				System.out.println("NOPAPA+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("neigbor: " + neighbor.getId());
//				this.cache.ShowCache();
			}
			Globals.cacheEvaluator.incCacheMitGLob();
			return;
			
		}
		boolean apres = convexEnvelopeProperty();
//		System.out.println("!!!Envelop State after: " + apres);
		Globals.cacheEvaluator.incCacheHitGLob();
		
	}
	
	/*
	 * Fonction MajNeighborProxy:
	 * 	met à jour les coordonnées du nœud "old"
	 * 	avec les valeurs du nœud "nouv"
	 */
	private void MajNeighborProxy(NeighborProxy old, VirtualEntity nouv){
		old.setCoord(nouv.getCoord());		
		old.setTime(CommonState.getTime());
	}
	
	/*
	 * Fonction MajNeighborProxy:
	 * 	met à jour les coordonnées du nœud "old"
	 * 	avec les valeurs du nœud "nouv"
	 */
	private void MajNeighborProxy(NeighborProxy old, NeighborProxy nouv){
		old.setCoord(nouv.getCoord());		
	}
	
	
	private boolean compareNeighborProxy(CacheRequest old,VirtualEntity act){
		boolean resultat;
		resultat = false;
		
		if ( act.getCoord()[0] == old.getOldData().getCoord()[0] && act.getCoord()[1] == old.getOldData().getCoord()[1]){
			resultat = true;
		}
		
		double res = VirtualWorld.simpleDistance(old.getOldData().getCoord(), act.getCoord());
		if (cacheDebug == 1){
			System.out.println(this);
			System.out.println("old: " + old.getOldData().getCoord() + ", act: " + act.getCoord());
			System.out.println("- curtime: " + CommonState.getTime());
			System.out.println("---- dist: " + res);
			System.out.println("- oldtime: " + old.getOldData().getTime());
		}
		if (res < this.limite){
			resultat = true;
		}
		return resultat;
	}
	

	private void processFoundMsg(Message msg) {
		NeighborProxy peer = (NeighborProxy)msg.getContent();
		GeometricRegion line;
		Message recoverMsg;
		if (peer!=null) {
			if (!isNeighbor(peer)) {
				switch(peer.getQuality()) {
				case NeighborProxy.REGULAR:
					this.mainVirtualEntity.addNeighbor(peer);
					this.sendConnectMessage(peer);
					this.searchInProgress = false;
					break;
				case NeighborProxy.PREFETCHED:
					if (!this.prefetchingModule.leftAside(peer) && !this.prefetchingModule.prefetchSetFull()) {
						this.mainVirtualEntity.addNeighbor(peer);
					}
					break;
				}
			} else {
				if (peer.getQuality() == NeighborProxy.REGULAR) {
					if (!this.checkEnvelopeState()) {
						Node n = Network.get(this.getPeersimNodeId());
						line  = new GeometricRegion(this.mainVirtualEntity.getCoord(), this.subjectiveCoord(peer.getId()));
						if (help_ok == 1){
							CacheHelpRequest request = new CacheHelpRequest(line, this.convexEnvelope,this.mainVirtualEntity.getCoord(),this.knowledgeRay);
							recoverMsg = new Message(Message.SEARCH_HELP, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), peer.getId(), request);
						}else{
							recoverMsg = new Message(Message.SEARCH, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), peer.getId(), line);
						}
						this.send(recoverMsg, peer);
						this.stabilized = false;
						this.searchInProgress = true;
					} else {
						this.stabilized = true;
						this.searchInProgress = false;
					}
				}
			}
		} else {
			this.searchInProgress = false;
		}
	}
	
	private void predictTrajectory(NeighborProxy entity) {
		long[] vector = this.predictionVector.get(entity.getId());
		long[] old = new long[2];
		old[0] = entity.getCoord()[0];
		old[1] = entity.getCoord()[1];
			entity.setCoord(VirtualWorld.sumCoords(entity.getCoord(), vector));
	}
	
	private void updatePredictionData(int id, long[] oldCoord, long[] newCoord) {
		long[] vector = VirtualWorld.substract(newCoord, oldCoord);
		this.predictionVector.put(id, vector);
	}
	
	public void propagateStateInformation() {
		Message msg;
		NeighborProxy current;
		Long timestamp; 
		long time;
		LinkedList<Integer> neighbors;
		LinkedList<NeighborProxy> longRange;
		int size;
		Iterator it;
		
		if (Globals.topologyIsReady && stateUpdateTimerReady()) {
			
			neighbors = this.getParticularNeighbors(NeighborProxy.REGULAR);
			size = neighbors.size();
			time = CommonState.getTime();
			for (int i = 0; i < size; i++) {
				current = this.proxies.get(neighbors.get(i));
				if (current.getQuality() == NeighborProxy.REGULAR) {
					timestamp = this.updateTimestamps.get(current.getId());
					if (this.type == SolipsisProtocol.ENHANCED && timestamp != null && (time - timestamp.longValue()) >= SolipsisProtocol.WANDERING_UPDATE_INTERVAL + this.transportLayer.getMaxLatency()) {
						this.predictTrajectory(current);
					}
				}
				msg = this.statePropagationMessage(current);
				this.send(msg, current);
			}

			if (this.type == SolipsisProtocol.SMALLWORLD) {
				it = this.smallWorldModule.getLinks().entrySet().iterator();

				while (it.hasNext()) {
					current = (NeighborProxy)((Map.Entry)it.next()).getValue();
					msg = this.statePropagationMessage(current);
					this.send(msg, current);
				}
			}
			rearmStateUpdateTimer();
		} 
	}

	private void processDeltaMsg(Message msg) {
		GeometricRegion knowledgeZone = (GeometricRegion)msg.getContent();
		int incomingId = msg.getSource();
		NeighborProxy proxy = this.proxies.get(incomingId);
		long[] oldCoord = new long[2];
			if (proxy != null) {
				this.sentinelAlgorithm(knowledgeZone,proxy);
				oldCoord[0] = proxy.getCoord()[0];
				oldCoord[1] = proxy.getCoord()[1];
				proxy.setCoord(knowledgeZone.getOrigin());
				proxy.setRadius(knowledgeZone.getRadius());
				
				if (this.type == SolipsisProtocol.ENHANCED) {
					if (knowledgeZone.getState() == MobilityStateMachine.TRAVELLING) {
						this.updateTimestamps.put(proxy.getId(), CommonState.getTime());
						this.updatePredictionData(proxy.getId(), oldCoord, proxy.getCoord());
					} else {
						this.updateTimestamps.remove(proxy.getId());
						this.predictionVector.remove(proxy.getId());
					}
				}
			} else {
				if (this.type == SolipsisProtocol.SMALLWORLD) {
					this.smallWorldModule.updateState(incomingId, knowledgeZone.getOrigin());
				} else {
					if (!Globals.realTime) {
						Globals.evaluator.messageRejected();
					}
				}
			}
	}
	
	private void sentinelAlgorithm(GeometricRegion delta, NeighborProxy source) {
		long [] newOrigin = this.subjectiveCoord(delta.getOrigin());
		double newRadius = delta.getRadius();
		
		long [] oldOrigin = this.subjectiveCoord(source.getCoord());
		double oldRadius = source.getRadius();
		
		double currentRadius;
		long [] currentCoord;
		NeighborProxy currentProxy;
		
		long [] myCoord = this.mainVirtualEntity.getCoord();
		
		boolean b1,b2,b3,b4,r1;
		LinkedList<Integer> neighbors = this.getParticularNeighbors(NeighborProxy.REGULAR);
		double newDistance, oldDistance, thisToCurrentDistance, thisToMovingDistance = VirtualWorld.simpleDistance(newOrigin, myCoord);
		int size = neighbors.size();
		for (int i = 0; i < size; i++) {
			currentProxy = this.proxies.get(neighbors.get(i));
			if (currentProxy != source && currentProxy.getQuality() == NeighborProxy.REGULAR) {
				currentCoord = this.subjectiveCoord(currentProxy.getCoord());
				currentRadius = currentProxy.getRadius();
				thisToCurrentDistance = VirtualWorld.simpleDistance(currentCoord, myCoord);
				newDistance = VirtualWorld.simpleDistance(newOrigin, currentCoord);
				oldDistance = VirtualWorld.simpleDistance(oldOrigin, currentCoord);
				
				//the new distance to a proxy is lower than the radius while old distance wasnt
				b1 = newDistance <= currentRadius && currentRadius < oldDistance;
				//the radius of the update is higher than the new distance 
				b2 = newDistance <= newRadius && newRadius < oldDistance;
				b3 = newDistance <= thisToCurrentDistance && thisToCurrentDistance < oldDistance;
				b4 = newDistance <= thisToMovingDistance && thisToMovingDistance < oldDistance;
				r1 = oldRadius < newDistance && newDistance <= newRadius;
				
				if (b1 || b3) {
					sendDetectMsg(currentProxy,source.clone());
				}
				
				if (b2 || b4 || r1) {
					sendDetectMsg(source,currentProxy.clone());
				}
			}
		}
		
		
	}
	
	private boolean checkEnvelopeState() {
		this.convexEnvelope = this.findConvexEnvelope(this.idToProxyList(this.getParticularNeighbors(NeighborProxy.REGULAR)), this.mainVirtualEntity);
			NeighborProxy [] sector = this.verifyEnvelope(convexEnvelope);
			return sector == null;
	}
	
	private void processCloseMsg(Message msg) {
		int peerToForget = msg.getSource();
			Long connect = this.connectTimestamps.get(peerToForget);
			if (this.hasNeighbor(peerToForget)) {
				if (connect != null) {
					if (connect.longValue() < msg.getTimestamp()) {
						this.mainVirtualEntity.removeNeighbor(peerToForget);
						this.stabilized = checkEnvelopeState();
					}
				} else {
					this.mainVirtualEntity.removeNeighbor(peerToForget);
					this.stabilized = checkEnvelopeState();
				}
			} else {
				if (this.type == SolipsisProtocol.SMALLWORLD && this.smallWorldModule.contains(peerToForget)) {
					this.smallWorldModule.removeLink(peerToForget);
				}
				if (!Globals.realTime) {
					Globals.evaluator.messageRejected();
				}
			}
			this.disconnectTimestamps.put(peerToForget, msg.getTimestamp());
	}
	
	/*
	 * Fonction RemoveProxy:
	 * 	On ajoute l'élement que l'on supprime des voisins 
	 * 	dans le cache.
	 */
	public void removeProxy(int neighbor) {
		NeighborProxy rm = this.proxies.get(neighbor);
		if (this.strategieCache == SolipsisProtocol.FIFO || this.strategieCache == SolipsisProtocol.FIFOMULT) {
			this.cache.AddCache(this.proxies.get(neighbor));
		}
		this.proxies.remove(neighbor);
		this.clearConnectionTime(neighbor);
		this.updateTimestamps.remove(neighbor);
		this.predictionVector.remove(neighbor);
	}
	
	/* 
	 * Fonction de suppression mais sans passer par le cache 
	 * 	sert pour lorsque que l'on ajoute au voisin pour tester
	 * 	la couverture dans la fonction de recherche dans le cache
	 * 
	 */
	public void removeProxyNoCache(int neighbor) {
		NeighborProxy rm = this.proxies.get(neighbor);
		this.proxies.remove(neighbor);
		this.clearConnectionTime(neighbor);
		this.updateTimestamps.remove(neighbor);
		this.predictionVector.remove(neighbor);
	}
	
	private boolean tooMuchNeighbors() {
		int toRemove = this.proxies.size() - this.exp - (int)(this.toleranceLevel * this.exp);
		return toRemove > 0;
	}
	
	public NeighborProxy createMyImage() {
		return new NeighborProxy(this.getPosition(), this.knowledgeRay, this.mainVirtualEntity.getId(), this.peersimNodeId);
	}
	
	public void sendConnectMessage(NeighborProxy peer) {
		NeighborProxy mySelf = this.createMyImage();
		Message msg = new Message(Message.CONNECT, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), peer.getId(), mySelf);
		msg.setTimestamp(CommonState.getTime());
		this.send(msg, peer);
	}

	private void processConnectMsg(Message msg) {
		NeighborProxy peer = (NeighborProxy)msg.getContent();
			Long disconnect = this.disconnectTimestamps.get(peer.getId());
			long connect = msg.getTimestamp();
			if (disconnect != null) {
				if (disconnect.longValue() < connect) {
					this.connectTimestamps.put(peer.getId(), msg.getTimestamp());
					this.mainVirtualEntity.addNeighbor(peer);
					this.maintainKnowledgeZone();
				}
			} else {
				this.connectTimestamps.put(peer.getId(), msg.getTimestamp());
				this.mainVirtualEntity.addNeighbor(peer);
				this.maintainKnowledgeZone();
			}
	}
	
	private void sendDetectMsg(NeighborProxy interestedEntity, NeighborProxy changingEntity) {
		VirtualEntity fastSend;
		SolipsisProtocol protocol;
		Integer id;
		long arrivalTime, latency;
		Message msg = new Message(Message.DETECT, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), interestedEntity.getId(), changingEntity);
		fastSend = this.neighborProxyToVirtualEntity(interestedEntity);
		if (fastSend != null) {
			protocol = fastSend.getProtocol();
			id = changingEntity.getId();
			if (Globals.topologyIsReady) {
				latency = this.transportLayer.getLatency();
				arrivalTime = CommonState.getTime() + latency;	
				if (protocol.hasDetected(id)) {			
					if (protocol.detectionTime(id) < arrivalTime) {
						if (!Globals.realTime) {
							Globals.evaluator.messageSent();
							Globals.evaluator.messageDetect();
						}
					} else {
						protocol.setDetected(id, arrivalTime);
						this.send(msg, interestedEntity, latency);
					}
				} else {
					protocol.setDetected(id, arrivalTime);
					this.send(msg, interestedEntity);
				}
			} else {
				fastSend.getProtocol().fastDetect(msg);
			}
		}
	}
	
	public void fastDetect(Message msg) {
		if (!Globals.topologyIsReady) {
			processDetectMsg(msg);
		}
	}
	

	private void processDetectMsg(Message msg) {
		NeighborProxy newEntity = (NeighborProxy)msg.getContent();
		Integer id = new Integer(newEntity.getId());
		if (this.hasDetected(id)) {
				this.removeDetected(id);
		}
		if (this.mainVirtualEntity.getId() != newEntity.getId() && !this.isNeighbor(newEntity)) {
			this.mainVirtualEntity.addNeighbor(newEntity);
			this.sendConnectMessage(newEntity);
			if (this.type == SolipsisProtocol.SMALLWORLD && this.smallWorldModule.isWiring()) {
				this.smallWorldModule.updateDistance();
				if (this.smallWorldModule.chooseLongRangePeer()) {
					this.smallWorldModule.sendLongRangeLink(newEntity);
				}
			}
		}
	}
	
	public void receive(Message msg, SolipsisProtocol oneHopSourcePeer) {
		Globals.cacheEvaluator.incnbNombreMessages();

		if (!Globals.realTime) {
			Globals.evaluator.messageReceived();
		}
		switch(msg.getType()) {
		case Message.HELLO:
			break;
		case Message.CONNECT:
			Globals.cacheEvaluator.incnbMessConnect();
			processConnectMsg(msg);
			break;
		case Message.HEARTBEAT:
			break;
		case Message.DELTA:
			Globals.cacheEvaluator.incnbMessDelta();
			processDeltaMsg(msg);
			if (!Globals.realTime) {
				Globals.evaluator.messageDelta();
			}
			break;
		case Message.DETECT:
			processDetectMsg(msg);
			if (!Globals.realTime) {
				Globals.evaluator.messageDetect();
			}
			break;
		case Message.SEARCH:
			Globals.cacheEvaluator.incnbMessSearch();
			processSearchMsg(msg);

			if (!Globals.realTime) {
				Globals.evaluator.messageSearch();
			}
			break;
		case Message.FOUND:
			Globals.cacheEvaluator.incnbMessFound();
			processFoundMsg(msg);
			break;
		case Message.CLOSE:	
			Globals.cacheEvaluator.incnbMessClose();
			processCloseMsg(msg);
			break;
		case Message.PREFETCH:
			prefetchingModule.processPrefetchMsg(msg);
			break;
		case Message.SMALLWORLD:
			this.processSmallWorldMsg(msg);
			break;
		case Message.FIND_NEAREST:
			this.processFindNearest(msg);
			break;
		case Message.QUERYAROUND:
			this.processQueryAround(msg);
			break;
		case Message.LOOKED_UP:
			this.processLookedUpMsg(msg);
			break;
		case Message.LOOKUP_REPLY:
			this.processLookupReply(msg);
			break;
		case Message.LONG_RANGE_ACK:
			this.processSmallWorldAckMsg(msg);
			break;
		case Message.LONG_RANGE_REQ:
			this.processSmallWorldConfirmation(msg);
			break;
		case Message.CACHE_UPD:
			Globals.cacheEvaluator.incNbCacheRequest();
			this.processCacheUpd(msg);
			break;
		case Message.CACHE_UPD_REP:
			Globals.cacheEvaluator.incnbCacheResponse();
			this.processCacheUpdResponse(msg);
			break;
		case Message.CACHE_HELP_REP:
			Globals.cacheEvaluator.incnbMessCacheHelpResp();
			this.processCacheHelpResponse(msg);
			break;
		case Message.SEARCH_HELP:
			Globals.cacheEvaluator.incnbMessSearch();
			Globals.cacheEvaluator.incnbMessSearchHelp();
			processSearchMsg(msg);

			if (!Globals.realTime) {
				Globals.evaluator.messageSearch();
			}
			break;
		case Message.UPDATE_CACHE_REQ:
			Globals.cacheEvaluator.incnbMessUpdateTot();
			this.processUpdateCaheREQ(msg);
			break;
		case Message.UPDATE_CACHE_REP:
			Globals.cacheEvaluator.incnbMessUpdateTot();
			this.processUpdateCaheREP(msg);
			break;
		default:
			break;
		}
	}
	
	private void processLookedUpMsg(Message msg) {
		NeighborProxy src;
		
		src = (NeighborProxy)msg.getContent();
//		src.setQuality(NeighborProxy.LONGRANGE);
		this.addLocalView(src);
		src.setTime(CommonState.getTime());

	}

	private void processLookupReply(Message msg) {
		LinkedList<NeighborProxy> around;
		Lookup request;
		int size;
		
		request = (Lookup)msg.getContent();
		this.status = "Joined";
		if (request.getKind() == Lookup.JOIN) {
			around = request.getAround();
			size = around.size();
			System.out.println(this.mainVirtualEntity.getId()+" (" + this.getPosition()[0] + "," + this.getPosition()[1] + "): joined with "+request.getHops()+" hops and has " + size + " neighbors.");
			for (int i = 0; i < size; i++) {
				this.addLocalView(around.get(i));
				around.get(i).setTime(CommonState.getTime());

			}
		} else {
			size = request.getHops();
			if (size < Network.size()) {
				Globals.hops += request.getHops();
				Globals.sqLookups += request.getHops() * request.getHops();
				Globals.lookupsAchieved++;
				Globals.lookupsPending--;
				//				}
			} else {
				System.err.println("lost lookup");
				System.exit(1);
			}
		}
		
	}
	
	private int countNoLongRange() {//TODO only for debug purpose. to remove
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

	private void processQueryAround(Message msg) {
		Lookup request;
		NeighborProxy closest, source;
		LinkedList<NeighborProxy> around;
		double distanceFromGlobalClosestNode, minDistanceFromCurrentNeighbors;
		long[] global, destination, supposedGlobalClosestReal, currentClosestReal, closestNeighborReal;
		int halfRound;
		
		Globals.totalUse++;
		request = (Lookup)msg.getContent();
		request.incrementHops();
		destination = request.getCoordinates();
		
		if (VirtualWorld.samePosition(destination, this.getPosition())) {
			if (request.getKind() == Lookup.JOIN) {
				around = this.myNeighbors();
				request.setAround(around);
				this.sendLookupReplyMessage(request.getSource(), request);
				this.warnNeighbors(around, request.getSource());
			} else {
				this.sendLookupReplyMessage(request.getSource(), request);
			}
			return;
		}

		halfRound = request.getHalfRound();
		global = request.getSupposedGlobalClosest();
		supposedGlobalClosestReal = this.realRelativeCoord(global, destination);
		currentClosestReal = this.realRelativeCoord(this.mainVirtualEntity.getCoord(), destination);
		around = request.getAround();
		around.add(this.createMyImage());
		source = request.getSource();
		closest = chooseClosestNeighborAround(destination, around, request.getDirection());
		if (closest == null) {
			if (request.getKind() == Lookup.JOIN) {
				request.setAround(around);
				this.sendLookupReplyMessage(source, request);
				this.warnNeighbors(around, source);
			} else {
				this.sendLookupReplyMessage(source, request);
			}
		} else {
			closestNeighborReal = this.realRelativeCoord(closest.getCoord(), destination);
			distanceFromGlobalClosestNode = VirtualWorld.simpleDistance(supposedGlobalClosestReal, destination);
			minDistanceFromCurrentNeighbors = VirtualWorld.simpleDistance(currentClosestReal, destination);
			if (distanceFromGlobalClosestNode > minDistanceFromCurrentNeighbors && notIn(closest,around)) {
				this.lookup(request);
			} else {
				halfRound+=(halfRound(destination, supposedGlobalClosestReal,currentClosestReal,closestNeighborReal))?1:0;
				if(closest == around.get(0) || halfRound == 2) {
					if (request.getKind() == Lookup.JOIN) {
						request.setAround(around);
						this.sendLookupReplyMessage(source, request);
						this.warnNeighbors(around, source);
					} else {
						this.sendLookupReplyMessage(source, request);
					}

				} else {
					request.setHalfRound(halfRound);
					this.sendQueryAroundMessage(request, closest);
				}
			}
		}
	}


	private void sendLookupReplyMessage(NeighborProxy destination, Lookup request) {
		Message msg;
		
		msg = new Message(Message.LOOKUP_REPLY, this.peersimNodeId, this.mainVirtualEntity.getId(), destination.getId(), request);
		this.send(msg, destination);
	}


	private void processFindNearest(Message msg) {
		Lookup request;
		
		request = (Lookup)msg.getContent();
		request.incrementHops();
		this.lookup(request);
		
	}

	private void processSmallWorldMsg(Message msg) {
		HashMap<Integer, NeighborProxy> lrl;
		Iterator it;
		
		NeighborProxy peer = (NeighborProxy)msg.getContent();
		this.smallWorldModule.addLongRangeLink(peer);
		lrl = this.getLongRangeLinks(peer);
		it = lrl.entrySet().iterator();
		
		while (it.hasNext()) {
			this.sendLongRangeLinkConfirmation((NeighborProxy)((Map.Entry)it.next()).getValue());
		}
	}
	
	private void sendLongRangeLinkConfirmation(NeighborProxy dest) {
		Message msg;
		
		msg = new Message(Message.LONG_RANGE_REQ, this.peersimNodeId, this.mainVirtualEntity.getId(), dest.getId(), this.createMyImage());
		this.send(msg, dest);
	}
	
	private void processSmallWorldConfirmation(Message msg) {
		NeighborProxy requester;
		
		requester = (NeighborProxy)msg.getContent();
		this.smallWorldModule.addLongRangeLink(requester);
		if (this.smallWorldModule.contains(requester.getId())) {
			this.sendLongRangeLinkAck(requester);
		}
	}
	
	private void sendLongRangeLinkAck(NeighborProxy dest) {
		Message msg;
		
		msg = new Message(Message.LONG_RANGE_ACK, this.peersimNodeId, this.mainVirtualEntity.getId(), dest.getId(), this.createMyImage());
		this.send(msg, dest);
	}

	private void processSmallWorldAckMsg(Message msg) {
		NeighborProxy evicted;
		
		evicted = this.smallWorldModule.addLongRangeLink((NeighborProxy)msg.getContent());
		
		if (evicted != null) {
			this.sendDisconnectMessage(evicted);
		}
	}
	

	private void send(Message msg, int peersimId) {
		Node src = solipsisToPeersimNode(this);
		Node dest = Network.get(peersimId);
		this.transportLayer.send(src, dest, msg, this.protocolId);
	}
	
	public void send(Message msg, NeighborProxy proxy) {
		Node dest = proxyToPeersimNode(proxy);
		Node src = solipsisToPeersimNode(this);
		if (dest != null) {
			this.transportLayer.send(src, dest, msg, this.protocolId);
		}
	}
	
	public void send(Message msg, NeighborProxy proxy, long latency) {
		Node dest = proxyToPeersimNode(proxy);
		Node src = solipsisToPeersimNode(this);
		if (dest != null) {
			this.transportLayer.send(src, dest, msg, this.protocolId,latency);
		}
	}
	
	public void send(Message msg, VirtualEntity entity) {
		Node dest = this.solipsisToPeersimNode(entity.getProtocol());
		Node src = solipsisToPeersimNode(this);
		if (dest != null) {
			this.transportLayer.send(src, dest, msg, this.protocolId);
		}
	}
	
	public boolean insidePeerZone(NeighborProxy peer) {
		double radius = peer.getRadius();
		long[] coord = this.mainVirtualEntity.getCoord();
		long[] subjectiveCoord;
		VirtualEntity entity;
		
		if (Globals.topologyIsReady) {
			subjectiveCoord = this.subjectiveCoord(peer.getId());
			radius = peer.getRadius();
		} else {
			entity = this.neighborProxyToVirtualEntity(peer);
			subjectiveCoord = this.subjectiveCoord(entity.getCoord());
			radius = entity.getKnowledgeRay();
		}
		double distance = VirtualWorld.simpleDistance(subjectiveCoord, coord);
		
		return (radius==0) || (distance <= radius);
	}
	
	private boolean convexEnvelopeProperty() {
		NeighborProxy[] sector = this.verifyEnvelope(this.findMyConvexEnvelope());
		return sector == null;
	}
	
	public boolean convexEnvelopePropertyTest() {
		NeighborProxy[] sector = this.verifyEnvelope(this.findMyConvexEnvelope());
		return sector == null;
	}
	

	private boolean convexEnvelopePropertyHelp(long [] destination, LinkedList<NeighborProxy> envelope) {
		NeighborProxy[] sector = this.verifyEnvelopeHelpCache(destination,envelope);
		return sector == null;
	}
	
	
	public boolean necessaryToEnvelope(NeighborProxy peer) {
		boolean beforeRemoval = convexEnvelopeProperty();
		NeighborProxy proxy = this.proxies.remove(peer.getId());
		boolean convexProperty = convexEnvelopeProperty();
		if (proxy != null)
			this.proxies.put(peer.getId(), peer);
			peer.setTime(CommonState.getTime());
		return !convexProperty && beforeRemoval;
	}
	
	public boolean helpfulToEnvelope(NeighborProxy peer) {
		int quality = peer.getQuality();
		boolean beforeAdding = convexEnvelopeProperty();
		peer.setQuality(NeighborProxy.REGULAR);
		boolean convexProperty = convexEnvelopeProperty();
		peer.setQuality(quality);
		return convexProperty && !beforeAdding;
	}
	
	synchronized public boolean helpfulToEnvelopeCache(NeighborProxy peer) {
		int quality = peer.getQuality();
		boolean beforeAdding = convexEnvelopeProperty();
		this.addLocalViewNoCache(peer);
		boolean convexProperty = convexEnvelopeProperty();
		peer.setQuality(quality);
		this.removeProxyNoCache(peer.getId());
		return convexProperty && !beforeAdding;
	}
	
	synchronized public boolean helpfulToEnvelopeCacheHelpNeighbor(NeighborProxy peer,CacheHelpRequest request) {

		boolean beforeAdding = convexEnvelopePropertyHelp(request.getDestination(), request.getEnvelop());
		
		LinkedList<NeighborProxy> newenvelope = request.getEnvelop();
		newenvelope.add(peer);
		
		boolean convexProperty = convexEnvelopePropertyHelp(request.getDestination(),newenvelope);
		newenvelope.remove(peer);

		return convexProperty && !beforeAdding ;
	}
	
	public void removeUnwantedNeighbors() {
		LinkedList<NeighborProxy> ordered = this.orderList(this.getParticularNeighbors(NeighborProxy.REGULAR));
		int insideKnowledgeZone = count();
		int range,remover,size = ordered.size();
		NeighborProxy current;
		int toRemove = size - this.exp - (int)(this.toleranceLevel * this.exp);
		if (toRemove > 0) {
			for (int  i = size-1; i >= insideKnowledgeZone ; i--) {
				current = ordered.get(i);
				if (!this.insidePeerZone(current) && (!necessaryToEnvelope(current) && !constructingEnvelope(current))) {
					if(Globals.topologyIsReady) {
							this.mainVirtualEntity.removeNeighbor(current.getId());
							this.sendDisconnectMessage(current);
					} else {
						this.mainVirtualEntity.removeNeighbor(current.getId());
						this.neighborProxyToVirtualEntity(current).removeNeighbor(this.mainVirtualEntity.getId());
					}
					toRemove--;
					if (toRemove == 0) {
						break;
					}
				}
			}
		}
	}
	
	public boolean constructingEnvelope(NeighborProxy proxy) {
		this.convexEnvelope = this.findConvexEnvelope(this.idToProxyList(this.getParticularNeighbors(NeighborProxy.REGULAR)), this.mainVirtualEntity);
		NeighborProxy [] sector = this.verifyEnvelope(convexEnvelope);
		boolean answer = false;
		
		if (sector != null) {
			answer = proxy.getId() == sector[0].getId() || proxy.getId() == sector[1].getId();
		}
		return answer;
	}
	
	public void sendDisconnectMessage(NeighborProxy entity) {
		Message msg = new Message(Message.CLOSE, this.getPeersimNodeId(), this.mainVirtualEntity.getId(), entity.getId(), null);
		msg.setTimestamp(CommonState.getTime());
		this.send(msg, entity);
	}
	

	public void maintainKnowledgeZone() {
		int tolerance = (int)(this.toleranceLevel * this.exp);
		int min = this.exp - tolerance;
		int max = this.exp + tolerance; 
		int neighborSize = this.getParticularNeighbors(NeighborProxy.REGULAR).size();
		int count = this.count();
		if (count > max) {
				this.knowledgeRay = dist(this.exp - 1);
		}
		
		if (count < min) {
			if (neighborSize > min) {
				if (neighborSize > this.exp) {
					this.knowledgeRay = dist(this.exp - 1);
				} else {
					this.knowledgeRay = dist(neighborSize-1);
				}
			} else {
				if (Globals.topologyIsReady) {
					count = (count==0)?1:count;
					this.knowledgeRay = (long)(this.knowledgeRay * Math.sqrt(this.exp/count));
				} else {
					this.knowledgeRay = dist(neighborSize-1);
				}
			}
		}
		
		if (!Globals.topologyIsReady) {
			if (count <= max && count >= min) {
				this.knowledgeRay = dist(count-1);
			}
		} else {
			if (count > min && count <= max && neighborSize > max) {
				this.knowledgeRay = dist(count-2);
			}
		}
	}
	
	public Iterator getNeighbors() {
		return this.getNeighbors(true);
	}
	
	public Iterator getNeighbors(boolean useLongRange) {
		Iterator it;
		
		if (this.type == SolipsisProtocol.SMALLWORLD && useLongRange) {
			it = this.mergeSets(this.smallWorldModule.getLinks(), this.proxies).entrySet().iterator();
		} else {
			it = this.proxies.entrySet().iterator();
		}
		return it;
	}
	
	public LinkedList<SolipsisProtocol> getSolipsisNeighbors(boolean useNoLongRange) {
		LinkedList<SolipsisProtocol> answer;
		Iterator it;
		
		answer = new LinkedList<SolipsisProtocol>();
		it = this.getNeighbors(useNoLongRange);
		
		while (it.hasNext()) {
			answer.add(this.neighborProxyToVirtualEntity((NeighborProxy)((Map.Entry)it.next()).getValue()).getProtocol());
		}
		
		return answer;
	}
	
	public void finalizeKnowledgeZone() {
		this.maintainKnowledgeZone();
		Iterator it = this.proxies.entrySet().iterator();
		LinkedList<NeighborProxy> neighbors = this.iteratorToList(it);
		Iterator twoHopNeighbors;
		NeighborProxy current, hopCurrent;
		int size = neighbors.size();
		
		for (int i = 0; i < size; i++) {
			current = neighbors.get(i);
			twoHopNeighbors = ((SolipsisProtocol)Network.get(current.getPeersimNodeId()).getProtocol(this.protocolId)).getNeighbors();
			while (twoHopNeighbors.hasNext()) {
				hopCurrent = (NeighborProxy)((Map.Entry)twoHopNeighbors.next()).getValue();
				if (hopCurrent.getId() != this.mainVirtualEntity.getId() && this.isInsideKnowledgeZone(hopCurrent) && this.notIn(hopCurrent, this.getNeighbors())) {
					this.mainVirtualEntity.addNeighbor(hopCurrent);
					this.neighborProxyToVirtualEntity(hopCurrent).addNeighbor(this);
				}
			}
		}
		
		this.maintainKnowledgeZone();
	}
	
	public long estimateRTT() {
		return this.transportLayer.estimateRTT();
	}
	
	public long estimateMaxRTT() {
		return this.transportLayer.getMaxLatency();
	}
	
	public void simplifyTopology() {
		int size = this.proxies.size();
		if (size > 3) {
			this.maintainKnowledgeZone();
			this.removeUnwantedNeighbors();
			this.refreshState();
		}
	}
	
	public void updateStateOf(VirtualEntity entity) {
		NeighborProxy proxy = this.proxies.get(entity.getId());
		if (Globals.topologyIsReady) {
			System.out.println("updateStateOf");
			System.exit(13);
		}
		if (proxy != null) {
			proxy.setCoord(entity.getCoord());
			proxy.setRadius(entity.getKnowledgeRay());
		}
	}
	
	public void refreshState() {
		Iterator it = this.proxies.entrySet().iterator();
		VirtualEntity current;
		
		if (Globals.topologyIsReady) {
			System.out.println("refresh State");
			System.exit(132);
		}
		while(it.hasNext()) {
			current = this.neighborProxyToVirtualEntity((NeighborProxy)((Map.Entry)it.next()).getValue());
			current.updateStateOf(this.getVirtualEntity());
		}
	}
	
	public boolean isStabilized() {
		return this.stabilized;
	}
	
	public LinkedList<Integer> getParticularNeighbors(int quality) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		Iterator it  = this.proxies.entrySet().iterator();
		NeighborProxy current;
		
		while (it.hasNext()) {
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			if (current.getQuality() == quality) {
				ids.add(current.getId());
			}
		}
		
		return ids;
	}
	
	public void maintainTopology() {
		LinkedList<Integer> prefetchedNeighbors;
		int size;
		this.convexEnvelope = this.findConvexEnvelope(this.idToProxyList(this.getParticularNeighbors(NeighborProxy.REGULAR)), this.mainVirtualEntity);
		NeighborProxy [] sector = this.verifyEnvelope(convexEnvelope);

		
		if (this.type == ENHANCED) {
			if (this.mainVirtualEntity.getState() == MobilityStateMachine.TRAVELLING) {
				if (this.prefetchingModule.needToPropagateRequest()) {
					this.prefetchingModule.propagatePrefetchRequest();
				} 
			} else {
				prefetchedNeighbors = this.prefetchingModule.getPrefetchedNeighbors();
				size = prefetchedNeighbors.size();
				this.prefetchingModule.togglePrefetch(false);
			}
		}
		if(this.type == SolipsisProtocol.ENHANCED && this.prefetchingModule.hasPrefetched()) {
			this.prefetchingModule.prospectPrefetched();
			if (sector != null) {
				this.convexEnvelope = this.findConvexEnvelope(this.idToProxyList(this.getParticularNeighbors(NeighborProxy.REGULAR)), this.mainVirtualEntity);
				sector = this.verifyEnvelope(convexEnvelope);
			}
		}

		if(sector != null) {
			this.stabilized = false;
			recoverTopology(sector);
		} else {
			this.stabilized = true;
		}

	}
	
	private void recoverTopology(NeighborProxy [] sector) {
		if (Globals.topologyIsReady) {
			if (!this.searchInProgress) {
				solipsisRecoverTopology(sector);
			}
		} else {
			fastRecoverTopology(sector);
		}
	}
	
	private void fastRecoverTopology(NeighborProxy [] sector) {
		if (sector != null) {
			NeighborProxy convexFail = sector[0];
			NeighborProxy current;
			Iterator neighbors = ((SolipsisProtocol)Network.get(convexFail.getPeersimNodeId()).getProtocol(this.protocolId)).getNeighbors();
			long [] coord;
			boolean found = false;
			if (sector != null) {
				while(neighbors.hasNext()) {
					current = (NeighborProxy)((Map.Entry)neighbors.next()).getValue();
					if (current.getId() != this.mainVirtualEntity.getId() && !isNeighbor(current) && isInHalfPlan(convexFail,this.subjectiveCoord(current.getCoord()))) {
						this.mainVirtualEntity.addNeighbor(current);	
						this.neighborProxyToVirtualEntity(current).addNeighbor(this);
						found = true;
						break;
					}
				}
				if (!found) {	
					convexFail = sector[1];
					neighbors = ((SolipsisProtocol)Network.get(convexFail.getPeersimNodeId()).getProtocol(this.protocolId)).getNeighbors();
					while(neighbors.hasNext()) {
						current = (NeighborProxy)((Map.Entry)neighbors.next()).getValue();
						if (current.getId() != this.mainVirtualEntity.getId() && !isNeighbor(current) && !isInHalfPlan(convexFail,this.subjectiveCoord(current.getCoord()))) {
							this.mainVirtualEntity.addNeighbor(current);	
							this.neighborProxyToVirtualEntity(current).addNeighbor(this);
							found = true;
							break;
						}
					}
				} 
			}
			if (found) 
				maintainTopology();
		}
	
	}
	
	private NeighborProxy[] verifyEnvelope(LinkedList<NeighborProxy> envelope) {
		NeighborProxy [] sector = null;
		NeighborProxy current, next;
		if (envelope != null) {
			int size = envelope.size();
			if (size >= 3) {
				for (int i = 0; i < size; i++) {
					current = envelope.get(i);
					next = envelope.get((i+1)%size);
					if(simpleAngleSign(this.subjectiveCoord(current.getId()),this.subjectiveCoord(next.getId()),this.mainVirtualEntity.getCoord()) >= 0) {
						sector = new NeighborProxy[2];
						sector[0] = current;
						sector[1] = next;
						break;
					}
				}
			} else {
				sector = new NeighborProxy[2];
				for (int i = 0; i < size; i++) {
					sector[i] = envelope.get(i);
				}
			}
		}
		return sector;
	}
	
	private NeighborProxy[] verifyEnvelopeHelpCache(long [] destination, LinkedList<NeighborProxy> envelope) {
		NeighborProxy [] sector = null;
		NeighborProxy current, next;
		if (envelope != null) {
			int size = envelope.size();
			if (size >= 3) {
				for (int i = 0; i < size; i++) {
					current = envelope.get(i);
					next = envelope.get((i+1)%size);
					if(simpleAngleSign(this.subjectiveCoordHelp(destination, current.getCoord()),this.subjectiveCoordHelp(destination, next.getCoord()),destination) >= 0) {
						sector = new NeighborProxy[2];
						sector[0] = current;
						sector[1] = next;
						break;
					}
				}
			} else {
				sector = new NeighborProxy[2];
				for (int i = 0; i < size; i++) {
					sector[i] = envelope.get(i);
				}
			}
		}else{
			System.out.println("__________Envelop NULL");
		}
		return sector;
	}
	
	public boolean isNeighbor(NeighborProxy e) {
		boolean is = false;
		Iterator it = proxies.entrySet().iterator();
		while(it.hasNext()) {
			if (((NeighborProxy)((Map.Entry)it.next()).getValue()).getId() == e.getId()) {
				is = true;
				break;
			}
		}
		
		return is;		
	}
	
	public boolean isNeighbor(int id) {
		boolean is = false;
		Iterator it = this.getNeighbors();//this.proxies.entrySet().iterator();
		while(it.hasNext()) {
			if (((NeighborProxy)((Map.Entry)it.next()).getValue()).getId() == id) {
				is = true;
				break;
			}
		}
		
		return is;		
	}
	
	public LinkedList<NeighborProxy> getConvexEnvelope() {
		return this.convexEnvelope;
	}
	
	private LinkedList<NeighborProxy> findMyConvexEnvelope() {
		return findConvexEnvelope(this.idToProxyList(this.getParticularNeighbors(NeighborProxy.REGULAR)), this.mainVirtualEntity);
	}
	
	private LinkedList<NeighborProxy> findConvexEnvelope(Iterator it, VirtualEntity main) {
		LinkedList<NeighborProxy> set = this.iteratorToList(it);
		GiftWrapping algorithm = new GiftWrapping(set, main);
		return algorithm.findEnvelope();
	}
	
	private LinkedList<NeighborProxy> findConvexEnvelope(LinkedList<NeighborProxy> set, VirtualEntity main) {
		GiftWrapping algorithm = new GiftWrapping(set, main);
		return algorithm.findEnvelope();
	}
	private void setStatus(String status) {
		this.status = status;
	}
	
	private String getStatus() {
		return this.status;
	}
	
	private int simpleAngleSign(long [] a, long [] b, long [] c) {
		return VirtualWorld.simpleAngleSign(a,b,c);
	}
	
	private boolean simpleLeftFromLine(long [] a, long [] b, long [] point) {
		return simpleAngleSign(a,b,point) > 0;
	}
	
	
	private boolean simpleRightFromLine(long [] a, long [] b, long [] point) {
		return simpleAngleSign(a,b,point) < 0;
	}
	
	private LinkedList<VirtualEntity> solipsisProtocolToVirtualEntityList(LinkedList<SolipsisProtocol> list) {
		LinkedList<VirtualEntity> retList = new LinkedList<VirtualEntity>();
		int size = list.size();
		for (int i=0; i<size; i++) {
			retList.addFirst(list.get(i).getVirtualEntity());
		}
		
		return retList;
	}
	
	private boolean halfRound(long [] a, long [] b, long [] x1, long [] x2) {
		boolean ok = simpleAngleSign(a,b,x1)*simpleAngleSign(a,b,x2) <0;
		return ok;
	}
	
	
	private boolean notIn(NeighborProxy entity, LinkedList set) {
		boolean notin = true;
		int size = set.size();
		
		if (size != 0) { 
			if (set.get(0) instanceof SolipsisProtocol) {
				for(int i = 0; i < size; i++) {
					if (((SolipsisProtocol)set.get(i)).mainVirtualEntity.getId() == entity.getId()) {
						notin = false;
						break;
					}
				}
			} else {
				for(int i = 0; i < size; i++) {
					if (((NeighborProxy)set.get(i)).getId() == entity.getId()) {
						notin = false;
						break;
					}
				}
			}
		}
		return notin;
	}

	
	private boolean notIn(VirtualEntityInterface entity, LinkedList<SolipsisProtocol> set) {
		boolean notin = true;
		int size = set.size();
		if (size != 0) { 
			for(int i = 0; i < size; i++) {
				if (set.get(i).mainVirtualEntity.getId() == entity.getId()) {
					notin = false;
					break;
				}
			}
		}
		return notin;
	}
	
	private boolean notIn(NeighborProxy entity, Iterator set) {
		boolean notin = true;
		NeighborProxy current;
		
		while(set.hasNext()) {
			current = (NeighborProxy)((Map.Entry)set.next()).getValue();
			if (current.getId() == entity.getId()) {
				notin = false;
				break;
			}
		}
		return notin;
	}
	
	private long[] moduloModificationRule(long[] ref, long[] distant) {
		long[] subjective = new long[3];
		
		subjective[0] = distant[0];
		subjective[1] = distant[1];
		
		if (distant[0] > ref[0]) {
			if (distant[0] - ref[0] > Globals.mapSize / 2) {
				subjective[0] -= Globals.mapSize;
			} 
		} else {
			if (ref[0] - distant[0] > Globals.mapSize / 2) {
				subjective[0] += Globals.mapSize;
			}	
		}

		if (distant[1] > ref[1]) {
			if (distant[1] - ref[1] > Globals.mapSize / 2) {
				subjective[1] -= Globals.mapSize;
			}
		} else {
			if (ref[1] - distant[1] > Globals.mapSize / 2) {
				subjective[1] += Globals.mapSize;
			}	
		}
		
		return subjective;
	}
	
	public long[] subjectiveCoord(int id) {
		long[] distant, local, subjective = null;
		NeighborProxy proxy = this.proxies.get(id);
		

		if (proxy != null) {
			local = this.mainVirtualEntity.getCoord(); 
			distant = proxy.getCoord();
			subjective = this.moduloModificationRule(local, distant);
		}else{
			proxy = this.cache.getCache().get(id);
			local = this.mainVirtualEntity.getCoord(); 
			distant = proxy.getCoord();
			subjective = this.moduloModificationRule(local, distant);
		}
		return subjective;
	}
	
	public long[] subjectiveCoordHelp(long [] dest,long [] current) {
		long[] distant, local, subjective = null;
		


			local = dest; 
			distant = current;
			subjective = this.moduloModificationRule(local, distant);

		return subjective;
	}
	
	
	public long[] subjectiveCoord(long[] coord) {
		long[] local, subjective = null;
		
		local = this.mainVirtualEntity.getCoord(); 
		subjective = this.moduloModificationRule(local, coord);
		
		return subjective;
	}

	public boolean hasNeighbor(int id) {
		return this.proxies.containsKey(id);
	}
	
	public long[] realRelativeCoord(VirtualEntityInterface entity) {
		return this.moduloModificationRule(this.mainVirtualEntity.getCoord(), entity.getCoord());
	}
	
	public long[] realRelativeCoord(VirtualEntityInterface entity, long[] self) {
		return this.moduloModificationRule(self, entity.getCoord());
	}
	
	public long[] realRelativeCoord(long[] position) {
		return this.moduloModificationRule(this.mainVirtualEntity.getCoord(), position);
	}
	
	private int surroundingPositionConnectionAlgorithm(SolipsisProtocol currentClosest, SolipsisProtocol supposedGlobalClosest, LinkedList<SolipsisProtocol> around, int direction, int halfRound, boolean lookup, int hops, long[] goal, int lookupId, boolean useLongRange) {
		around.addFirst(currentClosest);
		long [] wantedPosition,currentClosestReal;
		HashMap<Integer, NeighborProxy> set;
		VirtualEntity closestNeighbor;
		
		if (lookup) {
			wantedPosition = goal;
			currentClosestReal = this.realRelativeCoord(currentClosest.getVirtualEntity(),wantedPosition);
		} else {
			wantedPosition = this.mainVirtualEntity.getCoord();
			currentClosestReal = this.realRelativeCoord(currentClosest.getVirtualEntity());
		}
		
		if (!lookup) {
			closestNeighbor = this.findClosest(currentClosest.proxies, wantedPosition, currentClosestReal, direction, around, lookup, lookupId);
			if(closestNeighbor == null) {
				this.mainVirtualEntity.setNeighbors(around);
				this.warnNeighbors();
				return hops;
			}
		} else {
			if (this.type == SolipsisProtocol.SMALLWORLD && useLongRange) {
				set = this.mergeSets(currentClosest.proxies, currentClosest.smallWorldModule.getLinks());
			} else {
				set = currentClosest.proxies;
			}
			closestNeighbor = this.findClosest(set, wantedPosition, currentClosestReal, direction, around, lookup, lookupId);
			if (closestNeighbor == null) {
				return -1;
			}
			if (closestNeighbor.getId() == lookupId) {
				return hops;
			}
		}
		
		long [] supposedGlobalClosestReal,closestNeighborReal;
		if (lookup) {
			supposedGlobalClosestReal = this.realRelativeCoord(supposedGlobalClosest.getVirtualEntity(), goal);
			closestNeighborReal = this.realRelativeCoord(closestNeighbor, goal);
		} else {
			supposedGlobalClosestReal = this.realRelativeCoord(supposedGlobalClosest.getVirtualEntity());
			closestNeighborReal = this.realRelativeCoord(closestNeighbor, supposedGlobalClosestReal);
		}
		 
		double distanceFromGlobalClosestNode = VirtualWorld.simpleDistance(supposedGlobalClosestReal, wantedPosition);
		double minDistanceFromCurrentNeigbors = VirtualWorld.simpleDistance(closestNeighborReal, wantedPosition);
		if (distanceFromGlobalClosestNode > minDistanceFromCurrentNeigbors && notIn(closestNeighbor,around)) {
			hops = jumpingToPositionConnectionAlgorithm(closestNeighbor.getProtocol(), lookup, 1 + hops, goal, lookupId, useLongRange);
		} else {
			halfRound+=(halfRound(wantedPosition, supposedGlobalClosestReal,currentClosestReal,closestNeighborReal))?1:0;
			if(closestNeighbor.getProtocol() == around.get(0) || halfRound == 2) {
				if (! lookup) {
					this.mainVirtualEntity.setNeighbors(around);
					this.warnNeighbors();
				} else {
					hops = -1;
				}
			} else {
				hops = surroundingPositionConnectionAlgorithm(closestNeighbor.getProtocol(), supposedGlobalClosest, around, direction,halfRound, lookup, 1 + hops, goal, lookupId, useLongRange);
			}
		}
		
		return hops;
	}
	
	private int jumpingToPositionConnectionAlgorithm(SolipsisProtocol bootstrap, boolean lookup, int hops, long[] goal, int lookupId, boolean useLongRange) {
		Iterator neighbors;
		long [] currentPosition; 
		long [] wantedPosition;
		VirtualEntity closest;
		LinkedList<SolipsisProtocol> around = null;
		
		if (lookup) {
			if (this.type == SolipsisProtocol.SMALLWORLD && useLongRange) {
				neighbors = this.mergeSets(bootstrap.proxies, bootstrap.smallWorldModule.getLinks()).entrySet().iterator();
			} else {
				neighbors = bootstrap.getNeighbors();
			}
			currentPosition = this.realRelativeCoord(bootstrap.getVirtualEntity(), goal);
			wantedPosition = goal;
		} else {
			neighbors = bootstrap.getNeighbors();
			currentPosition = this.realRelativeCoord(bootstrap.getVirtualEntity());
			wantedPosition = this.mainVirtualEntity.getCoord();
		}
		closest = this.findClosest(neighbors, wantedPosition, lookup, lookupId);
		/*
		 * in case node has no neighbors: this is used for the first inhabitant of the metaverse.
		 */
		if (!lookup) {
			if(closest == null) {
				around = new LinkedList<SolipsisProtocol>();
				around.add(bootstrap);			
				this.mainVirtualEntity.setNeighbors(around);
				this.warnNeighbors();
				return hops;
			}
		} else {
			if (closest.getId() == lookupId) {
				return hops;
			} 
		}
		long [] closestReal;
		
		if (lookup) {
			closestReal = this.realRelativeCoord(closest, goal);
		} else {
			closestReal = this.realRelativeCoord(closest);
		}
		
		double distanceFromCurrentNode = VirtualWorld.simpleDistance(currentPosition, wantedPosition);
		double minDistanceFromCurrentNeigbors = VirtualWorld.simpleDistance(closestReal, wantedPosition);
		if (distanceFromCurrentNode <= minDistanceFromCurrentNeigbors) {
			around = new LinkedList<SolipsisProtocol>();
			around.addFirst(bootstrap);
			if(this.simpleLeftFromLine(wantedPosition, currentPosition, closestReal)) {
				hops = surroundingPositionConnectionAlgorithm(closest.getProtocol(), bootstrap, around, VirtualWorld.LEFT,0, lookup, 1 + hops, goal, lookupId, useLongRange);
			} else  {
				hops = surroundingPositionConnectionAlgorithm(closest.getProtocol(), bootstrap, around, VirtualWorld.RIGHT,0, lookup, 1 + hops, goal, lookupId, useLongRange);
			}
		} else {
			hops = jumpingToPositionConnectionAlgorithm(closest.getProtocol(), lookup, 1+hops, goal, lookupId, useLongRange);
		}
		
		return hops;
	}
	
	public VirtualEntity neighborProxyToVirtualEntity(NeighborProxy proxy) {
		Node n;
		VirtualEntity answer;
		
		n = Network.get(proxy.getPeersimNodeId());
		if (n == null) {
			answer = null;
		} else {
			answer = ((SolipsisProtocol)n.getProtocol(this.protocolId)).getVirtualEntity();
		}
		return answer;
	}
	
	private void warnNeighbors() {
		Iterator it;
		NeighborProxy current;
		
		it = this.proxies.entrySet().iterator();
		while(it.hasNext()) {
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			neighborProxyToVirtualEntity(current).addNeighbor(this);
		}
	}
	
	
	private void warnNeighbors(LinkedList<NeighborProxy> around, NeighborProxy src) {
		int size;
		
		size = around.size();
		for (int i = 0; i < size; i++) {
			this.sendLookedUpMsg(around.get(i), src);
		}
		
	}
	
	private void sendLookedUpMsg(NeighborProxy dest, NeighborProxy src) {
		Message msg;
		
		msg = new Message(Message.LOOKED_UP, this.peersimNodeId, this.mainVirtualEntity.getId(), dest.getId(), src);
		
		this.send(msg, dest);
	}

	/**
	 * picks from the EntitySet the VirtualEntity which position is the closest to the given position 
	 * @param entitySet the set of virtual entities to search in
	 * @param position the position we want to reach
	 * @return the VirtualEntity
	 */
	private VirtualEntity findClosest(Iterator neighbors, long[] position, boolean lookup, int lookupId) {
		
		NeighborProxy current = null;
		double currentDist;
		NeighborProxy nearest = null;
		double min = Double.MAX_VALUE;
		long [] currentCoordReal;
		while(neighbors.hasNext()) {
			current = (NeighborProxy)((Map.Entry)neighbors.next()).getValue();
			if (lookup) {
				currentCoordReal = this.realRelativeCoord(this.neighborProxyToVirtualEntity(current), position);
			} else {
				currentCoordReal = this.realRelativeCoord(this.neighborProxyToVirtualEntity(current));
			}
			if (lookup && lookupId == current.getId()) {
				nearest = current;
				break;
			}
			currentDist = VirtualWorld.simpleDistance(currentCoordReal,position);
			if (currentDist < min) {
				min = currentDist;
				nearest = current;
			}
		}
		
		return (nearest != null)?this.neighborProxyToVirtualEntity(nearest):null;
	}
	
	/**
	 * picks from {EntitySet}-{around} the VirtualEntity which position is the closest to the given position 
	 * @param entitySet the set of virtual entities to search in
	 * @param around a set of entities to exclude
	 * @param position the position we want to reach
	 * @return the VirtualEntity
	 */
	private VirtualEntity findClosest(HashMap<Integer,NeighborProxy> neighbors, long[] position, long [] start, int direction, LinkedList<SolipsisProtocol> around, boolean lookup, int lookupId) {
		
		NeighborProxy current = null;
		double currentDist;
		NeighborProxy nearest = null;
		double min = Double.MAX_VALUE;
		long [] currentCoord = null;
		boolean sameDirection;
		Iterator entitySet;
		entitySet = neighbors.entrySet().iterator();
		while(entitySet.hasNext()) {
			current = (NeighborProxy)((Map.Entry)entitySet.next()).getValue();
			if (lookup) {
				currentCoord = this.realRelativeCoord(this.neighborProxyToVirtualEntity(current), position);
			} else {
				currentCoord = this.realRelativeCoord(this.neighborProxyToVirtualEntity(current));
			}
			if (lookup && (lookupId == current.getId())) {
				nearest = current;
				break;
			}
				sameDirection = ((direction == VirtualWorld.RIGHT) && this.simpleRightFromLine(position, start, currentCoord)) ||
				((direction == VirtualWorld.LEFT)  && this.simpleLeftFromLine(position, start, currentCoord));
				if(sameDirection && notIn(current,around)) {
					currentDist = VirtualWorld.simpleDistance(position,currentCoord);
					if (currentDist < min) {
						min = currentDist;
						nearest = current;
					}
				}			
		}

		return (nearest != null)?this.neighborProxyToVirtualEntity(nearest):null;
	}

	public void disconnect() {
		Iterator it;
		NeighborProxy neighbor;
		
		it = this.proxies.entrySet().iterator();
		
		while (it.hasNext()) {
			neighbor = (NeighborProxy)((Map.Entry)it.next()).getValue();
			this.sendDisconnectMessage(neighbor);
		}
		
	}
	

}
