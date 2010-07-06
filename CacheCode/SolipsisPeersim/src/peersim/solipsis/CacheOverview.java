/**
 * 
 */
package peersim.solipsis;

import peersim.core.Control;
import peersim.solipsis.Globals;
import peersim.solipsis.VirtualWorldMonitor;

/**
 * @author xavier
 *
 */
public class CacheOverview implements Control {

	private String prefix;
	private int percent;
	
	public CacheOverview(String prefix) {
		this.prefix = prefix;
		this.percent = 0;
	}
	
    public boolean execute() {
    	VirtualWorldMonitor monitor;
    	VirtualWorldRecorder recorder;
    	CacheStatistics eval;
    	percent++;
    		if (Globals.cacheStat) {
    			System.err.println("Stepp " + percent + " done.");
    			recorder = Globals.recorder;
    			recorder.record();
    		} else {
    			eval = Globals.cacheEvaluator;
    			eval.printStatistics();
    		}
    	return false;
    }
}
