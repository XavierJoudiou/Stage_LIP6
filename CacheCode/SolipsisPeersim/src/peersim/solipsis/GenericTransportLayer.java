package peersim.solipsis;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.solipsis.Globals;

/**
 * Implement a transport layer that reliably delivers messages with a random
 * delay, that is drawn from the configured interval according to the uniform
 * distribution.
 * According to the size of the message, it calculates the transfer delay added
 * to the latency in order to simulate bandwidth
 * @author Alberto Montresor and Sergey Legtchenko
 * @version $Revision: 1.13 $
 */
public class GenericTransportLayer implements Protocol {

	//---------------------------------------------------------------------
	//Parameters
	//---------------------------------------------------------------------
	
	/** 
	 * String name of the parameter used to configure the probability that a 
	 * message sent through this transport is lost.
	 * @config
	 */
	private static final String PAR_DROP = "drop";

	/** Probability of dropping messages */
	private final float loss;

	/** 
	 * String name of the parameter used to configure the minimum latency.
	 * @config
	 */	
	private static final String PAR_MINDELAY = "mindelay";	

	/** 
	 * String name of the parameter used to configure the maximum latency.
	 * Defaults to {@value #PAR_MINDELAY}, which results in a constant delay.
	 * @config 
	 */	
	private static final String PAR_MAXDELAY = "maxdelay";

	//---------------------------------------------------------------------
	//Fields
	//---------------------------------------------------------------------

	/** Minimum delay for message sending */
	private final long min;

	/** Difference between the max and min delay plus one. That is, max delay is
	 * min+range-1.
	 */
	private final long range;


	//---------------------------------------------------------------------
	//Initialization
	//---------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public GenericTransportLayer(String prefix)
	{
		System.err.println("Transport Layer Enabled");
		min = Configuration.getLong(prefix + "." + PAR_MINDELAY);
		loss = (float) Configuration.getDouble(prefix+"."+PAR_DROP);
		long max = Configuration.getLong(prefix + "." + PAR_MAXDELAY,min);
		if (max < min) 
			throw new IllegalParameterException(prefix+"."+PAR_MAXDELAY, 
			"The maximum latency cannot be smaller than the minimum latency");
		range = max-min+1;
	}

	//---------------------------------------------------------------------

	/**
	 * Retuns <code>this</code>. This way only one instance exists in the system
	 * that is linked from all the nodes. This is because this protocol has no
	 * node specific state.
	 */
	@Override
	public Object clone()
	{
		return this;
	}

	//---------------------------------------------------------------------
	//Methods
	//---------------------------------------------------------------------

	/**
	 * Delivers the message with a random
	 * delay, that is drawn from the configured interval according to the uniform
	 * distribution.
	 */
	public void send(Node src, Node dest, Object msg, int pid)
	{
		
		if (CommonState.r.nextFloat() >= loss)
		{
			if (!Globals.realTime) {
				Globals.evaluator.messageSent();
			}
			// avoid calling nextLong if possible
			long delay = (range==1?min:min + CommonState.r.nextLong(range));
			EDSimulator.add(delay, msg, dest, pid);
		}


	}
	
	public void send(Node src, Node dest, Object msg, int pid, long delay)
	{
		
		if (CommonState.r.nextFloat() >= loss)
		{
			if (!Globals.realTime) {
				Globals.evaluator.messageSent();
			}
			// avoid calling nextLong if possible
//			long delay = (range==1?min:min + CommonState.r.nextLong(range));
			EDSimulator.add(delay, msg, dest, pid);
		}


	}
	
	public long getMaxLatency() {
		return min+range;
	}
	
	public long estimateRTT() {
		return (min + min+range-1)/2;
	}

	/**
	 * Returns a random
	 * delay, that is drawn from the configured interval according to the uniform
	 * distribution.
	 */
	public long getLatency(Node src, Node dest)
	{
		return (range==1?min:min + CommonState.r.nextLong(range));
	}
	
	public long getLatency() {
		return (range==1?min:min + CommonState.r.nextLong(range));		
	}


}

