package mobilityModel;

/**
 * A class that implements the behavioral state machine of the avatars
 * @author Sergey Legtchenko
 *
 */
public class MobilityStateMachine {

	public final static int HALTED     = 0;
	public final static int TRAVELLING = 1;
	public final static int WANDERING  = 2;
	
	/*
	 * Proportion of desert zone population to join the hotspots
	 * on each step (in percent)
	 */
	private final static int BALANCECONSTANT = 75;
	private int tSpeed;
	private int wSpeed;
	
	/*
	 * the current state
	 */
	private int state;
	
	/*
	 * the avatar which uses this state machine
	 */
	private Avatar entity;
	
	/*
	 * current hotspot coordinates
	 */
	private long [][] constraints;
	
	/*
	 * transition probabilities of the state machine
	 */
	private int haltedToHalted;
	private int haltedToTravelling;
	private int haltedToWandering;
	
	private int travellingToTravelling;
	private int travellingToHalted;
	private int travellingToWandering;
	
	private int wanderingToWandering;
	private int wanderingToTravelling;
	private int wanderingToHalted;
	private int changeWanderingDirection;
	
	
	MobilityStateMachine(Avatar entity, TransitionProbabilities probas, int travelSpeed, int wanderSpeed) {
		this.state = HALTED;
		this.entity = entity;
		this.constraints = new long[3][2];
		this.tSpeed = travelSpeed;
		this.wSpeed = wanderSpeed;
		//loading the probabilities on the transition edges.
		this.haltedToHalted = probas.getHaltedToHalted();
		this.haltedToTravelling = probas.getHaltedToTravelling();
		this.haltedToWandering = probas.getHaltedToWandering();
		this.travellingToTravelling = probas.getTravellingToTravelling();
		this.travellingToHalted = probas.getTravellingToHalted();
		this.travellingToWandering = probas.getTravellingToWandering();
		this.wanderingToHalted = probas.getWanderingToHalted();
		this.wanderingToTravelling = probas.getWanderingToTravelling();
		this.wanderingToWandering = probas.getWanderingToWandering();
		this.changeWanderingDirection = probas.getChangeWanderingDirection();
	}
	
	/**
	 * @return a random integer value between 0 and 10000
	 */
	private int throwDice() {
		return Globals.r.nextInt(10000);
	}
	
	/**
	 * starting a slow chaotic movement in the zoneOfInterest
	 * @param zoneOfInterest the coordinates of the zone
	 * @param zoneSize the size of the zone
	 */
	private void startWandering(long [] zoneOfInterest, long zoneSize) {
		this.state = WANDERING;
		this.constraints[0][0] = zoneOfInterest[0];
		this.constraints[0][1] = zoneOfInterest[0] + zoneSize;
		this.constraints[1][0] = zoneOfInterest[1];
		this.constraints[1][1] = zoneOfInterest[1] + zoneSize;
		long [] wanderingDestination = VirtualWorld.getConstraintedZipfLocation(constraints);
		switch(Globals.law) {
		case Distribution.ZIPF:
			wanderingDestination = VirtualWorld.getConstraintedZipfLocation(constraints);
			break;
		case Distribution.GAUSSIAN:
			wanderingDestination = VirtualWorld.getConstraintedGaussianLocation(constraints);
			break;
		default:
			break;
		}
		entity.newDestination(wanderingDestination);
	}
	
	private void continueWanderingTransition() {
		/*nothing to do, just continue to explore without changing direction*/
	}
	
	private void stayStillTransition() {
		/*nothing to do, just continue to stand*/	
	}
	
	/**
	 * change direction during the chaotic movement.
	 */
	private void changeWanderingDirectionTransition() {
		entity.setMaxSpeed(wSpeed);
//		long [] wanderingDestination = VirtualWorld.normalize(VirtualWorld.getConstraintedGaussianLocation(constraints));			
		long [] wanderingDestination = null;
		switch(Globals.law) {
		case Distribution.ZIPF:
			wanderingDestination = VirtualWorld.getConstraintedZipfLocation(constraints);
			break;
		case Distribution.GAUSSIAN:
			wanderingDestination = VirtualWorld.getConstraintedGaussianLocation(constraints);
			break;
		default:
			break;
		}
		entity.newDestination(wanderingDestination);	
	}
	
	/**
	 * the avatar suddenly stops
	 */
	private void haltTransition() {
		this.state = HALTED;
		entity.setMaxSpeed(0);
	}
	
	/**
	 * start travelling
	 */
	private void travelTransition() {
		this.state = TRAVELLING;
		long [] travelDestination;
		//choose a random hotspot
		int zoneNb = Globals.zoneNb;
		int zoneNum = Globals.r.nextInt(100);
		
		//setting the travel speed
		entity.setMaxSpeed(tSpeed);
		//with a certain probability, move to the hotspot, otherwise just go in a random place on the map
		if (zoneNum < this.changeStatusProbability()) {
			zoneNum = Globals.r.nextInt(zoneNb);
			travelDestination = Globals.zoneCoords[zoneNum];
			switch(Globals.law) {
			case Distribution.ZIPF:
				travelDestination = VirtualWorld.getConstraintedZipfLocation(VirtualWorld.setConstraints(travelDestination,Globals.zoneSize));
				break;
			case Distribution.GAUSSIAN:
				travelDestination = VirtualWorld.getConstraintedGaussianLocation(VirtualWorld.setConstraints(travelDestination,Globals.zoneSize));
				break;
			default:
				break;
			}
		} else {
			travelDestination = VirtualWorld.getSimpleConstraintedRandomLocation(Globals.mapSize);

		}
		entity.newDestination(travelDestination);
	}
	
	/**
	 * each step, the hotspot population should stay constant despite the avatar migration
	 * therefore, the proportion of the population of desert zones to join the hotspots must be
	 * larger than the proportion of the hotspot population leaving the hotspots (because population of hotspots
	 * is far greater than desert zone population.
	 * 
	 * @return a probability to leave the current zone (hotspot or desert), according to the current avatar status (hotspot member of desert zone member)
	 */
	private int changeStatusProbability() {
		//the urbanisation coefficient: proportion of desert population compared to total population
		int urbanisationCoefficient = Globals.avatarNb / Globals.outOfZoneNb;
		int probability;
		
		if (this.isCitizen()) {
			probability = 100 - (BALANCECONSTANT / urbanisationCoefficient);
		} else {
			probability = BALANCECONSTANT;
		}
		return probability;
	}
	
	/**
	 * determines whether the current avatar belongs to desert zone population or hotspot population.
	 * @return true if the current avatar belongs to a hotspot
	 */
	private boolean isCitizen() {
		long[] avatarCoord;
		
		avatarCoord = this.entity.getDestination();
		
		return this.isInsideHotspot(avatarCoord);
	}
	
	/**
	 * determines if the given coordinates are located inside one of the hotspots
	 * @param coord the coordinates to check
	 * @return true if coord is inside one of the hotspots
	 */
	private boolean isInsideHotspot(long[] coord) {
		long[][] zoneCoords;
		long zoneSize;
		boolean answer;
		
		zoneCoords = Globals.zoneCoords;
		zoneSize   = Globals.zoneSize;
		answer     = false;
		
		for (int i = 0; i < Globals.zoneCoords.length; i++) {
			if (zoneCoords[i][0] <= coord[0] && coord[0] <= zoneCoords[i][0] + zoneSize) {
				if (zoneCoords[i][1] <= coord[1] && coord[1] <= zoneCoords[i][1] + zoneSize) {
					answer = true;
					break;
				}
			}
		}
		
		return answer;
	}
	
	private void continueTravelTransition() {
		/*nothing to do, just continue*/
	}
	
	/**
	 * starting a chaotic movement outside any hotspot: just explore a zone around the avatar
	 */
	private void lookAround() {
		startWandering(entity.getCurrentPosition(), Globals.zoneSize);
	}
	
	/**
	 * true if the current distance of the avatar to its destination exceeds a fixed distance
	 * @return boolean value
	 */
	private boolean farFromArrival() {
		long distance = VirtualWorld.simpleDistance(entity.getCurrentPosition(), entity.subjectiveCoord(entity.getDestination()));
		return distance > Globals.mapSize/20;
	}
	public int getState() {
		return this.state;
	}
	
	/**
	 * setting the initial wandering zone coordinates
	 * @param zoneOfInterest coordinates of the interest zones
	 * @param zoneSize size of the zone
	 */
	public void bootstrapStateMachine(long [] zoneOfInterest, long zoneSize) {
		this.constraints = VirtualWorld.setConstraints(zoneOfInterest, zoneSize);
	}
	
	/**
	 * evaluates the state machine according to the probabilities.
	 * A new state is determined by evaluating transition probabilities.
	 */
	public void nextStep() {
		//int zoneNb;
		//int zoneNum;
		int randomNumber = this.throwDice();
		int limitOne, limitTwo, limitThree;
		switch(this.state) {
	
		case HALTED:
			limitOne = haltedToHalted + haltedToWandering;
			limitTwo = limitOne + haltedToTravelling;
			if (0  <= randomNumber && randomNumber < haltedToHalted) stayStillTransition();
			else if (haltedToHalted <= randomNumber && randomNumber < limitOne) lookAround();			
			else if (limitOne <= randomNumber && randomNumber <= limitTwo) travelTransition();
			break;
			
		case WANDERING:
			limitOne = wanderingToWandering + changeWanderingDirection;
			limitTwo = limitOne + wanderingToHalted;
			limitThree = limitTwo + wanderingToTravelling;
			if (0  <= randomNumber && randomNumber <  wanderingToWandering) continueWanderingTransition();
			else if (wanderingToWandering <= randomNumber && randomNumber <   limitOne) changeWanderingDirectionTransition();
			else if (limitOne <= randomNumber && randomNumber <   limitTwo) haltTransition();
			else if (limitTwo <= randomNumber && randomNumber <   limitThree) travelTransition();
			break;			
			
		case TRAVELLING:
			limitOne = travellingToTravelling + travellingToWandering;
			limitTwo = limitOne + travellingToHalted;
			if(farFromArrival()) {
				if (0  <= randomNumber && randomNumber < travellingToTravelling) continueTravelTransition();
				else if (travellingToTravelling <= randomNumber && randomNumber <   limitOne) lookAround();
				else if (limitOne <= randomNumber && randomNumber <= limitTwo) haltTransition();
			} else {
				lookAround();
			}
			break;
			
		default:
			System.out.println("Warning: Not a valid state");
			break;
		}
	}
	
}
