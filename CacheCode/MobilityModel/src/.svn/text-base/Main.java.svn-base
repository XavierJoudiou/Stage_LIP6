

import java.io.IOException;

import mobilityModel.EndOfTraceException;
import mobilityModel.MobilityModel;

public class Main {

	/**
	 * example of use
	 */
	public static void main(String[] args) {
		try {
			MobilityModel test = new MobilityModel("model.cfg", true);
			test.animate();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EndOfTraceException e) {
			System.err.println("La simulation est terminée");
		}

	}

}
