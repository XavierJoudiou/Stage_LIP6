package mobilityModel;

public class EndOfTraceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	EndOfTraceException() {
		super("EndOfTrace.");
	}
	
	EndOfTraceException(String msg) {
		super(msg);
	}
}
