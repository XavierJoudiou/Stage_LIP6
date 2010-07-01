package peersim.solipsis;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonListener;

import java.awt.*;

import peersim.solipsis.Globals;
import peersim.solipsis.NeighborProxy;
import peersim.solipsis.VirtualEntityInterface;

import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import peersim.tracePlayer.ButtonAction;
import peersim.tracePlayer.TraceDisplayer;
import peersim.tracePlayer.InteractiveInterface;
import peersim.tracePlayer.ButtonRedrawer;
//import java.awt.event.FocusListener;

public class VirtualWorldMonitor extends JFrame {
	
	public final static int ENTITY_SIZE = 1;

	private VirtualWorldDistributionInterface distribution;
	private int screenSize;
	private boolean topology;
	private boolean debug;
	private int xoffset;
	private int yoffset;
	private TraceDisplayer player;
	private int polygon;
	private int panelSize;
	private Graphics panelGraphics;
	private Graphics gr;
	private Graphics colored;
	private boolean playerMode;
	private boolean blinker;
	private int blinkingCounter;
	private boolean fileLoaded;
	private boolean once;
	private int overallSteps;

	public VirtualWorldMonitor(VirtualWorldDistributionInterface vwd, TraceDisplayer player) {
		this(vwd);
		this.player = player;
		this.playerMode = true;
		this.blinker = false;
		this.blinkingCounter = 0;
		this.fileLoaded = false;
		this.once = true;
		this.overallSteps = 0;
		this.createPanel();
	}
	
	public VirtualWorldMonitor(VirtualWorldDistributionInterface vwd) {
		super();
		this.playerMode = false;
		this.distribution = vwd;
		this.panelSize = (vwd instanceof VirtualWorldDistribution)?0:120;
		this.screenSize = Globals.screenSize;// + 4*Globals.offset;
		/*
		 * displaying the topology with high quality is too complicated...
		 */
		this.topology = (Globals.HQRendering&Globals.drawTopology)?false:Globals.drawTopology;
		this.debug = Globals.debug;
		this.xoffset = 0;//Globals.offset+(Globals.offset/2);
		this.yoffset = 0;//3*Globals.offset;
		this.polygon = 0;
		build();
	}

	private void build(){
		setTitle("VirtualWorldMap"); 
		setSize(this.screenSize+2*this.xoffset, this.screenSize+this.panelSize);
		setLocationRelativeTo(null);
		setResizable(false); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    	setVisible(true);
		this.gr = this.getGraphics().create(0, 0, this.screenSize+2*this.xoffset, this.screenSize);
		this.gr.setColor(Color.black);
	}
	
	public void drawPanel() {
		this.panelGraphics.drawLine(0, this.screenSize, this.screenSize, this.screenSize);
		this.drawButtons();
//		this.drawLoading();
	}
	
	private void createPanel() {
		this.createButtons();
	}
	
	private void drawButtons() {
		Container content = getContentPane();
		Component[] components = content.getComponents();
		int size = components[0].getWidth();
//		int overallSize = 0;
//		for (int i = 0; i < components.length; i++) {
//			overallSize += components[i].getWidth();
//		}
//		System.out.println("overallSize="+overallSize);
		content.setBounds(this.screenSize/2 - size*5/2 - components[5].getWidth(), this.screenSize, this.screenSize, this.panelSize);
		if (!content.isVisible()) {
			content.setVisible(true);
		}
		content.repaint();
	}
	
	public void setTopology(boolean value) {
		this.topology = value;
	}
	
	public VirtualWorldDistributionInterface getDistribution() {
		return this.distribution;
	}
	
	public void fileLoaded() {
		this.fileLoaded = true;
	}
	
	public boolean isLoaded() {
		return this.fileLoaded;
	}
	
	public void drawCurrentStep(int step) {
		String msg = "Showing step "+ step + "/";
		if (this.overallSteps == 0) {
			msg += "??";
		} else {
			msg += this.overallSteps;
		}
		int msgLen = msg.length();
//		if (panelGraphics == null) {
//			panelGraphics = this.getGraphics();
			Graphics panel = this.getGraphics();
			panel.clearRect(90, this.screenSize+10, 60, 13);
			panel.drawChars(msg.toCharArray(), 0, msgLen,10,this.screenSize+20);
//		}		
	}
	
	public void drawLoading(int steps) {
		String msg = (this.fileLoaded)?"File successfully loaded":"Loading data from file (" + steps + " steps)...";
		int msgLen = msg.length();
		if (panelGraphics == null) {
			panelGraphics = this.getGraphics();
		}
		Color old = panelGraphics.getColor();
		if (!this.fileLoaded) {
			this.blinkingCounter++;
			if (this.blinkingCounter == 5) { 
				this.blinker = !blinker;
				this.blinkingCounter = 0;
			}	
			if (this.blinker) {
				panelGraphics.setColor(Color.RED);
				while(panelGraphics.getColor()!=Color.RED);
			}
			this.panelGraphics.clearRect(200, this.screenSize+10, 400, 20);
			panelGraphics.drawChars(msg.toCharArray(), 0, msgLen,this.screenSize/2 - 4*(msgLen-7),this.screenSize+20);
			if (this.blinker) {
				panelGraphics.setColor(old);
				blinker = !blinker;
			}	
		} else {
			if (once) {
				this.overallSteps = steps;
				this.panelGraphics.clearRect(0, this.screenSize+10, this.screenSize, 20);
				panelGraphics.setColor(Color.GREEN);
				while(panelGraphics.getColor()!=Color.GREEN);
				panelGraphics.drawChars(msg.toCharArray(), 0, msgLen,this.screenSize/2 - 4*(msgLen-7),this.screenSize+20);
				once = false;
				panelGraphics.setColor(Color.BLACK);
				while(panelGraphics.getColor()!=Color.BLACK);
			}
//			this.panelGraphics.setColor(Color.GRAY);
//			while(panelGraphics.getColor()!=Color.GRAY);
//			this.panelGraphics.fillRect(15, this.screenSize+10, this.screenSize-30, 4);
//			panelGraphics.setColor(old);
		}
	}
	
	private void createButtons() {
		Container content = getContentPane();
		String topo = "Toggle Topology";
		ButtonRedrawer redrawer = new ButtonRedrawer(this);
		this.addMouseListener(new InteractiveInterface(this));		
		this.addWindowStateListener(redrawer);
//		this.addFocusListener((FocusListener)redrawer);
		content.setLayout(null);//new FlowLayout());
		JButton button1 = new JButton(new ImageIcon("images/play.jpg"));
		JButton button2 = new JButton(new ImageIcon("images/pause.jpg"));
		JButton button3 = new JButton(new ImageIcon("images/stop.jpg"));
		JButton button4 = new JButton(new ImageIcon("images/forward.jpg"));
		JButton button5 = new JButton(new ImageIcon("images/backward.jpg"));
		JButton button6 = new JButton(topo);
		int size = button1.getIcon().getIconWidth();
		button1.setSize(size, size);
		button2.setSize(size, size);
		button3.setSize(size, size);
		button4.setSize(size, size);
		button5.setSize(size, size);
		button6.setSize(topo.length()*12, 30);
		button1.setLocation(2*size, 0);
		button2.setLocation(3*size, 0);
		button3.setLocation(size, 0);
		button4.setLocation(4*size, 0);
		button5.setLocation(0, 0);
		button6.setLocation(6*size, size/2 - 20);
	    content.add(button5);
	    content.add(button1);
	    content.add(button2);
	    content.add(button3);
	    content.add(button4);
	    content.add(button6);
	    button1.addMouseListener(new ButtonAction(button1,this.player,ButtonAction.PLAY));
	    button2.addMouseListener(new ButtonAction(button2,this.player,ButtonAction.PAUSE));
	    button3.addMouseListener(new ButtonAction(button3,this.player,ButtonAction.STOP));
	    button4.addMouseListener(new ButtonAction(button4,this.player,ButtonAction.FWD));
	    button5.addMouseListener(new ButtonAction(button5,this.player,ButtonAction.BCK));
	    button6.addMouseListener(new ButtonAction(button1,this.player,ButtonAction.TOPO));
	}
	
	public void setDistribution(VirtualWorldDistributionInterface vwd) {
		this.distribution =vwd;
	}

	public void showVirtualWorld() {
		if(!Globals.HQRendering) {
			this.gr.clearRect(0, 0, this.screenSize, this.screenSize);

		}
		if (this.playerMode) {
			if (panelGraphics == null) {
				panelGraphics = this.getGraphics();
			}
			this.drawPanel();
		}
		this.paint(this.gr);
	}
	
	public void clearScreen() {
		this.gr.clearRect(0, 0, this.screenSize+2*this.xoffset, this.screenSize);
	}
	
	private boolean harmsInterface(int y) {
		
		return y > this.screenSize;
	}
	
	private int[] normalizeCoords(int x1, int y1, int x2, int y2) {
		int[] n = new int[4];
		int calcul;
		boolean firstPb = harmsInterface(y1);
		boolean secondPb = harmsInterface(y2);
		
		if(firstPb && secondPb) {
			n[0] = n[1] = n[2] = n[3] = -1;
		} else {
			if (firstPb) {
				calcul = x1*y2 - x2*y1 - (x1 - x2)*this.screenSize;

				if (calcul < 0 || calcul > this.screenSize) {
					n[0] = calcul;
					n[1] = this.screenSize;
				} else {
					n[0] = x1;
					n[1] = y1;			
				}
			} else {
				n[0] = x1;
				n[1] = y1;
			}

			if (secondPb) {
				calcul = (x1*y2 - x2*y1 - (x1 - x2)*this.screenSize) / (y2 - y1);

				if (calcul >= 0 && calcul <= this.screenSize) {
					n[2] = calcul;
					n[3] = this.screenSize;
				} else {
					n[2] = x2;
					n[3] = y2;			
				}
			} else {
				n[2] = x2;
				n[3] = y2;
			}
		}
		
		return n;
	}
	
	private void drawLine(int x1, int y1, int x2, int y2) {
//		if (this.playerMode) {
//			int[] n = normalizeCoords(x1,y1,x2,y2);
//			this.gr.drawLine(n[0], n[1], n[2], n[3]);
//		} else {
			this.gr.drawLine(this.xoffset + x1, this.yoffset + y1, this.xoffset + x2, this.yoffset + y2);
//		}
	}
	
	private void drawLine(int x1, int y1, int x2, int y2, Color color) {
//		try {
//		while(this.gr.getColor()==Color.blue)
//			Thread.sleep(10);
//		} catch (Exception e) {}
		if(this.colored == null) {
			this.colored = this.getGraphics();
		}
		Color old = this.colored.getColor();
		this.gr.setColor(color);
		this.gr.drawLine(this.xoffset + x1, this.yoffset + y1, this.xoffset + x2, this.yoffset + y2);
		this.gr.setColor(old);
	}
	
	private void drawRect(int x, int y, int l, int h) {
//		Graphics gr = this.getGraphics();
		this.gr.drawRect(this.xoffset+x, this.yoffset+y, l, h);
	}
	
	private void drawRect(int x, int y, int l, int h, Color color) {
//		Graphics gr = this.getGraphics();
		Color old = this.gr.getColor();
		this.gr.setColor(color);
		this.drawRect(this.xoffset+x, this.yoffset+y, l, h);
		this.gr.setColor(old);
	}
	
	private void clearRect(int x, int y, int l, int h) {
		this.gr.clearRect(this.xoffset+x, this.yoffset+y, l, h);
	}
	
	private void drawCircle(int [] center, int ray, Color color) {
//		Graphics gr = this.getGraphics();
		Color old = this.gr.getColor();
		this.gr.setColor(color);
		this.gr.drawOval(this.xoffset+center[0]-ray, this.yoffset+center[1]-ray, ray*2, ray*2);
		this.gr.setColor(old);
	}

	public void updateVirtualWorld() {
		HashMap<Integer, VirtualEntityInterface> coords = this.distribution.getDistribution();
		Iterator it = coords.entrySet().iterator();
		VirtualEntityInterface entity = null;
		long[] coord;
		int[] screenCoord;
		while(it.hasNext()) {
			entity = (VirtualEntityInterface)((Map.Entry)it.next()).getValue();
			coord = entity.getCoord();
			screenCoord = virtualCoordToScreenCoord(coord);

			if (Globals.HQRendering 
					&& this.distribution.HQRenderingAvailable() 
					&& entity.doesMove()) {
				
				this.eraseEntityLocation(entity.getId());
			}
			//this.surface.drawRect(screenCoord[0], screenCoord[1], ENTITY_SIZE, ENTITY_SIZE);
			this.drawRect(screenCoord[0], screenCoord[1], ENTITY_SIZE, ENTITY_SIZE);
			if (entity.isFull()) {
				if(this.topology) {
					this.drawTopology(entity);
				} else {
					if(entity.getId() == this.polygon) {
						this.drawConvexPolygon(entity);
						this.drawLocalView(entity);
						this.drawKnowledgeZone(entity);
					}
				}
			}
		}	
		
	}
	private void eraseEntityLocation(long id) {
		long[] former = this.distribution.getFormerLocation(id);
		int[] pixelCoord = this.virtualCoordToScreenCoord(former);
		this.clearRect(pixelCoord[0], pixelCoord[1], ENTITY_SIZE+1, ENTITY_SIZE+1); //???
	}
	
//	private long [] adjustCoordToModulo(VirtualEntityInterface subject, VirtualEntityInterface object) {		
//		return subject.relativeCoord(object);
//	}

	private void drawTopology(VirtualEntityInterface entity) {
		LinkedList<VirtualEntityInterface> neighbors = (LinkedList<VirtualEntityInterface>)entity.getNeighbors();
		int  [] entityCoord = virtualCoordToScreenCoord(entity.getCoord());
		long [] currentCoord;
		int  [] screenCurrentCoord, reverseCoord;
		VirtualEntityInterface current;

		for(int i = 0; i < neighbors.size(); i++) {
			current = neighbors.get(i);
			if (entity.getQualityOf(current.getId()) == NeighborProxy.REGULAR) {
				currentCoord = entity.relativeCoord(current);
				screenCurrentCoord = virtualCoordToScreenCoord(currentCoord);
				this.drawLine(entityCoord[0], entityCoord[1], screenCurrentCoord[0], screenCurrentCoord[1]);

				if(currentCoord[0] != current.getCoord()[0] || currentCoord[1] != current.getCoord()[1]) {
					reverseCoord = this.virtualCoordToScreenCoord(current.getCoord());
					currentCoord = current.relativeCoord(entity);
					screenCurrentCoord = this.virtualCoordToScreenCoord(currentCoord);
					this.drawLine(reverseCoord[0], reverseCoord[1], screenCurrentCoord[0], screenCurrentCoord[1]);
				}
			}
		}
//		System.out.println("drawTopology: size="+neighbors.size());
	}
	
	public void setPolygonDrawing(int order) {
		this.topology = false;
		this.polygon = order;
	}
	
	private long[] relativeOffsetsCalculation(VirtualEntityInterface ref, VirtualEntityInterface modif, VirtualEntityInterface arg) {
		long[] relative = ref.relativeCoord(arg);
		long[] translation = new long[2];
		
		translation[0] = relative[0] - arg.getCoord()[0];
		translation[1] = relative[1] - arg.getCoord()[1];
		
		relative = ref.relativeCoord(modif);
		relative[0] -= translation[0];
		relative[1] -= translation[1];
		
		return relative;
	}
	
	private void drawConvexPolygon(VirtualEntityInterface entity) {
		LinkedList<VirtualEntityInterface> polygon = entity.getConvexEnvelope();
		VirtualEntityInterface current, next;
		int [] screenCurrentCoord, screenNextCoord, currentCoord;
		int size;
		long [] mapCurrentCoord, mapNextCoord;
		currentCoord = this.virtualCoordToScreenCoord(entity.getCoord());
		this.drawRect(currentCoord[0], currentCoord[1], ENTITY_SIZE+1, ENTITY_SIZE+1, Color.RED);
		if (polygon != null) {
			size = polygon.size();
			for (int i = 0; i < size; i++) {
				current = polygon.get(i);
				next = polygon.get((i+1)%size);
				mapCurrentCoord = entity.relativeCoord(current);
				mapNextCoord = entity.relativeCoord(next);
				screenCurrentCoord = this.virtualCoordToScreenCoord(mapCurrentCoord);
				screenNextCoord = this.virtualCoordToScreenCoord(mapNextCoord);
				this.drawLine(screenCurrentCoord[0],screenCurrentCoord[1],screenNextCoord[0],screenNextCoord[1],Color.RED);
				/*reverse*/
				if ((mapNextCoord[0] != next.getCoord()[0]) || (mapNextCoord[1] != next.getCoord()[1]) ) {
					mapCurrentCoord = this.relativeOffsetsCalculation(entity, current, next);
					screenNextCoord = this.virtualCoordToScreenCoord(next.getCoord());
					screenCurrentCoord = this.virtualCoordToScreenCoord(mapCurrentCoord);
					this.drawLine(screenCurrentCoord[0],screenCurrentCoord[1],screenNextCoord[0],screenNextCoord[1],Color.RED);
				} 
				if ((mapCurrentCoord[0] != current.getCoord()[0]) || (mapCurrentCoord[1] != current.getCoord()[1]) ) {
					mapNextCoord = this.relativeOffsetsCalculation(entity, next, current);
					screenNextCoord = this.virtualCoordToScreenCoord(next.getCoord());
					screenCurrentCoord = this.virtualCoordToScreenCoord(mapCurrentCoord);
					this.drawLine(screenCurrentCoord[0],screenCurrentCoord[1],screenNextCoord[0],screenNextCoord[1],Color.RED);
				} 
			}
		}
	}
	
//	private long[] obtainModuloCoefs(long [] origin, long [] modulo) {
//		long [] coefs = new long[2];
//		coefs[0] = (modulo[0] - origin[0]) / Globals.mapSize;
//		coefs[1] = (modulo[1] - origin[1]) / Globals.mapSize;
//		return coefs;
//	}
//	
	
	private void drawLocalView(VirtualEntityInterface entity) {
		LinkedList<VirtualEntityInterface> neighbors = entity.getNeighbors();
		int [] coord = this.virtualCoordToScreenCoord(entity.getCoord());
		long [] mapNeighborCoord, mapEntityCoord;
		int [] screenNeighborCoord, screenEntityCoord;
		VirtualEntityInterface current;
		
		for (int i = 0; i < neighbors.size(); i++) {
			current = neighbors.get(i);
			mapEntityCoord = entity.getCoord();
			mapNeighborCoord = entity.relativeCoord(current);
			screenNeighborCoord = this.virtualCoordToScreenCoord(mapNeighborCoord);
			screenEntityCoord = this.virtualCoordToScreenCoord(mapEntityCoord);
			if (entity.getQualityOf(current.getId()) == NeighborProxy.REGULAR) {
				this.drawLine(screenEntityCoord[0], screenEntityCoord[1], screenNeighborCoord[0], screenNeighborCoord[1]);
			} else if (entity.getQualityOf(current.getId()) == NeighborProxy.LONGRANGE){
				this.drawLine(screenEntityCoord[0], screenEntityCoord[1], screenNeighborCoord[0], screenNeighborCoord[1], Color.ORANGE);
			} else {
				this.drawLine(screenEntityCoord[0], screenEntityCoord[1], screenNeighborCoord[0], screenNeighborCoord[1], Color.GREEN);
			}
			
			if(mapNeighborCoord[0] != current.getCoord()[0] || mapNeighborCoord[1] != current.getCoord()[1]) {
				mapNeighborCoord = current.getCoord();
				mapEntityCoord = current.relativeCoord(entity);
				screenNeighborCoord = this.virtualCoordToScreenCoord(mapNeighborCoord);
				screenEntityCoord = this.virtualCoordToScreenCoord(mapEntityCoord);			
				if (entity.getQualityOf(current.getId()) == NeighborProxy.REGULAR) {
					this.drawLine(screenEntityCoord[0], screenEntityCoord[1], screenNeighborCoord[0], screenNeighborCoord[1]);
				} else if (entity.getQualityOf(current.getId()) == NeighborProxy.LONGRANGE){
					this.drawLine(screenEntityCoord[0], screenEntityCoord[1], screenNeighborCoord[0], screenNeighborCoord[1], Color.ORANGE);
				} else {
					this.drawLine(screenEntityCoord[0], screenEntityCoord[1], screenNeighborCoord[0], screenNeighborCoord[1], Color.GREEN);
				}
			}
			
//			if(mapEntityCoord[0] != entity.getCoord()[0] || mapEntityCoord[1] != entity.getCoord()[1]) {
//				mapNeighborCoord = current.getCoord();
//				mapEntityCoord = current.relativeCoord(entity);
//				screenNeighborCoord = this.virtualCoordToScreenCoord(mapNeighborCoord);
//				screenEntityCoord = this.virtualCoordToScreenCoord(mapEntityCoord);
//				this.drawLine(screenNeighborCoord[0], screenNeighborCoord[1], screenEntityCoord[0], screenEntityCoord[1]);
//			}
		}
	}
	
	private void drawKnowledgeZone(VirtualEntityInterface entity) {
		int [] coord = this.virtualCoordToScreenCoord(entity.getCoord());
		int radius = virtualDistToScreenDist(entity.getKnowledgeRay());
		this.drawCircle(coord,radius,Color.BLUE);
		int xleft  = coord[0] - radius;
		int xright = coord[0] + radius;
		int ydown  = coord[1] - radius;
		int yup    = coord[1] + radius;
		int[] modCoord = new int[2];
		
		if (yup > this.screenSize) {
			modCoord[0] = coord[0];
			modCoord[1] = coord[1] - this.screenSize;
			this.drawCircle(modCoord,radius,Color.BLUE);
		}
		
		if (ydown < 0) {
			modCoord[0] = coord[0];
			modCoord[1] = coord[1] + this.screenSize;
			this.drawCircle(modCoord,radius,Color.BLUE);
		}
		
		if (xright > this.screenSize) {
			modCoord[0] = coord[0] - this.screenSize;
			modCoord[1] = coord[1];
			this.drawCircle(modCoord,radius,Color.BLUE);
		}
		
		if (xleft < 0) {
			modCoord[0] = coord[0] + this.screenSize;
			modCoord[1] = coord[1];
			this.drawCircle(modCoord,radius,Color.BLUE);
		}
		
	}

	public void paint(Graphics g) {
		this.updateVirtualWorld();
	}

	public void topology(boolean ok) {
		this.topology = ok;
	}

	public void debug(boolean ok) {
		this.debug = ok;
	}
	
//	public int getScreenSize() {
//		return this.screenSize;
//	}
//	public int getScreenSize() {
//		return this.screenSize;
//	}

	private int virtualDistToScreenDist(double dist) {
		long mapSize = this.distribution.getMapSize();
		int distPerPixel = (int) (mapSize / Globals.screenSize);
		return (int) (dist / distPerPixel);
	}

	public int[] virtualCoordToScreenCoord(long[] coord) {
		int [] screenCoord = new int[3];
		long mapSize = this.distribution.getMapSize();
		int distPerPixel = (int) (mapSize / Globals.screenSize);
		screenCoord[0] = (int) (coord[0] / distPerPixel);
		screenCoord[1] = (int) (coord[1] / distPerPixel);
		screenCoord[2] = (int) (coord[2] / distPerPixel);
		return screenCoord;
	}

}
