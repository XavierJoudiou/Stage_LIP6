package mobilityModel;

/**
 * This class implements a global time for the mobility model
 * @author Sergey Legtchenko
 *
 */
public class Clock {
	private long globalTime; // current time
	private long tickInterval;//time interval between two ticks 
	private long endTime; // end time
	
	Clock(long tickInterval, long endTime) {
		this.tickInterval = tickInterval;
		this.globalTime = 0;
		this.endTime = endTime;
	}
	
	/**
	 * executes next tick
	 */
	public void tick() {
		if (this.globalTime < this.endTime) {
			this.globalTime += this.tickInterval;
		}
	}
	
	/**
	 * checks if the time has exceeded the simulation endtime
	 * @return true if time is over
	 */
	public boolean timeEnded() {
		return this.globalTime >= this.endTime;
	}
	
	public long getTime() {
		return this.globalTime;
	}
	
	/**
	 * resets the clock
	 */
	public void reset() {
		this.globalTime = 0;
	}
}
