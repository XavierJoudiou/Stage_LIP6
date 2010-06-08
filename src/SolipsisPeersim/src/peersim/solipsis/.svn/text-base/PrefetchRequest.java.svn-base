package peersim.solipsis;

public class PrefetchRequest {
	
	private long[] origin;
	private long[] prefetchVector;
	private int speed;
	private long estimatedRTT;
	private NeighborProxy source;
	
	PrefetchRequest(NeighborProxy source, long[] origin, long[] prefetchVector, int speed) {
		this.source 			= source;
		this.origin 			= origin;
		this.prefetchVector		= prefetchVector;
		this.speed 				= speed;
	}
	
	public void setSource(NeighborProxy source) {
		this.source = source;
	}
	
	public void setAverageRTT(long value) {
		this.estimatedRTT = value;
	}
	
	public long getAverageRTT() {
		return this.estimatedRTT;
	}
	
	public NeighborProxy getSource() {
		return this.source;
	}
	
	public void setOrigin(long[] origin) {
		this.origin = origin;
	}
	
	public long[] getOrigin() {
		return this.origin;
	}
	
	public void setPrefetchVector(long[] prefetchVector) {
		this.prefetchVector = prefetchVector;
	}
	
	public long[] getPrefetchVector() {
		return this.prefetchVector;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public long getSpeed() {
		return this.speed;
	}
}
