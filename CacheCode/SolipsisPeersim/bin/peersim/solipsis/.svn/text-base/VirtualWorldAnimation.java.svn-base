package peersim.solipsis;

import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.solipsis.Globals;
import peersim.edsim.EDSimulator;

import java.lang.*;
import java.util.Calendar;

public class VirtualWorldAnimation implements Control {
	
	private String prefix;
	private int measuring;
	private long virtualTime;
	private long realTime;
	private int trigger;
	private double mean;
	private int stepNum;
	
	public VirtualWorldAnimation(String prefix) {
		this.prefix = prefix;
		this.measuring = 0;
		this.trigger = 0;
		this.mean = 0;
		this.stepNum = 0;
	}
	
    public boolean execute() {

    	if (Globals.realTime) {
    		if (this.measuring == 1) {
    			long virtualDelta = CommonState.getTime() - this.virtualTime;
    			long realDelta = (Calendar.getInstance().getTimeInMillis() - this.realTime) / 1000;
    			this.mean += (new Double(virtualDelta)/new Double(realDelta)); //to have it in seconds
    			trigger++;
    		}else if (this.measuring == 0) {
    			this.virtualTime = CommonState.getTime();
    			this.realTime = Calendar.getInstance().getTimeInMillis();
    			this.measuring++;
    		}
    		if (this.trigger == 10) {
    			System.out.println("Simulation speed: x"+ (this.mean/trigger)+" (step "+this.stepNum+")");
    			trigger = 0;
    			this.mean = 0;
    		}
    	}

    	if (Globals.topologyIsReady)
    		Globals.distribution.animate();
    	this.stepNum ++;
    	return false;
    }
}
