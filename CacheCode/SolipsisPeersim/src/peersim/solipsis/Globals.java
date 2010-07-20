package peersim.solipsis;


public class Globals {
	
	public static VirtualWorldDistribution distribution;
	public static VirtualWorldMonitor monitor;
	public static VirtualWorldRecorder recorder;
	public static StatisticsGatherer evaluator;
	public static CacheStatistics cacheEvaluator;
	
	public static long mapSize;
	public static int zoneNb;
	public static int outOfZoneNb;
	public static long zoneSize;
	public static long smallZoneSize;
	public static int smallZoneNb;
	public static int tSpeed;
	public static int wSpeed;
	public static int smallWorldLinkNb;
	
	public final static int screenSize = 768;
	public final static boolean debug = false;
	public final static boolean drawTopology = false;
	public final static boolean HQRendering = false;//!drawTopology;
	public final static boolean realTime = false;
	public final static int offset = 11;
	public final static int law = VirtualWorldDistribution.ZIPF;
	public final static boolean cacheStat = true;
	
	public static int traceType = peersim.tracePlayer.TraceReader.EXTENDEDSOLIPSIS;
	public static long[][] zoneCoords;
	
	public static int steps;
	public static boolean quiet;
	public static boolean topologyIsReady;
	
	public static int haltedToHalted;
	public static int haltedToTravelling;
	public static int haltedToWandering;
	
	public static int travellingToTravelling;
	public static int travellingToHalted;
	public static int travellingToWandering;
	
	public static int wanderingToWandering;
	public static int wanderingToTravelling;
	public static int wanderingToHalted;
	public static int changeWanderingDirection;
	
//	public static int envelope = 0;
	public static int hops = 0;
	public static int lookupsAchieved = 0;
	public static int lookupsPending  = 0;
	public static int sqLookups       = 0;
	public static int longRangeUse = 0;
	public static int totalUse     = 0;
	
	public static int affichage_options = 0;
	
	public static SecondLifeTraceDistribution slTrace;
	public static boolean generated = true;
	public static int stepCount;
	public static int debugCounter;
	
}
