package peersim.solipsis;

import java.util.Random;


import peersim.solipsis.Globals;
import peersim.solipsis.SolipsisProtocol;
import peersim.solipsis.VirtualEntity;
import peersim.solipsis.VirtualWorld;
import peersim.core.Network;

public class MobilityStateMachine {

	public final static int HALTED     = 0;
	public final static int TRAVELLING = 1;
	public final static int WANDERING  = 2;
	
	private long interestZone;
	
	private int tSpeed;
	private int wSpeed;
	
	private int state;
	private Random dice;
	private VirtualEntity entity;
	
	private long [][] constraints;
	
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
	
	/*
	 * Proportion of desert zone population to join the hotspots
	 * on each step (in percent)
	 */
	private final static int BALANCECONSTANT = 75;
	
	MobilityStateMachine(VirtualEntity entity) {
		this.state = HALTED;
		this.dice  = new Random();
		this.entity = entity;
		this.entity.setStateUpdateTimer(this.state);
		this.constraints = new long[3][2];
		this.interestZone = Globals.zoneSize / 2;
		this.tSpeed = Globals.tSpeed;
		this.wSpeed = Globals.wSpeed;
		this.haltedToHalted = Globals.haltedToHalted;
		this.haltedToTravelling = Globals.haltedToTravelling;
		this.haltedToWandering = Globals.haltedToWandering;
		this.travellingToTravelling = Globals.travellingToTravelling;
		this.travellingToHalted = Globals.travellingToHalted;
		this.travellingToWandering = Globals.travellingToWandering;
		this.wanderingToHalted = Globals.wanderingToHalted;
		this.wanderingToTravelling = Globals.wanderingToTravelling;
		this.wanderingToWandering = Globals.wanderingToWandering;
		this.changeWanderingDirection = Globals.changeWanderingDirection;
	}
	
	private int throwDice() {
		return dice.nextInt(10000);
	}
	
	private void startWandering(long [] zoneOfInterest, long zoneSize) {
		this.state = WANDERING;
		this.constraints[0][0] = zoneOfInterest[0];
		this.constraints[0][1] = zoneOfInterest[0] + zoneSize;
		this.constraints[1][0] = zoneOfInterest[1];
		this.constraints[1][1] = zoneOfInterest[1] + zoneSize;
		this.entity.setStateUpdateTimer(this.state);
		long [] wanderingDestination = VirtualWorld.getConstraintedGaussianLocation(constraints);
		entity.newDestination(wanderingDestination);
	}
	
	private void continueWanderingTransition() {
		/*nothing to do, just continue*/
	}
	
	private void stayStillTransition() {
		/*nothing to do, just continue to stand*/	
	}
	
	private void changeWanderingDirectionTransition() {
		entity.setMaxSpeed(wSpeed);
//		long [] wanderingDestination = VirtualWorld.normalize(VirtualWorld.getConstraintedGaussianLocation(constraints));
		long [] wanderingDestination = VirtualWorld.getConstraintedGaussianLocation(constraints);
		entity.newDestination(wanderingDestination);	
	}
	
//	private void changeZoneTransition() {
//		int zoneNb = Globals.distribution.getZoneNb();
//		int zoneNum = dice.nextInt(zoneNb + 1);
//		entity.setMaxSpeed(tSpeed);
//		if (zoneNum < zoneNb) {
//			startWandering(Globals.distribution.getZoneLocations()[zoneNum], Globals.distribution.getZoneSize());
//		} else {
//			startWandering(entity.getCoord(),this.interestZone);
//		}
//	}
	
	private void haltTransition() {
		SolipsisProtocol protocol;
		
		protocol = this.entity.getProtocol();
		if (protocol.getType() == SolipsisProtocol.SMALLWORLD) {
			if (this.state == MobilityStateMachine.TRAVELLING) {
				protocol.deactivateWiring();
			}
		}
		this.state = HALTED;
		entity.setMaxSpeed(0);
		this.entity.setStateUpdateTimer(this.state);
	}
	
	/**
	 * start travelling
	 */
	private void travelTransition() {
		this.state = TRAVELLING;
		long [] travelDestination;
		//choose a random hotspot
		int zoneNb = Globals.zoneNb;
		int zoneNum = dice.nextInt(100);
		SolipsisProtocol protocol;
		
		protocol = entity.getProtocol();
		if (protocol.getType() == SolipsisProtocol.SMALLWORLD) {
			protocol.activateWiring();
		}
		//setting the travel speed
		entity.setMaxSpeed(tSpeed);
		//with a certain probability, move to the hotspot, otherwise just go in a random place on the map
		if (zoneNum < this.changeStatusProbability()) {
			zoneNum = dice.nextInt(zoneNb);
			travelDestination = Globals.zoneCoords[zoneNum];
			switch(Globals.law) {
			case VirtualWorldDistribution.ZIPF:
				travelDestination = VirtualWorld.getConstraintedZipfLocation(VirtualWorld.setConstraints(travelDestination,Globals.zoneSize));
				break;
			case VirtualWorldDistribution.GAUSSIAN:
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
		int urbanisationCoefficient = Network.size() / Globals.outOfZoneNb;
		int probability;
		
		if (this.isCitizen()) {
			probability = 100 - (BALANCECONSTANT / urbanisationCoefficient);
		} else {
			probability = BALANCECONSTANT;
		}
		return probability;
	}
//	private void travelTransition() {
//		this.state = TRAVELLING;
//		long [] travelDestination;
//		int zoneNb = Globals.distribution.getZoneNb();
//		int zoneNum = dice.nextInt(100);
//		entity.setMaxSpeed(tSpeed);
//		this.entity.setStateUpdateTimer(this.state);
//		if (zoneNum < 85) {
//			zoneNum = dice.nextInt(zoneNb);
//			travelDestination = Globals.distribution.getZoneLocations()[zoneNum];
//			travelDestination = VirtualWorld.getConstraintedGaussianLocation(VirtualWorld.setConstraints(travelDestination,Globals.zoneSize));
//		} else {
//			travelDestination = VirtualWorld.getSimpleConstraintedRandomLocation(Globals.mapSize);
//
//		}
//		entity.newDestination(travelDestination);
//	}
	
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
	
	private void lookAround() {
		SolipsisProtocol protocol;
		
		protocol = this.entity.getProtocol();
		if (protocol.getType() == SolipsisProtocol.SMALLWORLD) {
			if (this.state == MobilityStateMachine.TRAVELLING) {
				protocol.deactivateWiring();
			}
		}
		startWandering(entity.getCoord(), this.interestZone);
	}
	
	private boolean farFromArrival() {
		double distance = VirtualWorld.simpleDistance(entity.getCoord(), entity.subjectiveCoord(entity.getDestination()));
		long distPerPixel = Globals.mapSize/Globals.screenSize;
		return distance > (Globals.screenSize/20)*distPerPixel;
	}
	public int getState() {
		return this.state;
	}
	
	public void bootstrapStateMachine(long [] zoneOfInterest, long zoneSize) {
		this.constraints = VirtualWorld.setConstraints(zoneOfInterest, zoneSize);
	}
	
	public void nextStep() {
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

	public int getKeepTravellingProbability() {
		return this.travellingToTravelling;
	}
	
}
