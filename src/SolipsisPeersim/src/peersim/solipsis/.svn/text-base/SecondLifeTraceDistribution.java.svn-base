package peersim.solipsis;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import peersim.tracePlayer.FillNeighborsThread;
import peersim.tracePlayer.StepBuffer;
import peersim.tracePlayer.VirtualEntityShell;
import peersim.tracePlayer.VirtualWorldDistributionShell;

public class SecondLifeTraceDistribution {
	
	private String filename;
	private FileInputStream file;
	private int mapSize;
	private String lastTimestamp;
	private LinkedList<VirtualWorldDistributionShell> trace;

	SecondLifeTraceDistribution (String filename) throws IOException {
		this.filename = filename;
		this.file = new FileInputStream(this.filename);
		System.out.println("File opened.");
		this.mapSize = 256*100; 
		this.lastTimestamp = null;
		this.trace = new LinkedList<VirtualWorldDistributionShell>();
		this.loadAllSteps();
	}

	public LinkedList<VirtualWorldDistributionShell> getTrace() {
		return this.trace;
	}
	
	public void loadAllSteps() {
		int count;
		
		count = 0;
		try {
			while(true) {
				System.err.println("Loading step " + count);
				this.trace.add(readSecondLifeTraceStep());
				count++;
			}
		} catch (Exception e) {
			if (e instanceof IOException) {
//				if (display!=null) {
//					this.display.fileLoaded();
//					this.display.showProgression(this.stepsRead);
//				}
//				System.out.println(e.getMessage());
				return;
			} else {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	private VirtualWorldDistributionShell readSecondLifeTraceStep() throws IOException, InterruptedException {
		char c;
		int readValue;
		String time;
		String coord0;
		String coord1;
		String id;

		int itime = 0;
		double dcoord0 = 0;
		double dcoord1 = 0;
		int iid = 0;
		int idlong;

		int counter;
		boolean changed;
		boolean currentStep = true;
		int stepTime = -1;
		long[] coord;
		VirtualWorldDistributionShell distribution = new VirtualWorldDistributionShell(this.mapSize);

		while(currentStep) {
			changed = false;
			coord0 = "";
			coord1 = "";
			id = "";
			time = "";
			counter = 0;
			c = ' ';

//			file.mark(64);
//			System.out.println("loop:");
			while(c!='\n') {
				readValue = file.read();
				if (readValue == -1) {
					throw new IOException("End of file");
				}
				c = (char)readValue;
				if (this.lastTimestamp != null) {
					counter++;
					changed = true;
				} else {
					if (c == ' ' || c == '\n') {
						counter++;
						changed = true;
					}
				}
				if (!changed) {
					switch(counter) {
					case 0:
						time += c;
						break;
					case 1:
						id += c;
						break;
					case 2:
						coord0 += c;
						break;
					case 3:
						coord1 += c;
						break;	
					default:
						break;
					}
				} else {
					changed = false;
					switch(counter) {
					case 1:
						if (this.lastTimestamp != null) {
							time = this.lastTimestamp;
							this.lastTimestamp = null;
							id += c;
						}
						itime = Integer.parseInt(time);
						if (stepTime == -1) {
							stepTime = itime;
						} else {
							if (stepTime != itime) {
								System.err.println(stepTime+" "+itime);
								currentStep = false;
								this.lastTimestamp = time;
								c = '\n';
							}
						}
						break;
					case 2:
						iid = Integer.parseInt(id);
						break;
					case 3:
						dcoord0 = Double.parseDouble(coord0)*100;
						break;
					case 4:
						dcoord1 = Double.parseDouble(coord1)*100;
						break;						
					default:
						break;
					}
				}
			}
			if (currentStep) {
				coord = new long[3];
				coord[0] = new Double(dcoord0).longValue();
				coord[1] = new Double(dcoord1).longValue();
				idlong = new Integer(iid).intValue();
				VirtualEntityShell shell = new VirtualEntityShell(idlong,coord);
				distribution.addToDistribution((VirtualEntityInterface)shell);
			}
		}
//		Iterator it = distribution.getDistribution().entrySet().iterator();
//    	while (it.hasNext()) {
//    		VirtualEntityInterface current = (VirtualEntityInterface)((Map.Entry)it.next()).getValue();
//    		System.err.println(current.getId()+" "+current.getCoord()[0]+","+current.getCoord()[1]);
//    	}
//    	Globals.debugCounter++;
//    	if (Globals.debugCounter==3)System.exit(1);
		return distribution;
	}
	
}
