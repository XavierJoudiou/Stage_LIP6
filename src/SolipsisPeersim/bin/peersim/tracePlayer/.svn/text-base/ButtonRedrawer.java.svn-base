package peersim.tracePlayer;

import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;

import peersim.solipsis.VirtualWorldMonitor;

public class ButtonRedrawer implements WindowStateListener {

	private VirtualWorldMonitor monitor;
	
	public ButtonRedrawer(VirtualWorldMonitor monitor) {
		this.monitor = monitor;
	}
	
	public void windowStateChanged(WindowEvent arg0) {
		monitor.drawPanel();
	}
	
//	public void focusGained(FocusEvent e) {
//		monitor.drawPanel();
//	}
//	
//	public void focusLost(FocusEvent e) {
//	}

}
