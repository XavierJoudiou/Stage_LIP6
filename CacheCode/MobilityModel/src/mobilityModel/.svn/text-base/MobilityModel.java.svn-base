package mobilityModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Main file of the mobility generator.
 * @author Sergey Legtchenko
 */
public class MobilityModel {
	
	//name of the configuration file
	private String configFilename;
	//a set containing all the avatars
	private Distribution map;
	//print coordinates of all avatars on each step?
	private boolean print;
	
	/**
	 * 
	 * @param configFile the path of the configuration file
	 * @param print if set to true, prints the whole simulation on the standard output.
	 * @throws IOException if the file has not been found
	 */
	public MobilityModel(String configFile, boolean print) throws IOException {
		this.configFilename = configFile;
		this.print = print;
		
		//fetching model values from the config file
		try {
			this.initializeGlobals();
		} catch (PropertyException e) {
			e.printStackTrace();
		}
		
		if (this.print) {
			System.out.print(Globals.mapSize + "*");
		}
	}
	
	/**
	 * 
	 * @return the size of the map edge
	 */
	public long getMapSize() {
		return Globals.mapSize;
	}
	
	/**
	 * ALL avatars on the map execute their next step if the simulation end time has not been reached.
	 * Otherwise a EndOfTraceException is thrown
	 * @throws EndOfTraceException if the simulation endtime has been reached
	 */
	public void executeNextStep() throws EndOfTraceException {
		String step;
		if (! Globals.clock.timeEnded()) {
			step = map.animate();
			if (this.print) {
				System.out.print(step);
			}
		} else {
			throw new EndOfTraceException();
		}
	}
	
	/**
	 * ALL avatars on the map are animated until the end of the simulation.
	 */
	public void animate() throws EndOfTraceException {
		while(true) {
			this.executeNextStep();
		}
	}
	
	/**
	 * get all avatars
	 * @return a map containing all the avatars
	 */
	public HashMap<Integer,Avatar> getAvatars() {
		return this.map.getAvatars();
	}
	
	/**
	 * get a particular avatar 
	 * @param identifier id of the avatar to get
	 * @return the Avatar object corresponding to the id
	 */
	public Avatar getAvatar(int identifier) {
		return this.map.get(identifier);
	}
	
	/**
	 * returns the position of a particular avatar
	 * @param identifier id of the avatar
	 * @return coordinates on the map
	 */
	public long[] getPosition(int identifier) {
		return this.map.get(identifier).getCurrentPosition();
	}
	
	/**
	 * fetches the configuration file
	 * @throws IOException if the file has not been found
	 * @throws PropertyException if a propery is missing
	 */
	private void initializeGlobals() throws IOException, PropertyException {
		Properties props;
		String tickValue, 
			   mapSize, 
			   seed, 
			   zoneSize, 
			   zoneNb, 
			   law, 
			   tspeed, 
			   wspeed, 
			   acceleration, 
			   avatarNb, 
			   outOfZone,
			   endTime;
		
		Globals.configFilename = this.configFilename;
		props = new Properties();
		this.load(props, this.configFilename);
		
		avatarNb = props.getProperty("avatarNb");
		if (avatarNb == null) {
			throw new PropertyException("Please specify the number of avatars");
		}
		tickValue = props.getProperty("tickInterval");
		if (tickValue == null) {
			throw new PropertyException("Please specify the clock step");
		}
		mapSize = props.getProperty("mapSize");
		if (mapSize == null) {
			throw new PropertyException("Please specify the map size");
		}
		seed = props.getProperty("randomSeed");

		zoneSize = props.getProperty("densityZoneSize");
		if (zoneSize == null) {
			throw new PropertyException("Please specify the size of a density zone");
		}
		zoneNb = props.getProperty("densityZoneNb");
		if (zoneNb == null) {
			throw new PropertyException("Please specify the number of density zones");
		}
		law = props.getProperty("zoneDistributionLaw");
		if (law == null) {
			throw new PropertyException("Please specify the inside-zone-avatar-distribution law");
		}
		tspeed = props.getProperty("maxTravelSpeed");
		if (tspeed == null) {
			throw new PropertyException("Please specify the maximum travel speed");
		}
		wspeed = props.getProperty("maxExploreSpeed");
		if (wspeed == null) {
			throw new PropertyException("Please specify the maximum explore speed");
		}
		acceleration = props.getProperty("acceleration");
		if (acceleration == null) {
			throw new PropertyException("Please specify the acceleration");
		}
		outOfZone = props.getProperty("avatarsInDesertZones");
		if (outOfZone == null) {
			throw new PropertyException("Please specify the number of avatars in desert zones");
		}
		endTime = props.getProperty("simulationEndtime");
		if (endTime == null) {
			throw new PropertyException("Please specify the simulation end time");
		}
		
		Globals.avatarNb	   			  = Integer.parseInt(avatarNb);
		Globals.clock          			  = new Clock(Long.parseLong(tickValue), Long.parseLong(endTime));
		Globals.mapSize        			  = Long.parseLong(mapSize);		
		Globals.zoneSize       			  = Long.parseLong(zoneSize);
		Globals.zoneNb        			  = Integer.parseInt(zoneNb);
		Globals.outOfZoneNb   			  = Integer.parseInt(outOfZone);
		Globals.maxTravelSpeed			  = Integer.parseInt(tspeed);
		Globals.maxWanderSpeed 			  = Integer.parseInt(wspeed);
		Globals.acceleration   			  = Integer.parseInt(acceleration);
		Globals.stateMachineProbabilities = new TransitionProbabilities(props);
		if (seed == null) {
			Globals.r = new EnhancedRandom();
		} else {
			Globals.r = new EnhancedRandom(Long.parseLong(seed));
		}

		if (law.compareTo("Gaussian") == 0) {
			Globals.law = Distribution.GAUSSIAN;
		} else {
			Globals.law = Distribution.ZIPF;
		}
		
		map = new Distribution(Distribution.SCEWED_DISTRIBUTION, 
				Globals.avatarNb, 
				Globals.zoneNb, 
				Globals.outOfZoneNb, 
				Globals.mapSize, 
				Globals.zoneSize);
	}
	
	/**
	 * loading the properties
	 * @param props a Properties object to put the config values in
	 * @param fileName name of the config file
	 * @throws IOException
	 */
	private void load(Properties props, String fileName) throws IOException {

		FileInputStream fis = new FileInputStream(fileName);
		props.load( fis );
		fis.close();
	}
}
