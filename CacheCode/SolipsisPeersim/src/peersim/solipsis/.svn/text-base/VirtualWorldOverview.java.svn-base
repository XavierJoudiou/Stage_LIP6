package peersim.solipsis;

import peersim.core.Control;
import peersim.solipsis.Globals;
import peersim.solipsis.VirtualWorldMonitor;

public class VirtualWorldOverview implements Control {
	
	private String prefix;
	private int percent;
	
	public VirtualWorldOverview(String prefix) {
		this.prefix = prefix;
		this.percent = 0;
	}
	
    public boolean execute() {
    	VirtualWorldMonitor monitor;
    	VirtualWorldRecorder recorder;
    	StatisticsGatherer eval;
    	percent++;
    	if (Globals.topologyIsReady) {
    		if (Globals.realTime) {
//    			monitor = Globals.monitor;
 //   			monitor.showVirtualWorld();
    			System.err.println("Step " + percent + " done.");
    			recorder = Globals.recorder;
    			recorder.record();
    		} else {
    			eval = Globals.evaluator;
    			eval.printStatistics();
    		}
    	}
    	return false;
    }
}
