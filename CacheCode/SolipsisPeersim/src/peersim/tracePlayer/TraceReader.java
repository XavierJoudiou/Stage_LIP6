package peersim.tracePlayer;

import java.io.FileInputStream;
import peersim.solipsis.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.IOException;
import java.lang.InterruptedException;

public class TraceReader {
	
	public static final int EXTENDEDSOLIPSIS  = 0;
	public static final int SECONDLIFE        = 1;
	public static final int GENERATED         = 2;
	
	private FileInputStream file;
	private String filename;
	private long mapSize;
	private int stepsRead;
	private StepBuffer buffer;
	private FillNeighborsThread thirdThread;
	private TraceDisplayer display;
	
	private String lastTimestamp;
	
	TraceReader() throws IOException {
		this("defaultRecord.txt", new StepBuffer());
	}
	
	TraceReader(String filename, StepBuffer buffer) throws IOException {
		this.filename = filename;
		this.stepsRead = 0;
		this.buffer = buffer;
		this.thirdThread = new FillNeighborsThread(buffer);
		this.thirdThread.start();
		this.file = new FileInputStream(this.filename);
		System.out.println("File opened.");
		switch (Globals.traceType) {
		case SECONDLIFE:
			this.mapSize = 256*10; 
			this.display = null;
			
			this.lastTimestamp = null;
			break;
		default:
			this.mapSize = this.readMapSize();
			this.display = null;
			this.lastTimestamp = null;
			break;
		}
	}
	
	public StepBuffer getBuffer() {
		return this.buffer;
	}
	
	public int getStepsNumber() {
		return this.stepsRead;
	}
	
	public void setDisplay(TraceDisplayer display) {
		this.display = display;
	}
	
	public void loadAllSteps() {
		try {
			while(true) {
				if(display != null)
					display.showProgression(this.stepsRead);
//				System.out.println("Pre-Loading step "+ this.stepsRead + "...");
				readNextStep();
			} 
		} catch (Exception e) {
			if (e instanceof IOException) {
//				if (display!=null) {
//					this.display.fileLoaded();
//					this.display.showProgression(this.stepsRead);
//				}
				return;
			} else {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
//	public VirtualWorldDistributionShell getStep(int stepNum) {
//		VirtualWorldDistributionShell step = null;
//		
//		if (stepNum < this.stepsRead) {
//			step = this.steps.get(stepNum);
//		}
//		
//		return step;
//	}
	
	public void finish() {
		this.thirdThread.finish();
	}
	
	private void readGeneratedTrace() throws IOException, InterruptedException {
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
					this.thirdThread.finish();
					throw new IOException();
				}
				if (this.lastTimestamp != null) {
					counter++;
					changed = true;
				} else {
					c = (char)readValue;
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
						}
						itime = Integer.parseInt(time);
						if (stepTime == -1) {
							stepTime = itime;
						} else {
							if (stepTime != itime) {
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
						dcoord0 = Double.parseDouble(coord0);
						break;
					case 4:
						dcoord1 = Double.parseDouble(coord1);
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
				distribution.addToDistribution(shell);
			}
		}
		this.thirdThread.organize(distribution);	
		this.stepsRead++;
	}
	
	private void readSecondLifeTrace() throws IOException, InterruptedException {
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
					this.thirdThread.finish();
					throw new IOException();
				}
				if (this.lastTimestamp != null) {
					counter++;
					changed = true;
				} else {
					c = (char)readValue;
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
						}
						itime = Integer.parseInt(time);
						if (stepTime == -1) {
							stepTime = itime;
						} else {
							if (stepTime != itime) {
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
						dcoord0 = Double.parseDouble(coord0)*10;
						break;
					case 4:
						dcoord1 = Double.parseDouble(coord1)*10;
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
				distribution.addToDistribution(shell);
			}
		}
		this.thirdThread.organize(distribution);	
		this.stepsRead++;
	}
	
	private void readSyntheticTrace() throws IOException, InterruptedException {
		String coord0;
		String coord1;
		String radius;
		String id;
		String neighborId;
		String modulo;
		int count;
		char c;
		int readValue;
		boolean sw;
		VirtualEntityShell entity;
		LinkedList<Integer> neighbors;
		HashMap<Integer,Integer> qualities;
		boolean currentStep = true;
		long[] coord, precCoord;
		HashMap<Long,VirtualEntityInterface> distrib;
		LinkedList<VirtualEntityInterface> neighborEntities;
		int nId,idlong;
		boolean stabilized = false;
		VirtualWorldDistributionShell distribution = new VirtualWorldDistributionShell(this.mapSize);
		
		while(currentStep) {
			radius = "";
			id = "";
			coord0 = "";
			coord1 = "";
			neighborId = "";
			modulo = "";
			count = 0;
			neighbors = new LinkedList<Integer>();	
			qualities = new HashMap<Integer,Integer>();
			sw = false;
			c = 'e';
			while(c!='\n') {
				readValue = file.read();
				if (readValue == -1) {
					this.thirdThread.finish();
					throw new IOException();
				}
				c = (char)readValue;
				if (c == '*') {
					currentStep = false;
					break;
				} else if (c == ':') {
					count++;
					if (count > 5) 
						sw = true;
				} else {
					switch (count) {
					case 0:
						id += c;
						break;
					case 1:
						radius += c;
						break;
					case 2: 
						coord0 += c;
						break;
					case 3:
						coord1 += c;
						break;
					case 4: 
						stabilized = (c == '0')?false:true;
						break;
					default:
						if (sw == true) {
							nId = Integer.parseInt(neighborId);
							neighbors.add(nId);
							qualities.put(nId, Integer.parseInt(""+c));
							file.read();
							sw = false;
							neighborId = "";
						} else {
							neighborId += c;
						}
						break;
					}
				}
			}
			if (currentStep) {
				coord = new long[3];
				coord[0] = Long.parseLong(coord0);
				coord[1] = Long.parseLong(coord1);
				idlong = Integer.parseInt(id);
				VirtualEntityShell shell = new VirtualEntityShell(idlong,coord,neighbors,qualities,Double.parseDouble(radius),stabilized);
				precCoord = distribution.getFormerLocation(idlong);
				shell.setMovement(precCoord == null || precCoord[0] != coord[0] || precCoord[1] != coord[1]);
				distribution.addToDistribution(shell);
			}
		}
		this.thirdThread.organize(distribution);	
		this.stepsRead++;
	}
	
	public void readNextStep() throws IOException, InterruptedException {
		switch(Globals.traceType) {
		case EXTENDEDSOLIPSIS:
			this.readSyntheticTrace();
			break;
		case SECONDLIFE:
			this.readSecondLifeTrace();
			break;
		case GENERATED:
			this.readGeneratedTrace();
		default:
			break;
		}
		

	}
	
	private long readMapSize() throws IOException {
		char c;
		String mapSize = "";
		while((c=(char)this.file.read()) != -1) {
			if (c == '*') {
				break;
			}
			mapSize += c;
		}
		
		return Long.parseLong(mapSize);
	}
	
}