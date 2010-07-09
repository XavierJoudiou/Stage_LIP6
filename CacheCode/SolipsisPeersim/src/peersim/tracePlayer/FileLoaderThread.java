package peersim.tracePlayer;


public class FileLoaderThread extends Thread {
	
	private TraceReader reader;
	
	FileLoaderThread(TraceReader reader) {
		super();
		this.reader = reader;
	}
	
	@Override
	public void run() {
		System.out.println("New thread Started");
		reader.loadAllSteps();
		this.reader.finish();
		this.reader.getBuffer().fileLoaded();

	}
}
