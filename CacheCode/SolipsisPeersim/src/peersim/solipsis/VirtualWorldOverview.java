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
	private File fichier,fichier2;
	private FileWriter fw,fw2;
	
	public VirtualWorldOverview(String prefix) throws IOException {
		this.prefix = prefix;
		this.percent = 0;
		
		fichier = new File("Résultats_CacheMess.txt");
	    fichier.createNewFile();
	    fw = new FileWriter("Résultats_CacheMess.txt",true);
	    
	    fichier2 = new File("Résultats_ActivitéMess.txt");
	    fichier2.createNewFile();
	    fw2 = new FileWriter("Résultats_ActivitéMess.txt",true);
		
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
            		evalCache = Globals.cacheEvaluator;
            		evalCache.printStatisticsCacheMess();
            		evalCache.printStatisticsActiviteMess();
            		System.out.println("+++++++++++++++++++++++++++++++++++");
		    		try {;
		    		
		    			String res1,res2;
		    			res1 = evalCache.printStatisticsCacheMessString();
		    			res2 = evalCache.printStatisticsActiviteMessString();
		    			fw.write(res1);
		    			fw2.write(res2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (CommonState.getTime() == CommonState.getEndTime() ){
					try {
						fw.close();
						fw2.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
    		}
    		
    	}
    	return false;
    }
}
