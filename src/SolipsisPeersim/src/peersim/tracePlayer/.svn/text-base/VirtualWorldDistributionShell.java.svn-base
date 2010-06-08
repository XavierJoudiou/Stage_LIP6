package peersim.tracePlayer;

import java.util.HashMap;

import peersim.solipsis.Globals;
import peersim.solipsis.VirtualEntityInterface;
import peersim.solipsis.VirtualWorldDistributionInterface;

public class VirtualWorldDistributionShell implements VirtualWorldDistributionInterface {
	
	private HashMap<Integer,VirtualEntityInterface> distribution;
	private long mapSize;
	
	public VirtualWorldDistributionShell(long mapSize) {
		this.distribution = new HashMap<Integer,VirtualEntityInterface>();
		this.mapSize = mapSize;
		Globals.mapSize = mapSize;
	}
	
	public void addToDistribution(VirtualEntityInterface entity) {
		this.distribution.put(entity.getId(), entity);
	}
	
	public long getMapSize() {
		return this.mapSize;
	}
	
	public HashMap<Integer,VirtualEntityInterface> getDistribution() {
		return this.distribution;
	}
	
	public boolean HQRenderingAvailable() {
		return false;
	}
	
	public long[] getFormerLocation(long id) {
		VirtualEntityInterface entity = distribution.get(id);
		long[] formerLocation = null;
		
		if (entity != null) {
			formerLocation = entity.getCoord();
		}
		return formerLocation;
	}
	
	public int countAvatars() {
		return this.distribution.size();
	}
}
