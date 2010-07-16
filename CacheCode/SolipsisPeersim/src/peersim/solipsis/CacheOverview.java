/**
 * 
 */
package peersim.solipsis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import peersim.core.CommonState;
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
	private File fichier;
	private FileWriter fw;
	
	public CacheOverview(String prefix) throws IOException {
		this.prefix = prefix;
		this.percent = 0;
		
		fichier = new File("/home/xavier/RES.txt");
	    fichier.createNewFile();
	    fw = new FileWriter("/home/xavier/RES.txt",true);
		
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
    			eval.printStatisticsCacheMess();
    			eval.printNbMessages();
    		}
    		if (CommonState.getTime() == CommonState.getEndTime() ){
    			System.out.println("Valeur Finale: ");
    			eval = Globals.cacheEvaluator;
    			eval.printNbMessages();
    			try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	return false;
    }
}
