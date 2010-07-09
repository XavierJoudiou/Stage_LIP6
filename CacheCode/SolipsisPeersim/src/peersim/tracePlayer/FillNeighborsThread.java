package peersim.tracePlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import peersim.solipsis.*;

import java.lang.InterruptedException;

public class FillNeighborsThread extends Thread {
	
	private VirtualWorldDistributionShell toOrganize;
	private StepBuffer buffer;
	private boolean finish;
	private boolean awaiting;
	
	FillNeighborsThread(StepBuffer buffer) {
		this.buffer = buffer;
		this.finish = false;
		this.awaiting = false;
		this.toOrganize = null;
	}
	
	public void organize(VirtualWorldDistributionShell toOrganize) throws InterruptedException{
		synchronized(this) {
			while (this.toOrganize != null) {
				this.wait();
			}
			this.toOrganize = toOrganize;
			this.notify();
		}
	}
	
	public void finish() {
		synchronized(this) {
			this.finish = true;
			this.notify();
		}
	}
	
	@Override
	public void run() {
		HashMap<Integer,VirtualEntityInterface> distrib;
		while(!finish) {
			synchronized(this) {
				while (this.toOrganize == null && !finish) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
				if (finish) continue;
				distrib = this.toOrganize.getDistribution();
				Iterator it = distrib.entrySet().iterator();
				VirtualEntityShell entity;
				VirtualEntityInterface current;
				int count,readValue = 1;
				LinkedList<VirtualEntityInterface> neighborEntities;
				LinkedList<Integer> neighbors;
				
				while(it.hasNext()) {
					entity = (VirtualEntityShell)((Map.Entry)it.next()).getValue();
					if (!entity.isFull()) break;
					entity.setOrder(readValue);
					readValue++;
					neighborEntities = new LinkedList<VirtualEntityInterface>();
					neighbors = entity.getNeighborIds();
					count = neighbors.size();
					for (int i = 0; i < count; i++) {
						current = distrib.get(neighbors.get(i));
						if(current != null){ 
							neighborEntities.add(current);
						}
					}
					entity.setNeighbors(neighborEntities);
				}
				this.buffer.put(this.toOrganize);
				this.toOrganize = null;
				//if (this.awaiting) {
					this.notify();
				//}
			}
		}
	}

}
