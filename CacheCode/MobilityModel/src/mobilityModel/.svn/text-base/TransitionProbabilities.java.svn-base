package mobilityModel;

import java.util.Properties;

/**
 * A container class containing all the probabilities of the state machine edges
 * @author Sergey Legtchenko
 */
public class TransitionProbabilities {
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

	/**
	 * Initializes the container with the transition probability values.
	 * The name of the values is formed to indicate the origin and the destination states.
	 * name=(originState)2(destinationState). Example h2h= Halted to Halted state transition.
	 * @param h2h
	 * @param h2t
	 * @param h2w
	 * @param t2t
	 * @param t2h
	 * @param t2w
	 * @param w2w
	 * @param w2t
	 * @param w2h
	 * @param cwd
	 */
	TransitionProbabilities(int h2h, int h2t, int h2w, int t2t, int t2h, int t2w, int w2w, int w2t, int w2h, int cwd) {
		this.setHaltedToHalted(h2h);
		this.setHaltedToTravelling(h2t);
		this.setHaltedToWandering(h2w);
		
		this.setTravellingToTravelling(t2t);
		this.setTravellingToHalted(t2h);
		this.setTravellingToWandering(t2w);
		
		this.setWanderingToWandering(w2w);
		this.setWanderingToTravelling(w2t);
		this.setWanderingToHalted(w2h);
		this.setChangeWanderingDirection(cwd);
	}

	/**
	 * loads transition probabilities from a java.util.Properties object
	 * @param config the Property object
	 * @throws PropertyException if a transition probability is missing
	 */
	public TransitionProbabilities(Properties config) throws PropertyException {
		String proba;
		
		proba = config.getProperty("h2h");
		if (proba == null) {
			throw new PropertyException("Missing h2h probability value in configuration file");
		}
		this.setHaltedToHalted(Integer.parseInt(proba));
		proba = config.getProperty("h2t");
		if (proba == null) {
			throw new PropertyException("Missing h2t probability value in configuration file");
		}
		this.setHaltedToTravelling(Integer.parseInt(proba));
		proba = config.getProperty("h2w");
		if (proba == null) {
			throw new PropertyException("Missing h2w probability value in configuration file");
		}
		this.setHaltedToWandering(Integer.parseInt(proba));
		proba = config.getProperty("t2t");
		if (proba == null) {
			throw new PropertyException("Missing t2t probability value in configuration file");
		}
		this.setTravellingToTravelling(Integer.parseInt(proba));
		proba = config.getProperty("t2w");
		if (proba == null) {
			throw new PropertyException("Missing t2w probability value in configuration file");
		}
		this.setTravellingToWandering(Integer.parseInt(proba));
		proba = config.getProperty("t2h");
		if (proba == null) {
			throw new PropertyException("Missing t2h probability value in configuration file");
		}
		this.setTravellingToHalted(Integer.parseInt(proba));
		proba = config.getProperty("w2w");
		if (proba == null) {
			throw new PropertyException("Missing w2w probability value in configuration file");
		}
		this.setWanderingToWandering(Integer.parseInt(proba));
		proba = config.getProperty("w2t");
		if (proba == null) {
			throw new PropertyException("Missing w2t probability value in configuration file");
		}
		this.setWanderingToTravelling(Integer.parseInt(proba));
		proba = config.getProperty("w2h");
		if (proba == null) {
			throw new PropertyException("Missing w2h probability value in configuration file");
		}
		this.setWanderingToHalted(Integer.parseInt(proba));
		proba = config.getProperty("cwd");
		if (proba == null) {
			throw new PropertyException("Missing cwd probability value in configuration file");
		}
		this.setChangeWanderingDirection(Integer.parseInt(proba));
	}

	public void setHaltedToHalted(int haltedToHalted) {
		this.haltedToHalted = haltedToHalted;
	}

	public int getHaltedToHalted() {
		return haltedToHalted;
	}

	public void setHaltedToTravelling(int haltedToTravelling) {
		this.haltedToTravelling = haltedToTravelling;
	}

	public int getHaltedToTravelling() {
		return haltedToTravelling;
	}

	public void setHaltedToWandering(int haltedToWandering) {
		this.haltedToWandering = haltedToWandering;
	}

	public int getHaltedToWandering() {
		return haltedToWandering;
	}

	public void setTravellingToTravelling(int travellingToTravelling) {
		this.travellingToTravelling = travellingToTravelling;
	}

	public int getTravellingToTravelling() {
		return travellingToTravelling;
	}

	public void setTravellingToHalted(int travellingToHalted) {
		this.travellingToHalted = travellingToHalted;
	}

	public int getTravellingToHalted() {
		return travellingToHalted;
	}

	public void setTravellingToWandering(int travellingToWandering) {
		this.travellingToWandering = travellingToWandering;
	}

	public int getTravellingToWandering() {
		return travellingToWandering;
	}

	public void setWanderingToWandering(int wanderingToWandering) {
		this.wanderingToWandering = wanderingToWandering;
	}

	public int getWanderingToWandering() {
		return wanderingToWandering;
	}

	public void setWanderingToTravelling(int wanderingToTravelling) {
		this.wanderingToTravelling = wanderingToTravelling;
	}

	public int getWanderingToTravelling() {
		return wanderingToTravelling;
	}

	public void setWanderingToHalted(int wanderingToHalted) {
		this.wanderingToHalted = wanderingToHalted;
	}

	public int getWanderingToHalted() {
		return wanderingToHalted;
	}

	public void setChangeWanderingDirection(int changeWanderingDirection) {
		this.changeWanderingDirection = changeWanderingDirection;
	}

	public int getChangeWanderingDirection() {
		return changeWanderingDirection;
	}
}
