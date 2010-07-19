package peersim.solipsis;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.LinkedList;
import peersim.core.*;
import peersim.solipsis.SolipsisProtocol;
import peersim.solipsis.VirtualEntity;

public class VirtualEntity implements VirtualEntityInterface {
	
	private long [] coord;
	private int id;
//	private LinkedList<VirtualEntity> neighbors;
	private SolipsisProtocol protocol;
	private int order;
	private int acceleration;
	private int maxSpeed;
	private double direction;
	private long startTime;
	private long [] originCoord;
	private long [] destination;
	private boolean active;
	
	private MobilityStateMachine behavior;
	
	private LinkedList<Integer> addedInMovement;
	
	VirtualEntity() {
		this.coord = new long[3];
		this.generateId();
//		this.neighbors = new LinkedList<VirtualEntity>();
		this.acceleration = 500;
		this.startTime = CommonState.getTime();
		this.originCoord = new long[3];
		this.behavior = new MobilityStateMachine(this);
		this.active = false;
		this.addedInMovement = new LinkedList<Integer>();
	}
	
	VirtualEntity(long [] coord) {
		this();
		this.coord = coord;
		this.setOrigin();
	}
	
	public MobilityStateMachine getStateMachine() {
		return this.behavior;
	}
	
	public int getAcceleration() {
		return this.acceleration;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public long[] getCoord() {
		return this.coord;
	}
	
	public boolean isFull() {
		return true;
	}
	
	public void setMaxSpeed(int speed) {
		this.maxSpeed = speed;
	}
	
	public int getMaxSpeed() {
		return this.maxSpeed;
	}
	
	public int getQualityOf(int id) {
		return this.protocol.getProxies().get(id).getQuality();
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean isStabilized() {
		return this.protocol.isStabilized();
	}
	
	public long[] getDestination() {
		return this.destination;
	}
	
	public void setProtocol(SolipsisProtocol protocol) {
		this.protocol = protocol;
	}
	
	public int getState() {
		return this.behavior.getState();
	}
	
	public SolipsisProtocol getProtocol() {
		return this.protocol;
	}
	
	public double getKnowledgeRay() {
		return this.protocol.getKnowledgeRay();
	}
	
	@Override
	public String toString() {
		return "Entity " + id + ": (" + coord[0] + "," + coord[1] + "," + coord[2] + ")";
	}
	
	public void setNeighbors(LinkedList<SolipsisProtocol> neighbors) {
		int size = neighbors.size();
		SolipsisProtocol neighbor;
		for (int i = 0; i < size; i++) {
			neighbor = neighbors.get(i);
			this.addNeighbor(neighbor);
		}
	}

	
	
	public void addNeighbor(SolipsisProtocol neighbor) {
		if(neighbor != this.protocol && !this.protocol.hasNeighbor(neighbor.getVirtualEntity().getId())) {
			this.protocol.addLocalView(neighbor.getVirtualEntity());
//			this.protocol.authorize(neighbor.getVirtualEntity().getId());
		}
	}
	
	private void markAsAddedInMovement(int id) {
		if (!addedInMovement(id)) {
			this.addedInMovement.add(id);
		} else {
			System.exit(20);
		}
	}
	
	public boolean addedInMovement(int id) {
		int size = this.addedInMovement.size();
		boolean answer = false; 
		
		for (int i = 0; i < size; i++) {
			if (this.addedInMovement.get(i).intValue() == id) {
				answer = true;
				break;
			}
		}
		
		return answer;
	}
	
	private void removeFromAddedInMovement(int id) {
		int size = this.addedInMovement.size();
		
		for (int i = 0; i < size; i++) {
			if (this.addedInMovement.get(i).intValue() == id) {
				this.addedInMovement.remove(i);
				break;
			}
		}		
	}
	
	public void addNeighbor(NeighborProxy neighbor) {
		if(neighbor.getId() != this.getId() && !this.protocol.hasNeighbor(neighbor.getId())) {
			this.protocol.addLocalView(neighbor);
//			if (neighbor.getQuality() == NeighborProxy.REGULAR) {
				this.protocol.setConnectionTime(neighbor.getId());
				if (this.getState() == MobilityStateMachine.TRAVELLING && !Globals.realTime) {
					this.markAsAddedInMovement(neighbor.getId());
				}
//			}
//			this.protocol.authorize(neighbor.getId());
		}
	}
	
//	public void addNeighbor(VirtualEntity neighbor, int[] coefs) {
//		if(this.notIn(neighbor, this.neighbors) && neighbor != this) {
//			this.neighbors.add(neighbor);
//			this.protocol.addLocalView(neighbor, coefs);
//			this.protocol.authorize(neighbor);
//		}
//	}
	
//	public boolean specialView(VirtualEntityInterface entity) {
//		return this.protocol.specialView(entity);
//	}
	
	public void removeNeighbor(int neighbor) {
		this.protocol.removeProxy(neighbor);
		if (!Globals.realTime) {
			this.removeFromAddedInMovement(neighbor);
		}
		//		this.protocol.ban(neighbor);
	}
	
	public void removeUnwantedNeighbors() {
		this.protocol.removeUnwantedNeighbors();
	}
	
	public boolean isAheadOfMovement(int id) {
		long[] directionVector, entityVector;
		long[] entity = this.relativeCoord(this.getNeighbor(id).getCoord());
		long[] destination = this.relativeCoord(this.getDestination());
		long[] position = this.getCoord();
		long dotProduct;
//		boolean answer = false;
		
		directionVector = new long[2];
		entityVector = new long[2];
		directionVector[0] = destination[0] - position[0];
		directionVector[1] = destination[1] - position[1];
		entityVector[0] = entity[0] - position[0];
		entityVector[1] = entity[1] - position[1];
		
		dotProduct = directionVector[0]*entityVector[0] + directionVector[1]*entityVector[1];
		
		return dotProduct > 0;
	}
	
//	public void mobilitySentinel
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public void propagateStateInformation() {
		this.protocol.propagateStateInformation();
	}
	
	public void setStateUpdateTimer(int state) {
		if (this.protocol!=null) {
//		if (Globals.topologyIsReady) {
			this.protocol.setStateUpdateTimer(state);
		}
	}

	public void proceedMovement() {
		VirtualEntityInterface shell;
		
		if (Globals.generated) {
			this.behavior.nextStep();
			this.moveOn();
			if (this.getState() != MobilityStateMachine.TRAVELLING) {
				if (this.addedInMovement.size() != 0) {
					this.addedInMovement.clear();
				}
			}
		} else {
			shell = Globals.slTrace.getTrace().get(Globals.stepCount).getDistribution().get(this.id);
			if (shell != null) {
				this.coord = shell.getCoord();
			}
		}
	}
	
	public void newDestination(long[] destination) {
		this.setOrigin();
		this.destination = destination;
		this.direction = VirtualWorld.calculateDirection(this.originCoord, this.destination);
		this.startTime = CommonState.getTime();
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * bootstrapping the automaton with default parameters
	 */
	public void activate() {
		long[] zoneOfInterest;
		long zoneSize;

		zoneSize = Globals.zoneSize;
		zoneOfInterest = new long[3];
		zoneOfInterest[0] = this.coord[0] - zoneSize / 2;
		zoneOfInterest[1] = this.coord[1] - zoneSize / 2;
		
		this.activate(zoneOfInterest, zoneSize);
	}
	
	public void activate(long [] zoneOfInterest, long zoneSize) {
		this.active = true;
		this.behavior.bootstrapStateMachine(zoneOfInterest, zoneSize);
	}
	
	public boolean doesMove() {
		return this.behavior.getState() != MobilityStateMachine.HALTED;
	}
	
	public void keepTopology() {
//			if (this.protocol.getType() == SolipsisProtocol.ENHANCED) {
//				this.protocol.getPrefetchingModule().prospectPrefetched();
//			}

//		System.out.println("maintaining Knowledge");
		this.protocol.maintainKnowledgeZone();
//		System.out.println("maintaining Topology "+CommonState.getTime());
		this.protocol.maintainTopology();
//		System.out.println("end of keepTopo");
	}
	
	public void refreshState() {
		this.protocol.refreshState();
	}
	
	public void updateStateOf(VirtualEntity entity) {
		this.protocol.updateStateOf(entity);
	}
	
	public long[] relativeCoord(VirtualEntityInterface entity) {
		return this.protocol.realRelativeCoord(entity);
	}
	
	public long[] relativeCoord(long[] position) {
		return this.protocol.realRelativeCoord(position);
	}
	
	public LinkedList<NeighborProxy> getConvexEnvelope() {
		return this.getProtocol().getConvexEnvelope();
	}
	
	public long[] subjectiveCoord(int id) {
		return this.protocol.subjectiveCoord(id);
	}
	
	public long[] subjectiveCoord(long[] coord) {
		return this.protocol.subjectiveCoord(coord);
	}
	
//	private boolean notIn(VirtualEntity entity, LinkedList<VirtualEntity> set) {
//		boolean notin = true;
//		int size = set.size();
//		for(int i = 0; i < size; i++) {
//			if (set.get(i) == entity) {
//				notin = false;
//				break;
//			}
//		}
//		
//		return notin;
//	}
	public LinkedList<VirtualEntityInterface> getNeighbors() {
		Iterator it = this.protocol.getNeighbors();
		VirtualEntityInterface current;
		LinkedList<VirtualEntityInterface> list = new LinkedList<VirtualEntityInterface>();
		
		while(it.hasNext()) {
			current = this.protocol.neighborProxyToVirtualEntity(((NeighborProxy)((Map.Entry)it.next()).getValue()));
			list.add(current);
		}
		
		return list;
	}
	
	public NeighborProxy getNeighbor (int id) {
		return this.protocol.getProxies().get(id);
	}
	
	private void moveOn() {
		long timeDelta;
		if (this.active) {
			timeDelta = CommonState.getTime() - this.startTime;
			this.coord = VirtualWorld.move(this.coord, this.direction, this.acceleration, this.maxSpeed, timeDelta);
		}
	}
	
	private void setOrigin() {
		this.originCoord[0] = this.coord[0];
		this.originCoord[1] = this.coord[1];
		this.originCoord[2] = this.coord[2];
	}
	
	private void generateId() {
		this.id = new Random().nextInt();
	}
	
	
}
