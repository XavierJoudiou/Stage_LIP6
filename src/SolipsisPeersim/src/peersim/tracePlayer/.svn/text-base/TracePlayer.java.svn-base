package peersim.tracePlayer;

public class TracePlayer {
	public static void main (String []args) {
		String filename;
		FileLoaderThread secondThread = null;
		StepBuffer synchronizedBuffer = new StepBuffer();
		
		if (args.length == 0) {
			System.err.println("Where is the trace file?");
			System.exit(1);
		}
		
		filename = args[0];
		TraceReader reader = null;
		TraceDisplayer display = null; 
		System.out.println("Trace File is '" + filename + "', loading...");

		try {
			reader = new TraceReader(filename, synchronizedBuffer);
			display = new TraceDisplayer(reader);
			reader.setDisplay(display);
			secondThread = new FileLoaderThread(reader);
		} catch (Exception e) {
			if(peersim.solipsis.Globals.debug) {
				e.printStackTrace();
			} else {
				System.err.println("The trace file is corrupted");
			}
			System.exit(1);
		}
		secondThread.start();
		while(display.hasNext()) {
			display.displayNextStep();
		}
		System.out.println("Trace is over");
	}
}
