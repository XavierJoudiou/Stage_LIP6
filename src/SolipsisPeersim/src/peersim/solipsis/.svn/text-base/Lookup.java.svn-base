package peersim.solipsis;

import java.util.LinkedList;

public class Lookup {
	public static final int LOOKUP = 0;
	public static final int JOIN   = 1;
	public static final int REPLY  = 2;
	
	
	private long[] coordinates, supposedGlobalClosest;
	private LinkedList<NeighborProxy> around;
	private int direction, halfRound, kind;
	private NeighborProxy src;
	private int hops;
	private int id;
	
	Lookup(int kind, NeighborProxy src, long[] coordinates) {
		this.src = src;
		this.coordinates = coordinates;
		this.halfRound = 0;
		this.around = null;
		this.direction = 0;
		this.hops = 0;
		this.setKind(kind);
	}
	
	Lookup(LinkedList<NeighborProxy> around) {
		this.setKind(Lookup.REPLY);
		this.around = around;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getDirection() {
		return this.direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	
	public long[] getCoordinates() {
		return this.coordinates;
	}

	public void setAround(LinkedList<NeighborProxy> around) {
		this.around = around;	
	}
	
	public LinkedList<NeighborProxy> getAround() {
		return around;
	}
	
	public long[] getSupposedGlobalClosest() {
		return this.supposedGlobalClosest;
	}
	
	public void setSupposedGlobalClosest(long[] closest) {
		this.supposedGlobalClosest = closest;
	}
	
	public void setHalfRound(int halfRound) {
		this.halfRound = halfRound;
	}
	
	public int getHalfRound() {
		return this.halfRound;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public int getKind() {
		return kind;
	}

//	public void setSource(NeighborProxy src) {
//		this.src = src;
//	}

	public NeighborProxy getSource() {
		return src;
	}

	public void incrementHops() {
		this.hops++;
	}

	public int getHops() {
		return hops;
	}
}
