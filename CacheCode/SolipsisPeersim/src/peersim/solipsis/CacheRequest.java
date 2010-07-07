/**
 * 
 */
package peersim.solipsis;

/**
 * @author xavier
 *
 */
public class CacheRequest {

	private NeighborProxy oldData;
	
//	private NeighborProxy source;

		
	public CacheRequest(NeighborProxy oldData) {
		super();
		this.oldData = oldData;
//		this.source = source;
	}


	/* ********************************************************************* */
							/* Getters & Setters */
	/* ********************************************************************* */


	public NeighborProxy getOldData() {
		return oldData;
	}	
	
	public void setOldData(NeighborProxy oldData) {
		this.oldData = oldData;
	}

//	public NeighborProxy getSource() {
//		return source;
//	}
//
//	public void setSource(NeighborProxy source) {
//		this.source = source;
//	}
	
	
	
}
