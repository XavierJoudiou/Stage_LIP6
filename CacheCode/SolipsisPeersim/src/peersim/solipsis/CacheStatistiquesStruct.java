package peersim.solipsis;

public class CacheStatistiquesStruct {

	private double topoCoherence;
	private double viewCoherence;
	private int msgCount;
	private double connecDuration;
	private double aheadCounter;
	private int step;
	
	
	
	public CacheStatistiquesStruct() {
		super();
		this.step = 0;
	}




	public CacheStatistiquesStruct(double topoCoherence, double viewCoherence,
			int msgCount, double connecDuration, double aheadCounter) {
		super();
		this.topoCoherence = topoCoherence;
		this.viewCoherence = viewCoherence;
		this.msgCount = msgCount;
		this.connecDuration = connecDuration;
		this.aheadCounter = aheadCounter;
		this.step = 0;
	}
	
	
	public void moy( CacheStatistiquesStruct curs){
		CacheStatistiquesStruct res = new CacheStatistiquesStruct();
		
		this.setAheadCounter( (this.getAheadCounter() + curs.getAheadCounter()));
		this.setConnecDuration((this.getConnecDuration() + curs.getConnecDuration()));
		this.setMsgCount( (this.getMsgCount() + curs.getMsgCount()) );
		this.setTopoCoherence( (this.getTopoCoherence() + curs.getTopoCoherence()));
		this.setViewCoherence( (this.getViewCoherence() + curs.getViewCoherence()));
		this.step ++;
	}
	
	
	public String PrintStats(){
		String res = "" + this.viewCoherence/step + " " + this.topoCoherence/step + " " + this.msgCount/step + " " + this.aheadCounter/step + " " + this.connecDuration/step + "\n";
		return res;
	}
	
	public String PrintStatsFinal(){
		String res = "" + this.viewCoherence + " " + this.topoCoherence + " " + this.msgCount + " " + this.aheadCounter + " " + this.connecDuration + "\n";
		return res;
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

	public double getConnecDuration() {
		return connecDuration;
	}

	public void setConnecDuration(double connecDuration) {
		this.connecDuration = connecDuration;
	}

	public double getAheadCounter() {
		return aheadCounter;
	}

	public void setAheadCounter(double aheadCounter) {
		this.aheadCounter = aheadCounter;
	}
	
	
	
	
}
