package peersim.solipsis;

import java.util.Arrays;

public class NeighborProxy {
	public final static int REGULAR    = 0;
	public final static int PREFETCHED = 1;
	public final static int CACHED     = 2;
	public final static int LONGRANGE  = 3;
	
	private long[] proxyCoord;
	private double proxyRadius;
	private int id;
	private int peersimId;
	private int quality;
	
	/* pour dater les nœuds, pour le cache */
	private long time;
	
	
	private PrefetchRequest prefetchData;
	
	NeighborProxy() {
		proxyCoord = new long[3];
		proxyRadius = 0;
		this.quality = REGULAR;
		this.prefetchData = null;
		this.time = -1;
	}
	
	public	NeighborProxy(long[] proxyCoord, double proxyRadius, int id, int peersimId) {
		this();
		this.proxyCoord[0] = proxyCoord[0];
		this.proxyCoord[1] = proxyCoord[1];
		this.proxyRadius = proxyRadius;
		this.id = id;
		this.peersimId = peersimId;
		this.time = -1;

	}
	
	NeighborProxy(long[] proxyCoord, double proxyRadius, int id, int peersimId, int quality) {
		this(proxyCoord, proxyRadius, id, peersimId);
		this.quality = quality;
	}
	
	public PrefetchRequest getPrefetchData() {
		if (this.quality == PREFETCHED) {
			return this.prefetchData;
		} else {
			return null;
		}
	}
	
	public void setPrefetchData(PrefetchRequest request) {
		if (this.quality == PREFETCHED) {
			this.prefetchData = request;
		} else {
			this.prefetchData = null;
		}
	}
	
	@Override
	public NeighborProxy clone() {
		NeighborProxy dolly = new NeighborProxy(this.proxyCoord,this.proxyRadius,this.id,this.peersimId,this.quality);
		return dolly;
	}
	
	public int getQuality() {
		return this.quality;
	}
	
	public void setQuality(int quality) {
		this.quality = quality;
	}
	
	public long[] getCoord() {
		return this.proxyCoord;
	}
	
	public void setCoord(long[] proxyCoord) {
		this.proxyCoord[0] = proxyCoord[0];
		this.proxyCoord[1] = proxyCoord[1];
	}
	
	public double getRadius() {
		return this.proxyRadius;
	}
	
	public void setRadius(double radius) {
		this.proxyRadius = radius;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getPeersimNodeId() {
		return this.peersimId;
	}

	@Override
	public String toString() {
		return "NeighborProxy [id=" + id + ", peersimId=" + peersimId
				+ ", prefetchData=" + prefetchData + ", proxyCoord="
				+ Arrays.toString(proxyCoord) + ", proxyRadius=" + proxyRadius
				+ ", quality=" + quality + "]";
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}
	
	
	
}