package peersim.solipsis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import peersim.core.CommonState;
import peersim.core.Control;
import peersim.solipsis.Globals;
import peersim.solipsis.VirtualWorldMonitor;

public class VirtualWorldOverview implements Control {
	
	private String prefix;
	private int percent;
	private File fichier,fichier2,fichier3,fichier4,fichier5,fichier6;

	private FileWriter fw,fw2,fw3,fw4,fw5,fw6;
	
	private CacheStatistiquesStruct cacheStats;
	private CacheStatistiquesStruct inter;

	private CacheFichierTraitements traitements;

	
	public VirtualWorldOverview(String prefix) throws IOException {
		this.prefix = prefix;
		this.percent = 0;
		
		fichier = new File("Résultats_CacheMess.txt");
	    fichier.createNewFile();
	    fw = new FileWriter("Résultats_CacheMess.txt",true);
	    
	    fichier2 = new File("Résultats_ActivitéMess.txt");
	    fichier2.createNewFile();
	    fw2 = new FileWriter("Résultats_ActivitéMess.txt",true);
	    
	    fichier3 = new File("Stats_EndTime.txt");
	    fichier3.createNewFile();
	    fw3 = new FileWriter("Stats_EndTime.txt",true);
	   
	    fichier4 = new File("Stats_CacheEnd.txt");
	    fichier4.createNewFile();
	    fw4 = new FileWriter("Stats_CacheEnd.txt",true);
	    
	    fichier5 = new File("Stats_sergey.txt");
	    fichier5.createNewFile();
	    fw5 = new FileWriter("Stats_sergey.txt",true);
	    
	    fichier6 = new File("Stats_Rules.txt");
	    fichier6.createNewFile();
	    fw6 = new FileWriter("Stats_Rules.txt",true);
	    

	    cacheStats = new CacheStatistiquesStruct(0,0,0,0,0);
	    inter = new CacheStatistiquesStruct(0,0,0,0,0);

	    traitements = new CacheFichierTraitements();
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
    				
    				eval = Globals.evaluator;
//    				eval.printStatistics();
            		evalCache = Globals.cacheEvaluator;
            		evalCache.printStatisticsCacheMess();
            		evalCache.printStatisticsActiviteMess();
        			System.out.println(":: " + Globals.countEnvelop + " ::");
            		System.out.println("+++++++++++++++++++++++++++++++++++");
		    		try {
		    		
		    			String res1,res2,res6;
		    			res1 = evalCache.printStatisticsCacheMessString();
		    			fw.write(res1);
		    			res2 = evalCache.printStatisticsActiviteMessString();
		    			fw2.write(res2);
		    			res6 = eval.printStatistics();
		    			fw5.write(res6);
		    			if (CommonState.getTime() > 15000){
		    				cacheStats.moy(eval.printStatisticsStruct());
		    			}	

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (CommonState.getTime() == CommonState.getEndTime()-100 ){
					try {
						fw.close();
						fw2.close();
						String res3;
						res3 = evalCache.printStatisticsCacheMessFinalString();
						fw3.write(res3);
						res3 = evalCache.printStatisticsActiviteMessFinalString();
						fw3.write(res3);
						fw3.write("=============================\n");
						fw3.close();
						res3=evalCache.printStatisticsActiviteMessString();
						fw4.write(res3);
						res3=evalCache.printStatisticsCacheMessString();
						fw4.write(res3);
						res3=cacheStats.PrintStats();
						fw4.write(res3);
						fw4.close();
						fw5.close();
						fw6.write(cacheStats.PrintStats());
						fw6.close();
						BufferedReader  in = new BufferedReader(new FileReader("Stats_Rules.txt")); 
						inter = traitements.TwoToOneLine(in);
						
						fichier6 = new File("Stats_Rules.txt");
					    fichier6.createNewFile();
					    fw6 = new FileWriter("Stats_Rules.txt",false);
						fw6.write(inter.PrintStatsFinal());
						fw6.close();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
    		}
    		
    	}
    	return false;
    }
}
