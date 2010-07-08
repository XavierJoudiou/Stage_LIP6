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
	private int nbCacheMitClob;
	
	
	CacheStatistics() {
		this.nbCaheMissGlob = 0;
		this.nbCaheHitGlob = 0;
		this.nbCacheMitClob = 0;
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
	

	public void printStatistics() {
		// TODO Auto-generated method stub
//		System.out.println("test");
		System.out.println(" " + this.nbCaheHitGlob +":" + this.nbCacheMitClob +":"+ this.nbCaheMissGlob + ", ");
		
	}
	
	public String printStatistics2() {
		// TODO Auto-generated method stub
//		System.out.println("test");
		String res;
		res = " " + this.nbCaheHitGlob +":" + this.nbCacheMitClob +":"+ this.nbCaheMissGlob + ", ";
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


	
	

}
