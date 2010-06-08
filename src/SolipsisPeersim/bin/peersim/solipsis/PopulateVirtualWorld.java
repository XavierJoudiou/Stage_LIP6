package peersim.solipsis;

import javax.swing.*;

import java.io.IOException;
import java.util.Iterator;

import peersim.config.*;
import peersim.core.*;
import peersim.solipsis.SolipsisProtocol;
import peersim.solipsis.VirtualEntity;
import peersim.tracePlayer.VirtualEntityShell;
import peersim.tracePlayer.VirtualWorldDistributionShell;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PopulateVirtualWorld implements peersim.core.Control {
	
	private static final String PAR_DISTRIB = "distribution";
	private static final String APPLICATIVE_LAYER = "applicative";
	private static final String MAP_SIZE = "mapSize";
	private static final String ZONE_SIZE = "zoneSize";
	private static final String ZONE_NB = "zoneNb";
	private static final String OUT_OF_ZONE_NB = "outOfZoneNb";
	private static final String WSPEED = "wanderingSpeed";
	private static final String TSPEED = "travellingSpeed";
	private static final String SMALL_ZONE_NB = "smallZoneNb";
	private static final String SMALL_ZONE_SIZE = "smallZoneSize";
	private static final String QUIET = "quiet";
	
	private static final String HTH = "hthTransitionProbability";
	private static final String HTT = "httTransitionProbability";
	private static final String HTW = "htwTransitionProbability";
	private static final String TTT = "tttTransitionProbability";
	private static final String TTW = "ttwTransitionProbability";
	private static final String TTH = "tthTransitionProbability";
	private static final String WTH = "wthTransitionProbability";
	private static final String WTW = "wtwTransitionProbability"; 
	private static final String WTT = "wttTransitionProbability";
	private static final String CWD = "cwdTransitionProbability";
	private static final int SYNTHETIC = 0;
	private static final int SECONDLIFE = 1;
	
	private int distribution;
	private int applicativeLayerId;
	private int type;
	private SecondLifeTraceDistribution trace;
	
	public PopulateVirtualWorld(String prefix) {
		distribution = Configuration.getInt(prefix + "." + PAR_DISTRIB);
		applicativeLayerId = Configuration.getPid(prefix + "." + APPLICATIVE_LAYER);
		Globals.mapSize = Configuration.getLong(prefix + "." + MAP_SIZE);
		Globals.zoneSize = Configuration.getLong(prefix + "." + ZONE_SIZE);
		Globals.zoneNb = Configuration.getInt(prefix + "." + ZONE_NB);
		Globals.outOfZoneNb = Configuration.getInt(prefix + "." + OUT_OF_ZONE_NB);
		Globals.wSpeed = Configuration.getInt(prefix + "." + WSPEED);
		Globals.tSpeed = Configuration.getInt(prefix + "." + TSPEED);
		Globals.smallZoneSize = Configuration.getInt(prefix + "." + SMALL_ZONE_SIZE);
		Globals.smallZoneNb = Configuration.getInt(prefix + "." + SMALL_ZONE_NB);
		
		Globals.haltedToHalted = Configuration.getInt(prefix + "." + HTH);
		Globals.haltedToTravelling = Configuration.getInt(prefix + "." + HTT);
		Globals.haltedToWandering = Configuration.getInt(prefix + "." + HTW);
		
		Globals.travellingToTravelling = Configuration.getInt(prefix + "." + TTT);
		Globals.travellingToHalted = Configuration.getInt(prefix + "." + TTH);
		Globals.travellingToWandering = Configuration.getInt(prefix + "." + TTW);
		
		Globals.wanderingToWandering = Configuration.getInt(prefix + "." + WTW);
		Globals.wanderingToTravelling = Configuration.getInt(prefix + "." + WTT);
		Globals.wanderingToHalted = Configuration.getInt(prefix + "." + WTH);
		Globals.changeWanderingDirection = Configuration.getInt(prefix + "." + CWD);
		
		Globals.quiet = Configuration.getBoolean(prefix + "." + QUIET);
		
	}
	
	public boolean execute() {
		if (Globals.generated) {
			this.executeSynthetic();
		} else {
			this.executeSecondLife();
		}

		return false;
	}
	
    private void executeSecondLife() {
    	VirtualEntity entity;
    	VirtualEntityShell shell;
    	VirtualWorldDistributionShell firstStep;
    	Iterator it;
    	SolipsisProtocol solipsis;
    	int i, toPercentCoef, percent, userNb;
    	VirtualWorldDistribution vwd;
    	
    	Globals.mapSize = 25600;
    	try {
			Globals.slTrace = new SecondLifeTraceDistribution ("data/SecondLifeRecord2.txt");
			this.trace = Globals.slTrace;
		} catch (IOException e) {
			System.err.println("File error");
			System.exit(1);
		}

    	vwd = new VirtualWorldDistribution();
    	Globals.distribution = vwd;
    	if (this.trace.getTrace().isEmpty()) {
    		System.err.println("The file is corrupted");
    		System.exit(1);
    	}
        System.err.println("Joining nodes...");
        userNb = this.trace.getTrace().get(0).countAvatars();
        percent = 0;
    	firstStep = this.trace.getTrace().get(0);
    	it = firstStep.getDistribution().entrySet().iterator();
    	userNb = firstStep.countAvatars();
    	toPercentCoef = (userNb>100)?userNb / 100:1;
    	i = 0;
    	Globals.stepCount = 1;
		while (it.hasNext()) {
    		shell = (VirtualEntityShell)((Map.Entry)it.next()).getValue();
    		entity = new VirtualEntity(shell.getCoord());
    		solipsis = (SolipsisProtocol)Network.get(i).getProtocol(applicativeLayerId);
    		solipsis.setPeersimNodeId(i);
    		solipsis.setVirtualEntity(entity);
    		entity.setProtocol(solipsis);
    		entity.setOrder(i+1);
    		entity.setId(shell.getId());
    		vwd.addToDistribution(entity);
    		if(i>0) {
    			solipsis.join((SolipsisProtocol)Network.get(i-1).getProtocol(applicativeLayerId));
    		}
    		i++;
    		if (i % toPercentCoef == 0) System.err.println(++percent+"%");
    	}
		Network.setCapacity(userNb);
    	System.err.println("Done.");
    	System.err.println("Normalizing Solipsis topology...");
    	percent = 0;
    	it = vwd.getDistribution().entrySet().iterator();
    	for (i = 0; i< userNb; i++) {
    		entity = (VirtualEntity)((Map.Entry)it.next()).getValue();
//        	System.out.println("peer "+i+": "+entity.getNeighbors().size()); 
    		entity.getProtocol().finalizeKnowledgeZone();
//        	System.out.println("peer "+i+": "+entity.getNeighbors().size()); 
    		entity.getProtocol().simplifyTopology();
//        	System.out.println("peer "+i+": "+entity.getNeighbors().size()); 
    		if (i % (3*toPercentCoef) == 0) System.err.println(++percent+"%");
    	}

    	it = vwd.getDistribution().entrySet().iterator();
    	for (i = 0; i< userNb; i++) {
    		entity = (VirtualEntity)((Map.Entry)it.next()).getValue();
    		entity.refreshState();
    		if (i % (3*toPercentCoef) == 0) System.err.println(++percent+"%");
    	}

    	it = vwd.getDistribution().entrySet().iterator();
    	for (i = 0; i< userNb; i++) {
    		entity = (VirtualEntity)((Map.Entry)it.next()).getValue();
    		entity.getProtocol().maintainKnowledgeZone();
    		entity.getProtocol().finalizeKnowledgeZone();
    		entity.getProtocol().removeUnwantedNeighbors();
//        	System.out.println("peer "+i+": "+entity.getNeighbors().size()); 
    		if (i % (3*toPercentCoef) == 0) System.err.println(++percent+"%");
    	}

    	System.err.println("Done.");
//    	for (int i = 0; i < 15; i++) {
//    		System.out.println("cycle...");
//    		Globals.distribution.animate();
//
//    	}
		Globals.topologyIsReady = true;
		System.err.println("Populated");
//		System.err.println("Collisions:" + this.collisionCount());
//		System.exit(2);
    	if (Globals.realTime) {
//    		VirtualWorldMonitor map = new VirtualWorldMonitor(vwd);
//    		Globals.monitor = map;
//        	map.showVirtualWorld();
    		Globals.recorder = new VirtualWorldRecorder(); 
    	} else {
    		Globals.evaluator = new StatisticsGatherer();
    	}
		
	}

	private void executeSynthetic() {
    	
    	int toPercentCoef, percent = 0;
    	int userNb = Network.size();
    	HashMap<Integer,VirtualEntity> coords;
    	SolipsisProtocol solipsis;
    	VirtualEntity entity;
    		System.err.println("Populating Virtual World with " + userNb + " participants...");
    	VirtualWorldDistribution vwd = 
    		new VirtualWorldDistribution(this.distribution, userNb, Globals.zoneNb, Globals.outOfZoneNb, Globals.mapSize, Globals.zoneSize, Globals.smallZoneSize, Globals.smallZoneNb, this.applicativeLayerId);
    	Globals.distribution = vwd;
    	coords = vwd.getDistribution();
    	Iterator it = coords.entrySet().iterator();
    	toPercentCoef = userNb / 100;
        System.err.println("Joining nodes...");
    	for (int i = 0; i < userNb; i++) {
    		entity = (VirtualEntity)((Map.Entry)it.next()).getValue();
    		solipsis = (SolipsisProtocol)Network.get(i).getProtocol(applicativeLayerId);
    		solipsis.setVirtualEntity(entity);
    		solipsis.setPeersimNodeId(i);
    		entity.setProtocol(solipsis);
    		entity.setOrder(i+1);
    		if(i>0) {
    			solipsis.join((SolipsisProtocol)Network.get(i-1).getProtocol(applicativeLayerId));
    		}
    		if (i % toPercentCoef == 0) System.err.println(++percent+"%");
//        	System.out.println("peer "+i+": "+solipsis.getVirtualEntity().getNeighbors().size()); 
    	}
    	System.err.println("Done.");
//    	System.out.println("uci2");
    	System.err.println("Normalizing Solipsis topology...");
    	percent = 0;
    	it = coords.entrySet().iterator();
    	for (int i = 0; i< userNb; i++) {
    		entity = (VirtualEntity)((Map.Entry)it.next()).getValue();
//        	System.out.println("peer "+i+": "+entity.getNeighbors().size()); 
    		entity.getProtocol().finalizeKnowledgeZone();
//        	System.out.println("peer "+i+": "+entity.getNeighbors().size()); 
    		entity.getProtocol().simplifyTopology();
//        	System.out.println("peer "+i+": "+entity.getNeighbors().size()); 
    		if (i % (3*toPercentCoef) == 0) System.err.println(++percent+"%");
    	}

    	it = coords.entrySet().iterator();
    	for (int i = 0; i< userNb; i++) {
    		entity = (VirtualEntity)((Map.Entry)it.next()).getValue();
    		entity.refreshState();
    		if (i % (3*toPercentCoef) == 0) System.err.println(++percent+"%");
    	}

    	it = coords.entrySet().iterator();
    	for (int i = 0; i< userNb; i++) {
    		entity = (VirtualEntity)((Map.Entry)it.next()).getValue();
    		entity.getProtocol().maintainKnowledgeZone();
    		entity.getProtocol().finalizeKnowledgeZone();
    		entity.getProtocol().removeUnwantedNeighbors();
//        	System.out.println("peer "+i+": "+entity.getNeighbors().size()); 
    		if (i % (3*toPercentCoef) == 0) System.err.println(++percent+"%");
    	}

    	System.out.println("Done.");
//    	for (int i = 0; i < 15; i++) {
//    		System.out.println("cycle...");
//    		Globals.distribution.animate();
//
//    	}
		Globals.topologyIsReady = true;
		System.err.println("Populated");
//		System.err.println("Collisions:" + this.collisionCount());
//		System.exit(2);
    	if (Globals.realTime) {
//    		VirtualWorldMonitor map = new VirtualWorldMonitor(vwd);
//    		Globals.monitor = map;
//        	map.showVirtualWorld();
    		Globals.recorder = new VirtualWorldRecorder(); 
    	} else {
    		Globals.evaluator = new StatisticsGatherer();
    	}

	} 
    
    private int collisionCount() {
    	HashMap<Integer,VirtualEntity> coords;
    	VirtualEntity current1, current2;
    	coords = Globals.distribution.getDistribution();
    	Iterator it1, it2 = coords.entrySet().iterator();
    	int counter = 0;
    	
    	while (it2.hasNext()) {
    		it1 = coords.entrySet().iterator();
    		current2 = (VirtualEntity)((Map.Entry)it2.next()).getValue();
    		while (it1.hasNext()) {
    			current1 = (VirtualEntity)((Map.Entry)it1.next()).getValue();
    			if (current1 != current2 && VirtualWorld.samePosition(current2.getCoord(), current1.getCoord())) {
    				counter++;
    			}
    		}
    	}
    	
    	return counter;
    }
	
}
