package peersim.tracePlayer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import peersim.solipsis.*;

import java.util.*;

public class InteractiveInterface extends MouseAdapter {
	
	private final static int DELTA = 5;
	
	private VirtualWorldMonitor monitor;

	public InteractiveInterface (VirtualWorldMonitor monitor) {
		super();
		this.monitor = monitor;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		HashMap<Integer,VirtualEntityInterface> distribution = monitor.getDistribution().getDistribution();
		Iterator it = distribution.entrySet().iterator();
		VirtualEntityInterface entity = null;
		int topologyHealth = 0;
		int overall = 0;
		
		while(it.hasNext()) {
			entity = (VirtualEntityInterface)((Map.Entry)it.next()).getValue();
			if (entitysRegion(entity, e)) {
				monitor.setPolygonDrawing(entity.getId());
				this.monitor.clearScreen();
				this.monitor.updateVirtualWorld();
				printEntityInformation(entity);
			}
			if (entity.isStabilized()) {
				topologyHealth++;
			}
			overall++;
		}
		int distPerPixel = (int) (Globals.mapSize / Globals.screenSize);
		System.out.println("Topology Stabilization level: "+((topologyHealth*100)/overall)+"% ("+topologyHealth+" nodes)");// (click coordinates: "+e.getX()*distPerPixel+","+e.getY()*distPerPixel+")");
	}
	
	private void printEntityInformation(VirtualEntityInterface entity) {
		int count = 0;
		int pref = 0;
		int lr   = 0;
		LinkedList<VirtualEntityInterface> neighbors = entity.getNeighbors();
		int size = neighbors.size();
		
		for (int i = 0; i < size; i++) {
			if (entity.getQualityOf(neighbors.get(i).getId()) == NeighborProxy.REGULAR) {
				count++;
			} else if (entity.getQualityOf(neighbors.get(i).getId()) == NeighborProxy.PREFETCHED) {
				pref++;
			} else if (entity.getQualityOf(neighbors.get(i).getId()) == NeighborProxy.LONGRANGE){
				lr++;
			}
		}
		System.out.println("Entity Information {");
		System.out.println("  Id: " + entity.getId());
		System.out.println("  Coordinates: " + "("+entity.getCoord()[0]+","+entity.getCoord()[1]+")");
		System.out.println("  Knowledge Radius: " + entity.getKnowledgeRay());
		System.out.println("  Regular Neighbors: " + count);
		System.out.println("  Prefetched Neighbors: " + pref);
		System.out.println("  Long-Range Neighbors: " + lr);
		System.out.println("  ConvexPolygon rule status: "+ ((entity.isStabilized())?"OK":"Compromised"));
		System.out.println("}");
	}
	
	private boolean entitysRegion(VirtualEntityInterface entity, MouseEvent e) {
		int[] screenCoord = monitor.virtualCoordToScreenCoord(entity.getCoord());
		int x = e.getX();
		int y = e.getY();
		
		boolean xOk = (screenCoord[0] - DELTA) < x && x < (screenCoord[0] + DELTA);
		boolean yOk = (screenCoord[1] - DELTA) < y && y < (screenCoord[1] + DELTA);
		
		return xOk && yOk;
	}
}
