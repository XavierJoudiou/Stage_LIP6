/**
 * 
 */
package peersim.solipsis;

/**
 * @author xavier
 *
 */
public class CacheStatistics {
	
	
	private int nbCaheMissGlob;
	private int nbCaheHitGlob;
	
	
	CacheStatistics() {
		this.nbCaheMissGlob = 0;
		this.nbCaheHitGlob = 0;
	}
	
	
	public void incCacheMissGlob(){
		this.nbCaheMissGlob ++;
	}
	
	public void incCacheHitGLob(){
		this.nbCaheHitGlob ++;
	}
	

	public void printStatistics() {
		// TODO Auto-generated method stub
//		System.out.println("test");
		System.out.println(" " + this.nbCaheHitGlob +":" + this.nbCaheMissGlob + ", ");
		
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


	
	

}
