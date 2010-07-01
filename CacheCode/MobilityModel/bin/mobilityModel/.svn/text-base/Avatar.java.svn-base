package mobilityModel;

/**
 * This class represents a single moving entity
 * @author Sergey Legtchenko
 */
public class Avatar {
	//unique identifier
	private int identifier;
	//speed that is never exceeded by the entity
	private int maxSpeed;
	//time of the last direction change
	private long startTime;
	//coordinates
	private long[] destination, currentPosition;
	//an avatar moves only if activated
	private boolean activated;
	
	private MobilityStateMachine brain;
	//current angular direction
	private double direction;
	//origin of the current movement
	private long[] originCoord;
	private int acceleration;
	
	Avatar(long[] position, TransitionProbabilities probas, int travelSpeed, int wanderSpeed, int acceleration) {
		this.brain = new MobilityStateMachine(this,probas,travelSpeed,wanderSpeed);
		this.identifier = this.generateId();
		this.currentPosition = position;
		this.destination = position;
		//avatar is not yet activated, the speed is not yet set
		this.maxSpeed = 0;
		this.activated = false;
		this.startTime = Globals.clock.getTime();
		this.acceleration = acceleration;
	}
	
	public void setMaxSpeed(int speed) {
		this.maxSpeed = speed;
	}
	
	/**
	 * called when the avatar changes its direction
	 * @param destination the new destination of the avatar
	 */
	public void newDestination(long[] destination) {
		this.setOrigin();
		this.destination = destination;
		this.direction = VirtualWorld.calculateDirection(this.originCoord, this.destination);
		this.startTime = Globals.clock.getTime();
	}
	
	private void setOrigin() {
		this.originCoord = this.currentPosition;
	}

	public long[] getDestination() {
		return this.destination;
	}
	
	/**
	 * a method to handle the boundlessness of the coordinates (with a modulo)
	 * @param coord the original coordinate (with values between 0 and Globals.mapSize)
	 * @return new coordinates without modulo (they may be > to Globals.mapSize)
	 */
	public long[] subjectiveCoord(long[] coord) {
		long[] local, subjective = null;
		
		local = this.currentPosition; 
		subjective = this.moduloModificationRule(local, coord);
		
		return subjective;
	}
	
	/**
	 * to bootstrap the automaton. the zone of interest is the current zone the avatar is exploring
	 * @param zoneOfInterest a square geometric region coordinate for avatar to choose its coordinates in.
	 * @param zoneSize the size of the geometric region
	 */
	public void activate(long [] zoneOfInterest, long zoneSize) {
		this.brain.bootstrapStateMachine(zoneOfInterest, zoneSize);
		this.activated = true;
	}
	
	/**
	 * bootstrapping the automaton with default parameters
	 */
	public void activate() {
		long[] zoneOfInterest;
		long zoneSize;

		zoneSize = Globals.zoneSize;
		zoneOfInterest = new long[3];
		zoneOfInterest[0] = this.currentPosition[0] - zoneSize / 2;
		zoneOfInterest[1] = this.currentPosition[1] - zoneSize / 2;
		
		this.activate(zoneOfInterest, zoneSize);
	}
	
	/**
	 * calculates the next step and then moves in that direction
	 */
	public void proceedMovement() {
		if (this.activated) {
			this.brain.nextStep();
			this.moveOn();
		}
	}
	
	public int getId() {
		return this.identifier;
	}

	public long[] getCurrentPosition() {
		return this.currentPosition;
	}
	
	/**
	 * calculates the new position from the old position and according to the current speed and the time delta between the two positions.
	 */
	private void moveOn() {
		long timeDelta = Globals.clock.getTime() - this.startTime;
		this.currentPosition = VirtualWorld.move(this.currentPosition, this.direction, this.acceleration, this.maxSpeed, timeDelta);
	}
	
	/**
	 * compensates the modulo of the coordinates and returns the compensated coordinates
	 * @param ref the origin of the modification
	 * @param distant the coordinates to modify
	 * @return the modified coordinates
	 */
	private long[] moduloModificationRule(long[] ref, long[] distant) {
		long[] subjective = new long[3];
		
		subjective[0] = distant[0];
		subjective[1] = distant[1];
		
		if (distant[0] > ref[0]) {
			if (distant[0] - ref[0] > Globals.mapSize / 2) {
				subjective[0] -= Globals.mapSize;
			} 
		} else {
			if (ref[0] - distant[0] > Globals.mapSize / 2) {
				subjective[0] += Globals.mapSize;
			}	
		}

		if (distant[1] > ref[1]) {
			if (distant[1] - ref[1] > Globals.mapSize / 2) {
				subjective[1] -= Globals.mapSize;
			}
		} else {
			if (ref[1] - distant[1] > Globals.mapSize / 2) {
				subjective[1] += Globals.mapSize;
			}	
		}
		
		return subjective;
	}
	
	private int generateId() {
		return Globals.r.nextInt();
	}

	
	
}
