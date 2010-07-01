/**
 * 
 */
package cache.utils;

import java.util.Arrays;

/**
 * @author xavier
 *
 */
public class Node {
	

	private long[] nodeCoord;
	private int id;
	private int quality;

	private int cacheInfo;
	private int cachePos;
	


	public Node(long[] nodeCoord, int id, int quality) {
		super();
		this.nodeCoord = nodeCoord;
		this.id = id;
		this.quality = quality;
		this.cachePos = -1;
		this.cacheInfo = -1;
	}

	public void IncPos(){
		this.cachePos ++;
	}

	public void DecPos(){	
		if (cachePos > 0){
			this.cachePos --;
		}
	}
	
	public void IncInfo(int nb){
		this.cacheInfo+= nb;
	}

	public void DecInfo(){	
		if (cacheInfo > 0 ){
			this.cacheInfo --;
		}
	}

	/* Setters & Getters */

	public long[] getNodeCoord() {
		return nodeCoord;
	}
	public void setNodeCoord(long[] nodeCoord) {
		this.nodeCoord = nodeCoord;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public int getCacheInfo() {
		return cacheInfo;
	}
	public void setCacheInfo(int cacheInfo) {
		this.cacheInfo = cacheInfo;
	}
	public void setCachePos(int cachePos) {
		this.cachePos = cachePos;
	}

	public int getCachePos() {
		return cachePos;
	}


	@Override
	public String toString() {
		return "Node [cacheInfo=" + cacheInfo + ", cachePos=" + cachePos
				+ ", id=" + id + ", nodeCoord=" + Arrays.toString(nodeCoord)
				+ ", quality=" + quality + "]";
	}




}
