/**
 * 
 */
package peersim.solipsis;

/**
 * @author xavier
 *
 */
public class CacheData {

	private int Position;
	private int others;
	
	
	
	public CacheData(int position, int others) {
		super();
		Position = position;
		this.others = others;
	}
	
	
	
	public int getPosition() {
		return Position;
	}
	public void setPosition(int position) {
		Position = position;
	}
	public int getOthers() {
		return others;
	}
	public void setOthers(int others) {
		this.others = others;
	}



	@Override
	public String toString() {
		return "CacheData [Position=" + Position + ", others=" + others + "]";
	}
	
	
}
