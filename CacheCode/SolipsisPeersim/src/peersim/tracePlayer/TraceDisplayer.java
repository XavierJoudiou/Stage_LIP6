package peersim.tracePlayer;

import java.io.IOException;

import peersim.solipsis.Globals;
import peersim.solipsis.VirtualWorldMonitor;
import peersim.solipsis.VirtualWorldDistributionInterface;

public class TraceDisplayer {
	
	private TraceReader reader;
	private boolean eof;
	private VirtualWorldMonitor monitor;
	private int dispNum;
	private int tick;
	private StepBuffer buffer;
	private boolean byStep;
	private boolean unpaused;
	private boolean topology;
	
	TraceDisplayer(TraceReader reader) {
		this.reader = reader;
		this.eof = false;
		this.buffer = reader.getBuffer();
		this.dispNum = 0;
		this.byStep = false;
		monitor = null;//new VirtualWorldMonitor(null);
//		monitor.setPolygonDrawing(116);
		this.tick = 50;
		this.topology = Globals.drawTopology;
		this.unpaused = false;
	}
	
	public void toggleTopology() {
		this.topology = !topology;
		this.monitor.setTopology(this.topology);
		this.monitor.clearScreen();
		this.monitor.updateVirtualWorld();
	}
	
	public void showProgression(int steps) {
		if (monitor!=null)
			this.monitor.drawLoading(steps);
	}
	
	public void fileLoaded() {
		this.monitor.fileLoaded();
	}
	
	public void pause() {
		this.tick = Integer.MAX_VALUE;
	}
	
	public void stop() {
		this.pause();
		this.dispNum = 0;
		this.monitor.clearScreen();
	}
	
	public void play() {
		synchronized(this) {
			this.tick = 50;
			this.byStep = false;
			this.notify();
		}
	}
	
	public void backwards() {
		if (this.unpaused) {
			this.pause();
			this.unpaused = false;
		} else {
			this.dispNum = (this.dispNum == 0)?0:this.dispNum-1;
			this.byStep = true;
			displayNextStep();
		}

//		this.byStep = true;
//		this.stopping = true;
//		if (this.byStep)
//		else

	}
	
	public void forward() {
		if (this.unpaused) {
			this.pause();
			this.unpaused = false;
		} else {
//		this.stopping = true;
			this.dispNum = (this.dispNum >= this.buffer.size())?this.dispNum-1:this.dispNum+1;
			this.byStep = true;
			displayNextStep();
		}
	}
	
	public void displayNextStep() {
		try {
			VirtualWorldDistributionInterface distrib;
			if (this.endOfTrack()) {
				this.dispNum = this.buffer.size()-1;
				this.tick = Integer.MAX_VALUE;
			}
			distrib = this.buffer.get(this.dispNum);
			if (monitor == null) 
				monitor = new VirtualWorldMonitor(distrib, this);
			else 
				monitor.setDistribution(distrib);
			monitor.drawCurrentStep(this.dispNum);
			
			monitor.showVirtualWorld();
			if (!this.byStep) {
				this.dispNum++;
//				if (this.stopping) {
//					this.byStep = true;
//					this.stopping = false;
//				}
				synchronized(this) {
					this.unpaused = true;
					this.wait(this.tick);
					this.unpaused = false;
				}
			}
		} catch (Exception e) {
			if (e instanceof IOException) {
				this.eof = true;
			} else {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public boolean hasNext () {
		return !this.eof;
	}
	
	private boolean endOfTrack() {
		return this.buffer.isFileLoaded() && this.dispNum == this.buffer.size();
	}
}
