package peersim.solipsis;

import peersim.core.GeneralNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import peersim.core.CommonState;
import peersim.core.Fallible;
import peersim.core.Network;
import peersim.core.Node;
import peersim.solipsis.Globals;
import peersim.solipsis.VirtualEntity;
import peersim.tracePlayer.VirtualEntityShell;
import peersim.tracePlayer.VirtualWorldDistributionShell;

/**
 * 
 * @author Legtchenko Sergey
 *
 */
public class VirtualWorldDistribution implements VirtualWorldDistributionInterface {
	
	public static final int randomDistribution = 0;
	public static final int scewedDistribution = 1;
	public static final int customDistribution = 2;
	
	public static final int GAUSSIAN = 0;
	public static final int ZIPF     = 1;
	
	public int distributionKind;
	private int usrNb;
	private HashMap <Integer,VirtualEntity> coords;
	private HashMap <Integer,long[]> savedForHQ;
	private int zoneNb;
	private int outOfZoneNb;
	private long mapSize;
	private long zoneSize;
	private long smallZoneSize;
	private int smallZoneNb;
	private long [][] zoneCoords;
	private long [][] smallZoneCoords;
	private boolean HQRendering;
	private int applicativeLayerId;

	
	VirtualWorldDistribution() {
		
		this.distributionKind    = VirtualWorldDistribution.customDistribution;
		this.usrNb 		         = 0;
		this.coords              = new HashMap<Integer,VirtualEntity>();
		this.savedForHQ			 = new HashMap<Integer,long[]>();
		this.zoneNb              = 0;
		this.zoneCoords          = new long[this.zoneNb][2];
		this.outOfZoneNb         = 0;
		this.mapSize             = Globals.mapSize;
		this.zoneSize            = 0;
		this.HQRendering 		 = false;
	}
	
	VirtualWorldDistribution(int distributionKind, int userNb, int zoneNb, int outOfZoneNb, long mapSize, long zoneSize, long smallZoneSize, int smallZoneNb, int layerId) {
		
		this.distributionKind    = distributionKind;
		this.usrNb 		         = userNb;
		this.coords              = new HashMap<Integer,VirtualEntity>();
		this.savedForHQ			 = new HashMap<Integer,long[]>();
		this.zoneNb              = zoneNb;
		this.outOfZoneNb         = outOfZoneNb;
		this.mapSize    		 = mapSize;
		this.zoneSize            = zoneSize;
		this.zoneCoords          = new long[this.zoneNb][3];
		this.HQRendering		 = false;
		this.smallZoneNb		 = smallZoneNb;
		this.smallZoneSize 		 = smallZoneSize;
		this.smallZoneCoords     = new long[this.smallZoneNb][3];
		this.applicativeLayerId  = layerId;
		this.makeDistribution(distributionKind);
	}
	
	VirtualWorldDistribution(int userNb, long mapSize, int layerId) {
		this(VirtualWorldDistribution.randomDistribution, userNb, 0, 0, mapSize, 0, 0, 0, layerId);
	}
	
//	private long[] normalize(long [] coords) {
//		for (int i=0; i < coords.length; i++) {
//			if (coords[i] > this.mapSize) {
//				coords[i] = this.mapSize;
//			}
//			if (coords[i] > this.mapSize) {
//				coords[i] = this.mapSize;
//			}
//		}
//		return coords;
//	}
	
	private void makeDistribution(int distributionKind) {
		
		int zoneId;
		long zoneBorder = this.mapSize - this.zoneSize;
		int zoneInhabitants = this.usrNb - this.outOfZoneNb;
		int smallZoneInhabitants, outOfZone;
		Random coin = new Random();
		VirtualEntity avatar;
		long [][] constraints = new long[3][2];
		long [] position;
		
		switch(distributionKind) {
		
			case VirtualWorldDistribution.scewedDistribution:
				/*assigning zone coordinates*/
				for(int i = 0; i < this.zoneNb; i++) {
					zoneCoords[i][0] = CommonState.r.nextLong(zoneBorder);
					zoneCoords[i][1] = CommonState.r.nextLong(zoneBorder);
					zoneCoords[i][2] = 0;
				}
				Globals.zoneCoords = zoneCoords;
				if(zoneInhabitants < 0) {
					System.err.println("Error: Incompatible parameters in VirtualWorldDistribution");
					System.exit(1);
				}
				
				/* populating high density zones*/
				for (int i = 0; i < zoneInhabitants; i++) {
					zoneId = coin.nextInt(this.zoneNb); //choose the zone to put the new user in.
					constraints[0][0] = zoneCoords[zoneId][0];
					constraints[0][1] = zoneCoords[zoneId][0] + this.zoneSize;
					constraints[1][0] = zoneCoords[zoneId][1];
					constraints[1][1] = zoneCoords[zoneId][1] + this.zoneSize;
					switch(Globals.law) {
					case VirtualWorldDistribution.GAUSSIAN:
						position = VirtualWorld.getConstraintedGaussianLocation(constraints);
						break;
					case VirtualWorldDistribution.ZIPF:
						position = VirtualWorld.getConstraintedZipfLocation(constraints);
						break;
					default:
						position = null;
						break;
					}
					avatar = new VirtualEntity(position);
					avatar.activate(zoneCoords[zoneId], this.zoneSize); 
					coords.put(avatar.getId(), avatar);
				}
				
				/* populating low density zones*/
				zoneBorder = this.mapSize - this.smallZoneSize;
				
				for(int i = 0; i < this.smallZoneNb; i++) {
					zoneId = coin.nextInt(this.zoneNb);
					if (Math.random() < 0.7) {
						smallZoneCoords[i][0] = CommonState.r.nextLong(zoneBorder);
						smallZoneCoords[i][1] = CommonState.r.nextLong(zoneBorder);
						smallZoneCoords[i][2] = 0;
					} else {
						smallZoneCoords[i][0] = zoneCoords[zoneId][0] + CommonState.r.nextLong(this.zoneSize);
						smallZoneCoords[i][1] = zoneCoords[zoneId][1] + CommonState.r.nextLong(this.zoneSize);
						smallZoneCoords[i][2] = 0;
					}
				}
				
				smallZoneInhabitants = this.outOfZoneNb *3 / 5;
				for (int i = 0; i < smallZoneInhabitants; i++) {
					zoneId = coin.nextInt(this.smallZoneNb); //choose the zone to put the new user in.
					constraints[0][0] = smallZoneCoords[zoneId][0];
					constraints[0][1] = smallZoneCoords[zoneId][0] + this.smallZoneSize;
					constraints[1][0] = smallZoneCoords[zoneId][1];
					constraints[1][1] = smallZoneCoords[zoneId][1] + this.smallZoneSize;
					switch(Globals.law) {
					case VirtualWorldDistribution.GAUSSIAN:
						position = VirtualWorld.getConstraintedGaussianLocation(constraints);
						break;
					case VirtualWorldDistribution.ZIPF:
						position = VirtualWorld.getConstraintedZipfLocation(constraints);
						break;
					default:
						position = null;
						break;
					}
//					position = VirtualWorld.normalize(VirtualWorld.getConstraintedGaussianLocation(constraints));
					avatar = new VirtualEntity(position);
					avatar.activate(smallZoneCoords[zoneId], this.smallZoneSize); 
					coords.put(avatar.getId(), avatar);
				}
				outOfZone = this.outOfZoneNb - smallZoneInhabitants;
				for (int i = 0; i < outOfZone; i++) {
					avatar = new VirtualEntity(VirtualWorld.getSimpleConstraintedRandomLocation(this.mapSize));
					coords.put(avatar.getId(), avatar);	 		
					avatar.activate(); 	
				}
				break;
				
			case VirtualWorldDistribution.randomDistribution:
				for (int i = 0; i < this.usrNb; i++) {
					avatar = new VirtualEntity(VirtualWorld.getSimpleConstraintedRandomLocation(this.mapSize));
					coords.put(avatar.getId(), avatar);
				}
				break;			
		}
	}
	
	public HashMap getDistribution() {
		return this.coords;
	}
	
	public VirtualEntity get(int id) {
		return this.coords.get(id);
	}
	
	public void animate() {
		Iterator it = coords.entrySet().iterator();
		VirtualEntity entity = null;
		int size;
		
		if (Globals.generated || (!Globals.generated && Globals.stepCount < Globals.slTrace.getTrace().size())) {
			while(it.hasNext()) {
				entity = (VirtualEntity)((Map.Entry)it.next()).getValue();
				if(Globals.HQRendering) {
					this.HQRendering = true;
					this.savedForHQ.put(entity.getId(), entity.getCoord().clone());
				}
				//			System.out.println("proceeding movement");
				if (Globals.topologyIsReady) {
					entity.proceedMovement();
				}
				//			System.out.println("propagating state info");
				entity.propagateStateInformation();
				//			System.out.println("keeping topo");
				entity.keepTopology();
				//			System.out.println("removing unwanted");
				entity.removeUnwantedNeighbors();
				//			System.out.println("end of animation");

			}
			if (!Globals.generated) {
				this.manageChurn();
				Globals.stepCount++;
			}
		}
	}

	private void manageChurn() {
		VirtualWorldDistributionShell step, newStep;
		HashMap<Integer, VirtualEntityInterface> users;
		Iterator it;
		VirtualEntityShell shell;
		VirtualEntity gone;
		int peersimNodeId, size;
		SolipsisProtocol current;

		newStep = Globals.slTrace.getTrace().get(Globals.stepCount);
		step    = Globals.slTrace.getTrace().get(Globals.stepCount-1);
		users   = step.getDistribution();
		it = newStep.getDistribution().entrySet().iterator();

//		System.out.println(users.size());
//		users   = step.getDistribution();
//		System.out.println(users.size());
//		System.exit(2);
		while (it.hasNext()) {
			shell = (VirtualEntityShell)((Map.Entry)it.next()).getValue();
			if (!users.containsKey(shell.getId())) {
				System.out.println("Joining"+shell.getId());
				this.joinPeer(shell);
			}
		}

//		if (this.coords.containsKey(282954)) System.exit(1);//err.println("step "+Globals.stepCount);
		it = step.getDistribution().entrySet().iterator();
		users   = newStep.getDistribution();
		while (it.hasNext()) {
			shell = (VirtualEntityShell)((Map.Entry)it.next()).getValue();
			if (!users.containsKey(shell.getId())) {
				gone = this.coords.remove(shell.getId());
//				System.err.println(Globals.stepCount+" "+shell.getId()+" "+gone);
				gone.getProtocol().disconnect();
				peersimNodeId = gone.getProtocol().getPeersimNodeId();
//				size = Network.size();
//				current = (SolipsisProtocol)Network.get(size-1).getProtocol(this.applicativeLayerId);
//				Network.remove(peersimNodeId);
//				current.updatePeersimNodeId(peersimNodeId);
				Network.get(peersimNodeId).setFailState(Fallible.DOWN);
			}
		}

	}

	private void joinPeer(VirtualEntityShell shell) {
		SolipsisProtocol entity;
		VirtualEntity avatar;
		
		avatar = new VirtualEntity(shell.getCoord());
		avatar.setId(shell.getId());
		entity = this.createNewPeer();
		entity.setVirtualEntity(avatar);
		entity.setPeersimNodeId(Network.size() - 1);
		avatar.setProtocol(entity);
		avatar.setOrder(Network.size());
		this.coords.put(avatar.getId(), avatar);
		entity.join(this.pickRandomNode());
//		System.out.println("joining "+entity.getVirtualEntity().getId());
	}

	public void joinPeer() {
		SolipsisProtocol entity;
		VirtualEntity avatar;
		
		avatar = new VirtualEntity(this.chooseLocation());
		
		entity = this.createNewPeer();
		entity.setVirtualEntity(avatar);
		entity.setPeersimNodeId(Network.size() - 1);
		avatar.setProtocol(entity);
		avatar.setOrder(Network.size());
		this.coords.put(avatar.getId(), avatar);
		entity.join(this.pickRandomNode());
		System.out.println("joining "+entity.getVirtualEntity().getId());
		
	}
	
	private long[] chooseLocation() {
		long[] location;
		
		location = new long[3];
		location[0] = VirtualWorld.randomCoord(0);
		location[1] = VirtualWorld.randomCoord(1);
		location[2] = VirtualWorld.randomCoord(2);
		
		return location;
	}

	private SolipsisProtocol createNewPeer() {
		Node newNode;
		SolipsisProtocol applicative;
		
		newNode = new GeneralNode("");
		Network.add(newNode);
		applicative = (SolipsisProtocol)Network.get(Network.size() - 1).getProtocol(this.applicativeLayerId);
		return applicative;
	}

	public long[] getFormerLocation(long id) {
		return this.savedForHQ.get(new Long(id));
	}
	
	public boolean HQRenderingAvailable() {
		return this.HQRendering;
	}
	
	public long getMapSize() {
		return this.mapSize;
	}
	
	public long[][] getZoneLocations() {
		return this.zoneCoords;
	}
	
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
	public void addToDistribution(VirtualEntity entity) {
		this.coords.put(entity.getId(), entity);
	}
	
	private SolipsisProtocol pickRandomNode() {
		SolipsisProtocol result;
		Random generator;
		Node randomNode;

		generator = new Random();
		randomNode = Network.get(generator.nextInt(Network.size()));
		result = (SolipsisProtocol)randomNode.getProtocol(this.applicativeLayerId);
		
		return result;
	}
}
