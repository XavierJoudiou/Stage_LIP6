/**
 * 
 */
package peersim.solipsis;

import java.io.FileWriter;
import java.io.IOException;

import peersim.core.CommonState;

/**
 * @author xavier
 *
 */
public class CacheStatistics {
	
	
	private int nbCaheMissGlob;
	private int nbCaheHitGlob;
	private int nbCacheMitClob;
	private int nbCachePassClob;
	

	CacheStatistics() {
		this.nbCaheMissGlob = 0;
		this.nbCaheHitGlob = 0;
		this.nbCacheMitClob = 0;
		this.nbCachePassClob = 0;
	}
	
	
	public void incCacheMissGlob(){
		this.nbCaheMissGlob ++;
	}
	
	public void incCacheHitGLob(){
		this.nbCaheHitGlob ++;
	}
	
	public void incCacheMitGLob(){
		this.nbCacheMitClob ++;
	}
	
	public void incnbCachePassClob(){
		this.nbCachePassClob ++;
	}
	
	public void printStatistics() {
		// TODO Auto-generated method stub
//		System.out.println("test");
		System.out.println(" " + this.nbCaheHitGlob +":" + this.nbCacheMitClob +":" + this.nbCaheMissGlob + ":"+ this.nbCachePassClob + ", ");
		
	}
	
	public String printStatistics2() {
		// TODO Auto-generated method stub
//		System.out.println("test");
		String res;
		res = "" + this.nbCaheHitGlob + " " + this.nbCacheMitClob + " " + this.nbCaheMissGlob + " " + this.nbCachePassClob + " " + CommonState.getTime() + "\n";
//		res = "" + this.nbCaheHitGlob + " " + CommonState.getTime() + "\n";

		return res;
		
	}


	public int getNbCaheMissGlob() {
		return nbCaheMissGlob;
	}


	public void setNbCaheMissGlob(int nbCaheMissGlob) {
		this.nbCaheMissGlob = nbCaheMissGlob;
	}


	public int getNbCaheHitGlob() {
		return nbCaheHitGlob;
	}


	public void setNbCaheHitGlob(int nbCaheHitGlob) {
		this.nbCaheHitGlob = nbCaheHitGlob;
	}


	public void setNbCacheMitClob(int nbCacheMitClob) {
		this.nbCacheMitClob = nbCacheMitClob;
	}


	public int getNbCacheMitClob() {
		return nbCacheMitClob;
	}

	public int getNbCachePassClob() {
		return nbCachePassClob;
	}


	public void setNbCachePassClob(int nbCachePassClob) {
		this.nbCachePassClob = nbCachePassClob;
	}

	
	

}
