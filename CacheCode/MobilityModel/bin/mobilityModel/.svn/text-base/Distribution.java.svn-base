package mobilityModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is basically a HashMap of avatars with method to correctly manipulate it
 * @author Legtchenko Sergey
 */
public class Distribution {
	
	public static final int RANDOM_DISTRIBUTION = 0;
	public static final int SCEWED_DISTRIBUTION = 1;
	public static final int CUSTOM_DISTRIBUTION = 2;
	
	public static final int GAUSSIAN = 0;
	public static final int ZIPF     = 1;
	
	private int usrNb;
	private HashMap <Integer,Avatar> coords;
	private int zoneNb;
	private int outOfZoneNb;
	private long mapSize;
	private long zoneSize;
	private long [][] zoneCoords;

	
	Distribution() {
		
		this.usrNb 		         = 0;
		this.coords              = new HashMap<Integer,Avatar>();
		this.zoneNb              = 0;
		this.zoneCoords          = new long[this.zoneNb][2];
		this.outOfZoneNb         = 0;
		this.mapSize             = 0;
		this.zoneSize            = 0;
	}
	
	Distribution(int distributionKind, int userNb, int zoneNb, int outOfZoneNb, long mapSize, long zoneSize) {
		
		this.usrNb 		         = userNb;
		this.coords              = new HashMap<Integer,Avatar>();
		this.zoneNb              = zoneNb;
		this.outOfZoneNb         = outOfZoneNb;
		this.mapSize    		 = mapSize;
		this.zoneSize            = zoneSize;
		this.zoneCoords          = new long[this.zoneNb][3];
		this.makeDistribution(distributionKind);
	}
	
	Distribution(int userNb, long mapSize) {
		this(Distribution.RANDOM_DISTRIBUTION, userNb, 0, 0, mapSize, 0);
	}
	
	/**
	 * computes the initial distribution of avatars
	 * @param distributionKind set to 0 to have uniformly distributed avatars and to 1 to have hotspots 
	 */
	private void makeDistribution(int distributionKind) {
		
		int zoneId;
		long zoneBorder = this.mapSize - this.zoneSize;
		int zoneInhabitants = this.usrNb - this.outOfZoneNb;
		Avatar avatar;
		long [][] constraints = new long[3][2];
		long [] position;
		TransitionProbabilities probas;
		
		probas = Globals.stateMachineProbabilities;
		
		switch(distributionKind) {
		
			case Distribution.SCEWED_DISTRIBUTION:
				/*assigning zone coordinates*/
				for(int i = 0; i < this.zoneNb; i++) {
					zoneCoords[i][0] = Globals.r.nextLong(zoneBorder);
					zoneCoords[i][1] = Globals.r.nextLong(zoneBorder);
					zoneCoords[i][2] = 0;
				}
				Globals.zoneCoords = zoneCoords;
				if(zoneInhabitants < 0) {
					System.err.println("Error: Incompatible parameters in VirtualWorldDistribution");
					System.exit(1);
				}
				
				/* populating high density zones*/
				for (int i = 0; i < zoneInhabitants; i++) {
					zoneId = Globals.r.nextInt(this.zoneNb); //choose the zone to put the new user in.
					constraints[0][0] = zoneCoords[zoneId][0];
					constraints[0][1] = zoneCoords[zoneId][0] + this.zoneSize;
					constraints[1][0] = zoneCoords[zoneId][1];
					constraints[1][1] = zoneCoords[zoneId][1] + this.zoneSize;
					switch(Globals.law) {
					case Distribution.GAUSSIAN:
						position = VirtualWorld.getConstraintedGaussianLocation(constraints);
						break;
					case Distribution.ZIPF:
						position = VirtualWorld.getConstraintedZipfLocation(constraints);
						break;
					default:
						position = null;
						break;
					}
					avatar = new Avatar(position, probas, Globals.maxTravelSpeed, Globals.maxWanderSpeed, Globals.acceleration);
					avatar.activate(zoneCoords[zoneId], this.zoneSize); 
					coords.put(avatar.getId(), avatar);
				}
				
				/* populating low density zones*/

				for (int i = 0; i < this.outOfZoneNb; i++) {
					avatar = new Avatar(VirtualWorld.getSimpleConstraintedRandomLocation(this.mapSize), probas, Globals.maxTravelSpeed, Globals.maxWanderSpeed, Globals.acceleration);
					avatar.activate(); 
					coords.put(avatar.getId(), avatar);	 				
				}
				break;
				
			case Distribution.RANDOM_DISTRIBUTION:
				for (int i = 0; i < this.usrNb; i++) {
					avatar = new Avatar(VirtualWorld.getSimpleConstraintedRandomLocation(this.mapSize), probas, Globals.maxTravelSpeed, Globals.maxWanderSpeed, Globals.acceleration);
					coords.put(avatar.getId(), avatar);
				}
				break;			
		}
	}
	
	/**
	 * 
	 * @param id the id of the avatar to find
	 * @return the avatar corresponding to the given id or null if none is found
	 */
	public Avatar get(int id) {
		return this.coords.get(id);
	}
	
	/**
	 * executes the next step of all avatars in the HashMap.
	 * @return if the print parameter is false, returns a string containing all the coordinates for the current step, the void string otherwise
	 */
	@SuppressWarnings("unchecked")
	public String animate() {
		Iterator it = coords.entrySet().iterator();
		Avatar entity = null;
		String step = "";
		while(it.hasNext()) {
			entity = (Avatar) ((Map.Entry)it.next()).getValue();
			entity.proceedMovement();
			step += Globals.clock.getTime() + " " + entity.getId() + " " + entity.getCurrentPosition()[0] + " " + entity.getCurrentPosition()[1] + "\n";

		}
		Globals.clock.tick();
		return step;
	}
	
	/**
	 * 
	 * @return the coordinates of the upper left corners of all hotspots
	 */
	public long[][] getZoneLocations() {
		return this.zoneCoords;
	}
	
	/**
	 * 
	 * @return the size of the hotspots (they have all the same size)
	 */
	public long getZoneSize() {
		return this.zoneSize;
	}
	
	public int getUserNb() {
		return this.usrNb;
	}
	
	public int getZoneNb() {
		return this.zoneNb;
	}
	
	public int countAvatars() {
		return this.coords.size();
	}
	
	/**
	 * @return the HashMap containing the Avatars
	 */
	public HashMap<Integer, Avatar> getAvatars() {
		return this.coords;
	}
}
