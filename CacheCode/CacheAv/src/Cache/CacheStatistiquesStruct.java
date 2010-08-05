package Cache;

public class CacheStatistiquesStruct {

	private double topoCoherence;
	private double viewCoherence;
	private int msgCount;
	private long connecDuration;
	private double aheadCounter;
	
	
	
	public CacheStatistiquesStruct() {
		super();
	}




	public CacheStatistiquesStruct(double topoCoherence, double viewCoherence,
			int msgCount, long connecDuration, double aheadCounter) {
		super();
		this.topoCoherence = topoCoherence;
		this.viewCoherence = viewCoherence;
		this.msgCount = msgCount;
		this.connecDuration = connecDuration;
		this.aheadCounter = aheadCounter;
	}
	
	
	
	
	public void moy( CacheStatistiquesStruct curs){
		CacheStatistiquesStruct res = new CacheStatistiquesStruct();
		
		this.setAheadCounter( (this.getAheadCounter() + curs.getAheadCounter())/2 );
		this.setConnecDuration((this.getConnecDuration() + curs.getConnecDuration())/2 );
		this.setMsgCount( (this.getMsgCount() + curs.getMsgCount())/2 );
		this.setTopoCoherence( (this.getTopoCoherence() + curs.getTopoCoherence())/2 );
		this.setViewCoherence( (this.getViewCoherence() + curs.getViewCoherence())/2 );
	}
	
	
	
	

	@Override
	public String toString() {
		return "CacheStatistiquesStruct [aheadCounter=" + aheadCounter
				+ ", connecDuration=" + connecDuration + ", msgCount="
				+ msgCount + ", topoCoherence=" + topoCoherence
				+ ", viewCoherence=" + viewCoherence + "]";
	}




	public double getTopoCoherence() {
		return topoCoherence;
	}

	public void setTopoCoherence(double topoCoherence) {
		this.topoCoherence = topoCoherence;
	}

	public double getViewCoherence() {
		return viewCoherence;
	}

	public void setViewCoherence(double viewCoherence) {
		this.viewCoherence = viewCoherence;
	}

	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}

	public long getConnecDuration() {
		return connecDuration;
	}

	public void setConnecDuration(long connecDuration) {
		this.connecDuration = connecDuration;
	}

	public double getAheadCounter() {
		return aheadCounter;
	}

	public void setAheadCounter(double aheadCounter) {
		this.aheadCounter = aheadCounter;
	}
	
	
	
	
}
