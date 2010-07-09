package peersim.tracePlayer;

import java.util.LinkedList;

import peersim.solipsis.Globals;
import peersim.solipsis.VirtualEntityInterface;
import peersim.solipsis.GiftWrapping;
import java.util.HashMap;

public class VirtualEntityShell implements VirtualEntityInterface {

	private int id;
	private long[] coord;
	private LinkedList<VirtualEntityInterface> neighbors;
	private double interestRadius;
	private LinkedList<Integer> neighborIds;
	private HashMap<Integer,Integer> qualities;
	private boolean moving;
	private int order;
	private boolean stabilized;
	private boolean full;
	
	VirtualEntityShell(int id, long[] coord, LinkedList<Integer> neighborIds, HashMap<Integer,Integer> qualities, double interestRadius, boolean stabilized) {
		this.id 			= id;
		this.coord          = coord;
		this.neighborIds    = neighborIds;
		this.interestRadius = interestRadius;
		this.stabilized     = stabilized;
		this.qualities 		= qualities;
		this.full 			= true;
	}
	
	public VirtualEntityShell(int id, long [] coord) {
		this.id = id;
		this.coord = coord;
		this.full = false;
	}
	
	public boolean doesMove() {
		return this.moving;
	}
	
	public boolean isStabilized() {
		return this.stabilized;
	}
	
	public boolean isFull() {
		return this.full;
	}
	
	public void setMovement(boolean moving) {
		this.moving = moving;
	}
	
	public int getQualityOf(int id) {
		return this.qualities.get(id).intValue();
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setCoord(long [] coord) {
		this.coord = coord;
	}
	
	public long[] getCoord() {
		return this.coord;
	}
	
	public void setNeighborIds(LinkedList<Integer> ids) {
		this.neighborIds = ids;
	}
	
	public LinkedList<Integer> getNeighborIds() {
		return this.neighborIds;
	}
	
	public void setNeighbors(LinkedList<VirtualEntityInterface> neighbors) {
		this.neighbors = neighbors;
	}
	
	public LinkedList<VirtualEntityInterface> getNeighbors() {
		return this.neighbors;
	}
	
	public void setInterestRadius(long interestRadius) {
		this.interestRadius = interestRadius;
	}
	
	public double getKnowledgeRay() {
		return this.interestRadius;
	}
	
	public LinkedList<VirtualEntityInterface> getConvexEnvelope() {
			GiftWrapping algorithm = new GiftWrapping(this.neighbors, this);
			return algorithm.findEnvelope();
	}
	
	public long[] relativeCoord(VirtualEntityInterface entity) {
		return this.moduloModificationRule(this.coord, entity.getCoord());
	}
	
	public long[] relativeCoord(long[] position) {
		return this.moduloModificationRule(this.coord, position);
	}
	
	public long[] subjectiveCoord(int id) {
		VirtualEntityInterface neighbor = this.getNeighbor(id);
		return this.moduloModificationRule(this.coord, neighbor.getCoord());
	}
	
	private VirtualEntityInterface getNeighbor(int id) {
		VirtualEntityInterface current, chosen = null;
		int size = this.neighbors.size();
		
		for (int i = 0; i < size; i++) {
			current = this.neighbors.get(i);
			if (current.getId() == id) {
				chosen = current;
				break;
			}
		}
		
		return chosen;
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
}
