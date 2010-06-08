package peersim.solipsis;

import java.util.HashMap;

public interface LinkSet {
	public NeighborProxy addLink(NeighborProxy proxy);
	public HashMap<Integer,NeighborProxy> getLinks();
	public void removeLinks();
	public int size();
	public NeighborProxy getLink(int id);
	public boolean contains(int peerToForget);
}
