package peersim.tracePlayer;

import java.util.LinkedList;

public class StepBuffer {
	private LinkedList<VirtualWorldDistributionShell> steps;
	private int listSize;
	private int awaiting;
	private int prebuffering;
	private boolean loaded;
	
	StepBuffer() {
		this.steps = new LinkedList<VirtualWorldDistributionShell>();
		this.listSize = 0;
		this.awaiting = 0;
		this.prebuffering = 2;
	}
	
	public void put(VirtualWorldDistributionShell step) {
		synchronized(this) {
			steps.add(step);
			this.listSize++;
			
			if (this.listSize > this.awaiting + this.prebuffering) {
				this.notify();
			}
		}
	}
	
	public int size() {
		return this.listSize;
	}
	
	public synchronized void fileLoaded() {
		this.loaded =true;
	}
	
	public synchronized boolean isFileLoaded() {
		return this.loaded;
	}
	
	public VirtualWorldDistributionShell get(int step) throws java.lang.InterruptedException {
		VirtualWorldDistributionShell element = null;
		synchronized(this) {
			if (step >= this.listSize) {
				this.awaiting = step;
				this.wait();
			}
			element = this.steps.get(step);
		}
		
		return element;
	}
}
