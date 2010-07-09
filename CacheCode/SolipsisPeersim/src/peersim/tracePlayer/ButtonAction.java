package peersim.tracePlayer;

import javax.swing.plaf.basic.BasicButtonListener;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;

public class ButtonAction extends BasicButtonListener {
	
	public final static int PLAY  = 0;
	public final static int FWD   = 1;
	public final static int BCK   = 2;
	public final static int PAUSE = 3;
	public final static int STOP  = 4;
	public final static int TOPO  = 5;
	
	private int type;
	private TraceDisplayer player;
	
	public ButtonAction(AbstractButton button, TraceDisplayer player, int type) {
		super(button);
		this.type = type;
		this.player = player;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		switch(this.type) {
		case PLAY:
//			System.out.println("play");
			this.player.play();
			break;
		case FWD:
			this.player.forward();
//			System.out.println("fwd");
			break;
		case BCK:
			this.player.backwards();
//			System.out.println("back");
			break;
		case PAUSE:
//			System.out.println("pause");
			this.player.pause();
			break;
		case STOP:
//			System.out.println("stop");
			this.player.stop();
			break;
		case TOPO:
//			System.out.println("Toggle Topology");
			this.player.toggleTopology();
		default:
			break;	
		}
	}

}
