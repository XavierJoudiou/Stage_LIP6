/**
 * 
 */
package peersim.solipsis;

import java.util.LinkedList;

/**
 * @author xavier
 *
 */
public class CacheHelpRequest {
	
	private GeometricRegion region;
	private LinkedList<NeighborProxy> envelop;
	private long [] destination;
 	
	
	public CacheHelpRequest(GeometricRegion region,
			LinkedList<NeighborProxy> envelop, long[] destination) {
		super();
		this.region = region;
		this.envelop = envelop;
		this.destination = destination;
	}


	
	public long[] getDestination() {
		return destination;
	}
	
	public void setDestination(long[] destination) {
		this.destination = destination;
	}
	
	public GeometricRegion getRegion() {
		return region;
	}

	public void setRegion(GeometricRegion region) {
		this.region = region;
	}

	public LinkedList<NeighborProxy> getEnvelop() {
		return envelop;
	}

	public void setEnvelop(LinkedList<NeighborProxy> envelop) {
		this.envelop = envelop;
	}

}
