package peersim.solipsis;

import java.util.LinkedList;

import peersim.solipsis.Globals;
import peersim.solipsis.NeighborProxy;
import peersim.solipsis.VirtualEntityInterface;
import peersim.solipsis.VirtualWorld;

public class GiftWrapping {
	private LinkedList set;
	private VirtualEntityInterface mainEntity;
	private int setSize;
	private boolean error;
	Object origin;
	
	public GiftWrapping(LinkedList set, VirtualEntityInterface entity) {
//		LinkedList newSet = new LinkedList();
//		int size;
//		boolean veto;
		this.mainEntity = entity;
		this.setSize = set.size();
//		Object candidate;
//		size = 0;
//		
//		for (int i = 0; i < this.setSize; i++) {
//			candidate = set.get(i);
//			veto = false;
//			for (int j  = 0; j < size; j++) {
//				if (samePosition(newSet.get(j),candidate)) {
//					veto = true;
//					break;
//				}
//			}
//			
//			if (!veto) {
//				newSet.add(candidate);
//				size++;
//			}
//		}
		this.error = false;
		this.set = set;
//		this.setSize = size;
	}
	
	private Object findEnvelopeMember() {
		Object currentObject;
		VirtualEntityInterface currentEntity;
		NeighborProxy currentProxy;
		Object member = null;
		
		long yValue, xValue = Long.MIN_VALUE;
		long max = Long.MIN_VALUE;
		long [] localViewCoord;
		boolean regular;
		for (int i = 0; i < this.setSize; i++) {
		 	currentObject = set.get(i);
		 	
		 	if (currentObject instanceof NeighborProxy) {
		 		currentProxy = (NeighborProxy)currentObject;
		 		if (currentProxy.getQuality() != NeighborProxy.REGULAR) {
		 			regular = false;
		 		} else {
		 			regular = true;
		 		}
			 	localViewCoord = this.mainEntity.subjectiveCoord(currentProxy.getId());
		 	} else {
		 		currentEntity = (VirtualEntityInterface)currentObject;
//		 		System.err.println(currentEntity);
		 		if (this.mainEntity.getQualityOf(currentEntity.getId()) != NeighborProxy.REGULAR) {
		 			regular = false;
		 		} else {
		 			regular = true;
		 		}
			 	localViewCoord = this.mainEntity.relativeCoord(currentEntity);
		 	}
		 	if (regular) {
		 		yValue = localViewCoord[1];
		 		if(max < yValue) {
		 			max = yValue;
		 			xValue = localViewCoord[0];
		 			member = currentObject;
		 		} else if (max == yValue) {
		 			if (xValue < localViewCoord[0]) {
		 				xValue = localViewCoord[0];
		 				member = currentObject;
		 			}
		 		}
		 	}
		}
			
		return member;
	}
	
//	private double calculateAngle(VirtualEntityInterfaceInterface x, VirtualEntityInterfaceInterface y) {
//		double angle = VirtualWorld.calculateDirection(x.getCoord(), y.getCoord());
//		if (angle < 0) {
//			angle += 2*Math.PI; // we want the angle to be in the [0,2Pi] interval, instead of [-Pi,Pi]
//		}
//		return angle;
//	}
//	
	private boolean inList(Object entity, LinkedList set) {
		boolean in = false;
		int size = set.size();
		for(int i = 0; i < size; i++) {
			if (set.get(i) == entity) {
				in = true;
				break;
			}
		}
		return in;
	}
	
	private boolean leftOf(Object a, Object b, Object entity) {
		long [] realA, realB, realEntity;
		if (a instanceof NeighborProxy) {
			realA = this.mainEntity.subjectiveCoord(((NeighborProxy)a).getId());
			realB = this.mainEntity.subjectiveCoord(((NeighborProxy)b).getId());
			realEntity = this.mainEntity.subjectiveCoord(((NeighborProxy)entity).getId());
		} else {
			realA = this.mainEntity.relativeCoord((VirtualEntityInterface)a);
			realB = this.mainEntity.relativeCoord((VirtualEntityInterface)b);
			realEntity = this.mainEntity.relativeCoord((VirtualEntityInterface)entity);
		}
		return VirtualWorld.simpleAngleSign(realA, realB, realEntity) > 0;
	}
	
	private boolean aligned(Object a, Object b, Object entity) {
		long [] realA, realB, realEntity;
		if (a instanceof NeighborProxy) {
			realA = this.mainEntity.subjectiveCoord(((NeighborProxy)a).getId());
			realB = this.mainEntity.subjectiveCoord(((NeighborProxy)b).getId());
			realEntity = this.mainEntity.subjectiveCoord(((NeighborProxy)entity).getId());
		} else {
			realA = this.mainEntity.relativeCoord((VirtualEntityInterface)a);
			realB = this.mainEntity.relativeCoord((VirtualEntityInterface)b);
			realEntity = this.mainEntity.relativeCoord((VirtualEntityInterface)entity);
		}
		return VirtualWorld.simpleAngleSign(realA, realB, realEntity) == 0;
	}
	
	private boolean isRegular(Object a) {
		boolean regular;
		
	 	if (a instanceof NeighborProxy) {
	 		if (((NeighborProxy)a).getQuality() != NeighborProxy.REGULAR) {
	 			regular = false;
	 		} else {
	 			regular = true;
	 		}
	 	} else {
	 		if (this.mainEntity.getQualityOf(((VirtualEntityInterface)a).getId()) != NeighborProxy.REGULAR) {
	 			regular = false;
	 		} else {
	 			regular = true;
	 		}
	 	}
	 	
	 	return regular;
	}

	private Object findNextMember(LinkedList envelope) {
		Object current;
		Object lastAdded = envelope.getLast();
		Object next = lastAdded;
		boolean regular;
		for (int i = 0; i < this.setSize; i++) {
			current = this.set.get(i);
		 	if (isRegular(current) && !samePosition(current, lastAdded)) {
		 		if (next == lastAdded) {
		 			next = current;
		 		} else {
		 			if (this.leftOf(lastAdded, next, current)) {
		 				next = current;
		 			}
		 		}
		 		if (error) {
					if (next == origin)
						System.err.println("next is origin");
					
				}
//		 		break;
		 	}
		}
		
		if (error) {
			if (next == lastAdded) {
				System.err.println("next still == last added");
			}
		}
		
//		System.out.println("last added= "+lastAdded+" set = "+this.set);
		for (int i = 0; i < this.setSize; i++) {
			current = this.set.get(i);
			if (error) {
				if (current == origin) {
					NeighborProxy entity;
					System.err.println("current is origin "+ isRegular(current) +" "+ (next!=current) +" "+ !samePosition(current, lastAdded)+ " "+ this.leftOf(lastAdded, next, current));
					entity = (NeighborProxy)lastAdded;
					System.err.println(entity.getId()+" : "+this.mainEntity.relativeCoord(entity.getCoord())[0]+","+this.mainEntity.relativeCoord(entity.getCoord())[1]);
					entity = (NeighborProxy)next;
					System.err.println(entity.getId()+" : "+this.mainEntity.relativeCoord(entity.getCoord())[0]+","+this.mainEntity.relativeCoord(entity.getCoord())[1]);
					entity = (NeighborProxy)current;
					System.err.println(entity.getId()+" : "+this.mainEntity.relativeCoord(entity.getCoord())[0]+","+this.mainEntity.relativeCoord(entity.getCoord())[1]);
				} else {
					System.err.println("count");
				}
			}
			if (isRegular(current) && next!=current && current != lastAdded && !samePosition(current, lastAdded)) {//!inList(current,envelope)&&next!=current) {
				if(this.leftOf(lastAdded, next, current)) {
					next = current;
				}
			}
		}
		
//		if (samePosition(next, lastAdded)) {
//			System.out.println(lastAdded + " " + next);
//			System.exit(1);
//		}
		
		return next;
	}
	
	private boolean samePosition(Object a, Object b) {
		boolean answer;
	 	if (a instanceof NeighborProxy) {
	 		answer = VirtualWorld.samePosition(this.mainEntity.subjectiveCoord(((NeighborProxy)a).getId()), this.mainEntity.subjectiveCoord(((NeighborProxy)b).getId()));
	 	} else {
	 		answer = VirtualWorld.samePosition(this.mainEntity.relativeCoord(((VirtualEntityInterface)a).getCoord()), this.mainEntity.relativeCoord(((VirtualEntityInterface)b).getCoord()));
	 	}
	 	return answer;
	}
	
	public LinkedList findEnvelope() {
		Object origin = this.findEnvelopeMember();
		Object old,current = origin;
		LinkedList envelope = null;
		this.origin = origin;
		
		if(origin != null) {
			envelope = new LinkedList();
			envelope.add(origin);
			int loop = 0;
			while(true) {
				old = envelope.getLast();
				current = this.findNextMember(envelope);//current);
				if(current == origin || samePosition(current,origin)) {
					break;
				}
				envelope.add(current);
				loop++;
				if (loop > 10000) {
					this.error = true;
					this.origin = origin;
					System.err.println("begin"+this.setSize);
					NeighborProxy entity = (NeighborProxy)old;
					System.err.println(entity.getId()+" : "+this.mainEntity.relativeCoord(entity.getCoord())[0]+","+this.mainEntity.relativeCoord(entity.getCoord())[1]);
					 entity = (NeighborProxy)current;
					System.err.println(entity.getId()+" : "+this.mainEntity.relativeCoord(entity.getCoord())[0]+","+this.mainEntity.relativeCoord(entity.getCoord())[1]);
					System.err.println(this.inList(origin, this.set));
					System.err.println("Origin: "+((NeighborProxy)origin).getId()+" "+this.mainEntity.subjectiveCoord(((NeighborProxy)origin).getId())[0]+" "+this.mainEntity.subjectiveCoord(((NeighborProxy)origin).getId())[1]);
					System.err.println(this.mainEntity.getCoord()[0]+" "+this.mainEntity.getCoord()[1]);
					System.err.println(this.leftOf(old, entity, origin)+" "+isRegular(origin));
					System.err.println("end"+this.set.size());
					try {
					if (loop > 10020)
						throw new Exception("GiftWrapping loop");
					} catch(Exception e) {
						e.printStackTrace();
						System.exit(123);
					}
				}
			}
		}
		return envelope;
	}
}
