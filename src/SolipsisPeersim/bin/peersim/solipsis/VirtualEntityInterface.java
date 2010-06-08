package peersim.solipsis;

import java.util.LinkedList;

public interface VirtualEntityInterface {
	public long[] getCoord();
	public boolean doesMove();
	public int getOrder();
	public LinkedList<VirtualEntityInterface> getNeighbors();
	public double getKnowledgeRay();
	public LinkedList getConvexEnvelope();
	public long[] relativeCoord(VirtualEntityInterface entity);
	public long[] relativeCoord(long[] position);
	public int getId();
//	public boolean specialView(VirtualEntityInterface entity);
	public boolean isStabilized();
	public long[] subjectiveCoord(int id);
	public int getQualityOf(int id);
	public boolean isFull();
}
