package peersim.solipsis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import peersim.core.CommonState;
import peersim.core.Control;
import peersim.solipsis.Globals;
import peersim.solipsis.VirtualWorldMonitor;

public class VirtualWorldOverview implements Control {
	
	private String prefix;
	private int percent;
	private File fichier;
	private FileWriter fw;
	
	public VirtualWorldOverview(String prefix) throws IOException {
		this.prefix = prefix;
		this.percent = 0;
		
		fichier = new File("RES.txt");
	    fichier.createNewFile();
	    fw = new FileWriter("RES.txt",true);
		
	}
	
    public boolean execute() {
    	VirtualWorldMonitor monitor;
    	VirtualWorldRecorder recorder;
    	StatisticsGatherer eval;
		CacheStatistics evalCache;
    	percent++;
    	if (Globals.topologyIsReady) {
    		if (Globals.realTime) {
//    			monitor = Globals.monitor;
 //   			monitor.showVirtualWorld();
    			System.err.println("Step " + percent + " done.");
    			recorder = Globals.recorder;
    			recorder.record();
    		} else {
    			if (Globals.cacheStat) {
//        			System.out.println(this + " cache stat");
            		evalCache = Globals.cacheEvaluator;
            		evalCache.printStatistics();
            		evalCache.printNbMessages();
            		evalCache.printnbEnvelopNotOK();
		    		try {;
		    		
		    			String res;
		    			res = evalCache.printStatistics2();
		    			fw.write(res);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (CommonState.getTime() == CommonState.getEndTime() ){
//					System.err.println("Valeur Finale: ");
//					evalCache.printStatistics();
					try {
						fw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
    		}
    		
    	}
    	return false;
    }
}
